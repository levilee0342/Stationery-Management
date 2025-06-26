import os

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

import random
import json
import pickle
import numpy as np

import nltk
from nltk.stem import WordNetLemmatizer

from tensorflow.keras.models import load_model
from database_helper import  *

def clean_up_sentence(sentence):
    lemmatizer = WordNetLemmatizer()

    sentence_words = nltk.word_tokenize(sentence)
    sentence_words = [lemmatizer.lemmatize(word) for word in sentence_words]

    return sentence_words


def bag_of_words(sentence):
    words = pickle.load(open('model/words.pkl', 'rb'))

    sentence_words = clean_up_sentence(sentence)
    bag = [0] * len(words)
    for w in sentence_words:
        for i, word in enumerate(words):
            if word == w:
                bag[i] = 1
    return np.array(bag)


def predict_class(sentence):
    classes = pickle.load(open('model/classes.pkl', 'rb'))
    model = load_model('model/chatbot_model.keras', compile=False)

    bow = bag_of_words(sentence)
    res = model.predict(np.array([bow]))[0]
    ERROR_THRESHOLD = 0.95

    results = [[i, r] for i, r in enumerate(res) if r > ERROR_THRESHOLD]
    results.sort(key=lambda x: x[1], reverse=True)

    return_list = []

    for r in results:
        return_list.append({'intent': classes[r[0]], 'probability': str(r[1])})

    return return_list


def get_list_products():
    products = query_products()
    if not products:
        return "<p>❌ Hiện tại không có sản phẩm nào hoặc có lỗi khi truy vấn dữ liệu.</p>"

    # Định dạng HTML
    product_list = "".join([
        f"""
        <div class="product-item">
            <h3>{p['name']}</h3>
            <p><strong>📌 Category:</strong> {p['category_name']}</p>
            <p><strong>📝 Description:</strong> {p['description']}</p>
        </div>
        """ for p in products
    ])

    return f"""
    <div class="product-list">
        {product_list}
    </div>
    """


def get_response(intents_list, message):
    with open('intents.json', encoding='utf-8') as f:
        intents_json = json.load(f)

    if not intents_list:
        return "❌ Sorry, I didn't understand that. Could you please rephrase?"

    tag = intents_list[0]['intent']

    if tag == "list_products":
        product_list = get_list_products()
        response_template = random.choice(
            [resp for resp in intents_json['intents'] if resp['tag'] == tag][0]['responses']
        )
        return response_template.replace("{product_list}", product_list)

    for i in intents_json['intents']:
        if i['tag'] == tag:
            return random.choice(i['responses'])

    return "❌ Sorry, I don't have an appropriate response at the moment."









