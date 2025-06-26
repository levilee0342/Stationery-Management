import json
import logging
import os
import sqlite3

from dotenv import load_dotenv
from flask import Flask, request, jsonify, render_template, redirect, url_for
from flask_cors import CORS

from utils import get_response, predict_class
import continuous_learning as cl
from rag_langchain import init_vectorstore, answer_query

app = Flask(__name__, template_folder='templates')
CORS(app, resources={r"/*": {"origins": "*"}})
logging.debug("Flask app is starting...")
cl.init_db()
API_URL = os.getenv(
        "PRODUCTS_API_URL",
        "http://localhost:8080/api/products/get-all-products-for-chatbot"
    )
if os.environ.get("WERKZEUG_RUN_MAIN") == "true":
    store = init_vectorstore(API_URL)
@app.route('/')
def index():
    return render_template('index.html')

@app.route('/handle_message', methods=['POST'])
def handle_message():
    data = request.json
    message = data['message']

    user_id = data.get('user_id', 'anonymous')


    result = predict_class(message)

    if result:
        intent = result[0]['intent']
        confidence = float(result[0]['probability'])
        raw_response = get_response(result, message)
    else:
        #hits = retrieve(message)
        #prompt = build_prompt(message, hits)

        pre_raw_response = answer_query(message,store)
        raw_response = format_response(pre_raw_response)
        intent = None
        confidence = 0.0


    cl.log_interaction(message, raw_response, user_id=None)
    return jsonify({'response': raw_response})

@app.route('/review')
def review():
    """
    Lấy tất cả interaction CHƯA được human review (corrected_intent IS NULL).
    """
    interactions = cl.fetch_all_messages()
    with open(cl.INTENTS_PATH, encoding='utf-8') as f:
        intents_data = json.load(f)
    tags = [intent['tag'] for intent in intents_data['intents']]
    return render_template('review.html', interactions=interactions, intents=tags)

@app.route('/submit_corrections', methods=['POST'])
def submit_corrections():
    """
    Chỉ xử lý những interaction mà checkbox 'select_<id>' đã được tick.
    Tên của intent chọn là 'intent_<id>'; nếu chọn 'other' thì lấy 'other_intent_<id>' và
    thêm vào intents.json.
    """
    form = request.form.to_dict()
    corrections = {}
    new_responses = {}

    for key in form:
        if key.startswith('select_'):
            try:
                interaction_id = int(key.split('_', 1)[1])
            except ValueError:
                continue

            intent_key = f'intent_{interaction_id}'
            intent_value = form.get(intent_key)

            new_response_key = f'new_response_{interaction_id}'
            new_response_value = form.get(new_response_key, '').strip()

            if intent_value == 'other':
                new_tag = form.get(f'other_intent_{interaction_id}', '').strip()
                if new_tag:
                    corrections[interaction_id] = new_tag
                    if new_response_value:
                        new_responses[interaction_id] = new_response_value
            else:
                corrections[interaction_id] = intent_value
                if new_response_value:
                    new_responses[interaction_id] = new_response_value

    if corrections:
        cl.update_intents(corrections, new_responses=new_responses)

    return redirect(url_for('review'))


# ———————— REVIEW GEMINI ————————

@app.route('/review_gemini')
def review_gemini():
    """
    Lấy interaction mà Gemini đánh dấu mismatch (gemini_flag = 1)
    và corrected_intent IS NULL.
    """
    items = cl.fetch_uncorrected_messages()
    with open(cl.INTENTS_PATH, encoding='utf-8') as f:
        intents_data = json.load(f)
    tags = [intent['tag'] for intent in intents_data['intents']]
    return render_template('review_gemini.html', interactions=items, intents=tags)

@app.route('/submit_gemini_corrections', methods=['POST'])
def submit_gemini_corrections():
    """
    Tương tự submit_corrections, nhưng chỉ xử lý với những interaction
    mà 'select_<id>' được tick trên giao diện review_gemini.
    """
    form = request.form.to_dict()
    corrections = {}

    for key in form:
        if key.startswith('select_'):
            try:
                interaction_id = int(key.split('_', 1)[1])
            except ValueError:
                continue

            intent_key = f'intent_{interaction_id}'
            intent_value = form.get(intent_key)

            if intent_value == 'other':
                new_tag = form.get(f'other_intent_{interaction_id}', '').strip()
                if new_tag:
                    corrections[interaction_id] = new_tag
            else:
                corrections[interaction_id] = intent_value

    if corrections:
        cl.update_intents(corrections)

    return redirect(url_for('review_gemini'))

def format_response(raw_response):
    lines = raw_response.split('\n')
    html = '<div class="formatted-response">'
    in_list = False

    for line in lines:
        line = line.strip()
        if not line:
            if in_list:
                html += '</ul>'
                in_list = False
            continue

        if line.startswith('**') and line.endswith('**'):
            if in_list:
                html += '</ul>'
                in_list = False
            html += f'<h3>{line[2:-2]}</h3>'
        elif line.startswith('*'):
            if not in_list:
                html += '<ul>'
                in_list = True
            html += f'<li>{line[2:]}</li>'
        elif line.startswith('```php'):
            if in_list:
                html += '</ul>'
                in_list = False
            code_content = []
            for next_line in lines[lines.index(line) + 1:]:
                if next_line.startswith('```'):
                    break
                code_content.append(next_line)
            html += '<pre><code>' + '\n'.join(code_content) + '</code></pre>'
        else:
            if in_list:
                html += '</ul>'
                in_list = False
            html += f'<p>{line}</p>'

    if in_list:
        html += '</ul>'
    html += '</div>'

    html = html.replace('**', '')
    return html


if __name__ == '__main__':
    port = 5000
    host = '0.0.0.0'
    print(f"\n🚀 Review interface available at:")
    print(f"👉 http://127.0.0.1:{port}/review")
    print(f"👉 http://127.0.0.1:{port}/review_gemini")
    app.run(host=host, debug=True)



