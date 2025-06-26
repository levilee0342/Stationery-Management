from tensorflow.keras.layers import InputLayer

@classmethod
def _patched_from_config(cls, config):
    config.pop('batch_shape', None)
    return cls(**config)

InputLayer.from_config = _patched_from_config
from flask import Flask, request, jsonify, render_template

from utils import get_response, predict_class
from rag_langchain import init_rag, retrieve, build_prompt, ask_llm

app = Flask(__name__, template_folder='templates')


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
        # hits = retrieve(message)
        # prompt = build_prompt(message, hits)
        # raw_response = ask_llm(prompt)
        raw_response = "Hehe"
        intent = None
        confidence = 0.0

    return jsonify({'response': raw_response})


# curl -X POST http://127.0.0.1:5000/handle_message -d '{"message":"what is coding"}' -H "Content-Type: application/json"


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
