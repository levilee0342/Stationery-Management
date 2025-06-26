from flask import Flask, request, jsonify
import tensorflow as tf
import numpy as np
import cv2
import os
from flask_cors import CORS

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "*"}})

# Load model và labels như bạn đã làm ở trên
interpreter = tf.lite.Interpreter(model_path='product_classifier.tflite')
interpreter.allocate_tensors()
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

with open('labels.txt', 'r') as f:
    class_labels = [line.strip() for line in f.readlines()]

def preprocess_image(image_path, target_size=(224, 224)):
    img = cv2.imread(image_path)
    img = cv2.resize(img, target_size)
    img = img.astype('float32') / 255.0
    img = np.expand_dims(img, axis=0)
    return img

@app.route('/')
def index():
    return None

@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return jsonify({'error': 'No image uploaded'}), 400
    file = request.files['image']
    filepath = f"./uploads/{file.filename}"
    os.makedirs('./uploads', exist_ok=True)
    file.save(filepath)

    img = preprocess_image(filepath)
    interpreter.set_tensor(input_details[0]['index'], img)
    interpreter.invoke()
    output_data = interpreter.get_tensor(output_details[0]['index'])[0]
    predicted_index = np.argmax(output_data)
    confidence = float(output_data[predicted_index])
    label = class_labels[predicted_index]

    return jsonify({'label': label, 'confidence': confidence})

if __name__ == '__main__':
    port = 5050  # Đổi port tại đây
    host = '0.0.0.0'
    app.run(host=host, port=port, debug=True)

