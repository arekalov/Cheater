from bs4 import BeautifulSoup
import json
import re

def parse_questions(html_file_path, output_file_path):
    with open(html_file_path, 'r', encoding='utf-8') as file:
        soup = BeautifulSoup(file, 'html.parser')
    
    questions = []
    
    # Находим все блоки вопросов
    question_blocks = soup.find_all('div', class_=re.compile(r'QuestionDisplay_question__\w+'))
    
    for block in question_blocks:
        # Извлекаем ID вопроса
        question_header = block.find('div')
        if question_header:
            id_match = re.search(r'Вопрос (\d+)', question_header.text)
            question_id = int(id_match.group(1)) if id_match else None
        else:
            question_id = None
        
        # Извлекаем текст вопроса
        question_text_elem = block.find_all('div')[1] if len(block.find_all('div')) > 1 else None
        question_text = question_text_elem.text.strip() if question_text_elem else ""
        
        # Извлекаем изображения из текста вопроса
        images = []
        if question_text_elem:
            img_tags = question_text_elem.find_all('img')
            for img in img_tags:
                img_src = img.get('src', '')
                # Заменяем обратные слеши на прямые для JSON
                img_src = img_src.replace('\\', '/')
                images.append(img_src)
                # Удаляем теги img из текста вопроса
                img.extract()
            # Обновляем текст вопроса после удаления img
            question_text = question_text_elem.text.strip()
        
        # Находим все варианты ответов
        answer_divs = block.find_all('div', class_=re.compile(r'QuestionDisplay_answer__\w+'))
        
        # Проверяем, есть ли неправильные ответы (без класса correct)
        has_wrong_answers = any('correct' not in div.get('class', []) for div in answer_divs if isinstance(div.get('class'), list))
        
        answers = []
        if has_wrong_answers:
            # Если есть неправильные ответы, берем ТОЛЬКО правильные
            for div in answer_divs:
                classes = div.get('class', [])
                if isinstance(classes, list) and any('correct' in c for c in classes):
                    answers.append(div.text.strip())
        else:
            # Если все ответы правильные, берем их все
            for div in answer_divs:
                answers.append(div.text.strip())
        
        # Генерируем ключевые слова из текста вопроса
        keywords = generate_keywords(question_text)
        
        # Создаем объект вопроса
        question_obj = {
            "text": question_text,
            "images": images,
            "answers": answers,
            "keywords": keywords,
            "id": question_id,
            "category": "nets"  # Категория по умолчанию, можно изменить при необходимости
        }
        
        questions.append(question_obj)
    
    # Сохраняем в JSON файл
    with open(output_file_path, 'w', encoding='utf-8') as json_file:
        json.dump(questions, json_file, ensure_ascii=False, indent=2)
    
    print(f"Сохранено {len(questions)} вопросов в {output_file_path}")
    return questions

def generate_keywords(text):
    """Генерирует ключевые слова из текста вопроса"""
    # Удаляем специальные символы и разбиваем на слова
    words = re.findall(r'[а-яА-Яa-zA-Z0-9]+', text.lower())
    
    # Убираем дубликаты, сохраняя порядок
    seen = set()
    unique_words = []
    for word in words:
        if word not in seen and len(word) > 1:  # Игнорируем слишком короткие слова
            seen.add(word)
            unique_words.append(word)
    
    return unique_words

# Запуск парсинга
if __name__ == "__main__":
    parse_questions('questions.html', 'questions.json')