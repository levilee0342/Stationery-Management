import tensorflow as tf

# Load mô hình đã train (giả sử là mô hình Keras)
model = tf.keras.models.load_model('product_classifier.tflite')

# Khởi tạo TFLiteConverter
converter = tf.lite.TFLiteConverter.from_keras_model(model)

# Cấu hình để sử dụng toán tử TFLite tiêu chuẩn
converter.target_spec.supported_ops = [
    tf.lite.OpsSet.TFLITE_BUILTINS,  # Toán tử TFLite tiêu chuẩn
    tf.lite.OpsSet.SELECT_TF_OPS     # Toán tử TensorFlow bổ sung (bao gồm FULLY_CONNECTED)
]

# Tùy chọn tối ưu hóa (nếu cần)
converter.optimizations = [tf.lite.Optimize.DEFAULT]

# Chuyển đổi mô hình
tflite_model = converter.convert()

# Lưu mô hình TFLite
with open('product_classifier.tflite', 'wb') as f:
    f.write(tflite_model)