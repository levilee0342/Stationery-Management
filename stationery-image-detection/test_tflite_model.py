import tensorflow as tf
print("TensorFlow version:", tf.__version__)
import numpy as np
import cv2  # Hoặc dùng PIL nếu bạn không muốn dùng OpenCV
import os

# Load labels
with open('labels.txt', 'r') as f:
    class_labels = [line.strip() for line in f.readlines()]

# Load TFLite model
interpreter = tf.lite.Interpreter(model_path='product_classifier.tflite')
interpreter.allocate_tensors()

# Lấy thông tin input/output
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# Hàm tiền xử lý ảnh
def preprocess_image(image_path, target_size=(224, 224)):
    img = cv2.imread(image_path)
    img = cv2.resize(img, target_size)
    img = img.astype('float32') / 255.0  # Normalize
    img = np.expand_dims(img, axis=0)  # Add batch dimension
    return img

# Hàm dự đoán
def predict_image(image_path):
    img = preprocess_image(image_path)
    interpreter.set_tensor(input_details[0]['index'], img)
    interpreter.invoke()
    output_data = interpreter.get_tensor(output_details[0]['index'])[0]
    predicted_index = np.argmax(output_data)
    confidence = output_data[predicted_index]
    label = class_labels[predicted_index]
    return label, confidence

# Test với 1 ảnh mẫu
test_image_path = './test_images/img.png'  # Thay bằng đường dẫn ảnh của bạn
if not os.path.exists(test_image_path):
    raise FileNotFoundError(f"Test image not found: {test_image_path}")

label, confidence = predict_image(test_image_path)
print(f"Dự đoán: {label} ({confidence * 100:.2f}%)")
