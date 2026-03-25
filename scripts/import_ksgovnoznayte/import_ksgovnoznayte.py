#!/usr/bin/env python3
"""
Parse saved ksgovnoznayte HTML (Save Page As) into Cheater AppData JSON.
Expects HTML + *_files/ folder under repo root `nets/` by default.
"""

from __future__ import annotations

import argparse
import json
import re
import shutil
import sys
from pathlib import Path

from bs4 import BeautifulSoup, NavigableString

CATEGORY_ID = "nets"
CATEGORY_NAME = "Сети передачи данных"

# Russian + latin letters for keywords
WORD_RE = re.compile(r"[a-zA-Zа-яА-ЯёЁ0-9]+", re.UNICODE)
ANSWER_PREFIX_RE = re.compile(r"^(\d+)\s*>\s*(.*)$", re.DOTALL)
WRONG_LINE_RE = re.compile(r"^\d+\s*>.+$", re.DOTALL)


def normalize_src_to_basename(src: str) -> str:
    src = src.strip()
    if not src:
        return ""
    # ./ksgovnoznayte.netlify.app_files/foo.JPG -> foo.JPG
    name = Path(src.replace("\\", "/")).name
    return name


def extract_answers(body: BeautifulSoup) -> list[str]:
    out: list[str] = []
    for b in body.find_all("b"):
        t = b.get_text(separator=" ", strip=True)
        t = re.sub(r"</a_\d+>\s*$", "", t, flags=re.IGNORECASE)
        t = re.sub(r"/a_\d+\s*$", "", t, flags=re.IGNORECASE)
        m = ANSWER_PREFIX_RE.match(t)
        if m:
            out.append(m.group(2).strip())
        elif t:
            out.append(t)
    return out


def extract_question_text(body: BeautifulSoup) -> str:
    clone = BeautifulSoup(str(body), "html.parser").body
    if not clone:
        return ""
    for b in clone.find_all("b"):
        b.decompose()
    for s in list(clone.strings):
        raw = str(s)
        stripped = raw.strip()
        if not stripped:
            continue
        if WRONG_LINE_RE.match(stripped):
            s.replace_with("")
    text = clone.get_text(separator="\n", strip=True)
    lines = [ln.strip() for ln in text.splitlines() if ln.strip()]
    return "\n".join(lines)


def extract_image_paths(body: BeautifulSoup) -> list[str]:
    seen: list[str] = []
    done: set[str] = set()
    for img in body.find_all("img"):
        src = img.get("src") or ""
        base = normalize_src_to_basename(src)
        if not base or base in done:
            continue
        done.add(base)
        seen.append(f"nets/{base}")
    return seen


def keywords_from_text(text: str) -> list[str]:
    words = [w.lower() for w in WORD_RE.findall(text)]
    return [w for w in words if len(w) >= 3]


def split_blocks(html: str) -> list[str]:
    parts = re.split(r"<hr\s*/?>", html, flags=re.IGNORECASE)
    return [p.strip() for p in parts if p.strip()]


def parse_args() -> argparse.Namespace:
    root = Path(__file__).resolve().parents[2]
    p = argparse.ArgumentParser(description="Import ksgovnoznayte HTML to questions_nets.json")
    p.add_argument(
        "--html",
        type=Path,
        default=root / "nets" / "ksgovnoznayte.netlify.app.html",
        help="Path to saved HTML",
    )
    p.add_argument(
        "--images-src",
        type=Path,
        default=None,
        help="Folder with images (default: sibling *_files next to HTML)",
    )
    p.add_argument(
        "--assets-dir",
        type=Path,
        default=root / "app" / "src" / "main" / "assets",
        help="Android assets root",
    )
    p.add_argument(
        "--out-json",
        type=Path,
        default=None,
        help="Output JSON (default: assets-dir/questions_nets.json)",
    )
    p.add_argument("--dedupe", action="store_true", help="Drop duplicate question texts")
    return p.parse_args()


def main() -> int:
    args = parse_args()
    html_path: Path = args.html
    if not html_path.is_file():
        print(f"HTML not found: {html_path}", file=sys.stderr)
        return 1

    images_src: Path = args.images_src or html_path.parent / (
        html_path.stem + "_files"
    )
    if not images_src.is_dir():
        print(f"Images folder not found: {images_src}", file=sys.stderr)
        return 1

    assets: Path = args.assets_dir
    out_json: Path = args.out_json or (assets / "questions_nets.json")
    images_out = assets / "images" / "nets"

    raw = html_path.read_text(encoding="utf-8", errors="replace")
    blocks = split_blocks(raw)

    questions_raw: list[dict] = []
    for part in blocks:
        frag = BeautifulSoup(f"<body>{part}</body>", "html.parser")
        body = frag.body
        if not body:
            continue
        answers = extract_answers(body)
        if not answers:
            continue
        text = extract_question_text(body)
        if not text.strip():
            continue
        images = extract_image_paths(body)
        questions_raw.append(
            {
                "text": text,
                "images": images,
                "answers": answers,
                "keywords": keywords_from_text(text),
            }
        )

    if args.dedupe:
        seen: set[str] = set()
        deduped: list[dict] = []
        for q in questions_raw:
            key = re.sub(r"\s+", " ", q["text"].strip().lower())
            if key in seen:
                continue
            seen.add(key)
            deduped.append(q)
        questions_raw = deduped

    for i, q in enumerate(questions_raw, start=1):
        q["id"] = i
        q["category"] = CATEGORY_ID

    app_data = {
        "categories": [{"id": CATEGORY_ID, "name": CATEGORY_NAME}],
        "questions": questions_raw,
    }

    images_out.mkdir(parents=True, exist_ok=True)
    for f in images_src.iterdir():
        if f.is_file():
            shutil.copy2(f, images_out / f.name)

    missing: list[str] = []
    for q in questions_raw:
        for rel in q["images"]:
            name = Path(rel).name
            if not (images_out / name).is_file():
                missing.append(name)

    if missing:
        print(f"Warning: {len(missing)} referenced images missing after copy", file=sys.stderr)

    out_json.parent.mkdir(parents=True, exist_ok=True)
    out_json.write_text(
        json.dumps(app_data, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )

    print(f"Wrote {len(questions_raw)} questions -> {out_json}")
    print(f"Images -> {images_out} ({len(list(images_out.iterdir()))} files)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
