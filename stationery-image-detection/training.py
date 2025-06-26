# Import các thư viện cần thiết
import tensorflow as tf
print("TensorFlow version:", tf.__version__)
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.layers import Dense, GlobalAveragePooling2D, Dropout
from tensorflow.keras.models import Model
import os
import matplotlib.pyplot as plt
import numpy as np

# Đường dẫn dataset (cùng cấp với training.py)
dataset_path = './training_dataset'

# Kiểm tra xem dataset có tồn tại không
if not os.path.exists(dataset_path):
    raise FileNotFoundError(f"Dataset path {dataset_path} does not exist. Please check the directory.")

# Kiểm tra số lượng ảnh trong mỗi thư mục
for class_name in os.listdir(dataset_path):
    class_path = os.path.join(dataset_path, class_name)
    if os.path.isdir(class_path):
        num_images = len(os.listdir(class_path))
        print(f"{class_name}: {num_images} images")
        if num_images < 10:
            print(f"Warning: {class_name} has only {num_images} images. Consider adding more for better training.")

# Tăng cường dữ liệu
train_datagen = ImageDataGenerator(
    rescale=1./255,  # Chuẩn hóa pixel về [0,1]
    rotation_range=20,  # Xoay ngẫu nhiên ±20 độ
    width_shift_range=0.2,  # Dịch ngang ±20%
    height_shift_range=0.2,  # Dịch dọc ±20%
    shear_range=0.2,  # Biến dạng ±20%
    zoom_range=0.2,  # Phóng to/thu nhỏ ±20%
    horizontal_flip=True,  # Lật ngang
    brightness_range=[0.8, 1.2],  # Thay đổi độ sáng ±20%
    fill_mode='nearest',
    validation_split=0.2  # 20% dữ liệu cho validation
)

# Tải dữ liệu huấn luyện
try:
    train_generator = train_datagen.flow_from_directory(
        dataset_path,
        target_size=(224, 224),  # Resize về 224x224
        batch_size=32,  # 32 ảnh mỗi batch
        class_mode='categorical',  # Phân loại đa lớp
        subset='training'
    )

    # Tải dữ liệu validation
    validation_generator = train_datagen.flow_from_directory(
        dataset_path,
        target_size=(224, 224),
        batch_size=32,
        class_mode='categorical',
        subset='validation'
    )
except Exception as e:
    print(f"Error loading data: {e}")
    raise

# Lấy danh sách nhãn
class_labels = list(train_generator.class_indices.keys())
print("Labels:", class_labels)

# Lưu nhãn vào labels.txt
with open('labels.txt', 'w') as f:
    f.write('\n'.join(class_labels))

# Xây dựng mô hình
base_model = MobileNetV2(weights='imagenet', include_top=False, input_shape=(224, 224, 3))
base_model.trainable = False  # Đóng băng tầng cơ bản

x = base_model.output
x = GlobalAveragePooling2D()(x)
x = Dense(1024, activation='relu')(x)
x = Dropout(0.5)(x)  # Ngăn overfitting
predictions = Dense(len(class_labels), activation='softmax')(x)

model = Model(inputs=base_model.input, outputs=predictions)

# Biên dịch mô hình
model.compile(
    optimizer='adam',
    loss='categorical_crossentropy',
    metrics=['accuracy']
)

# In tóm tắt mô hình
model.summary()

# Huấn luyện mô hình
try:
    history = model.fit(
        train_generator,
        epochs=10,
        validation_data=validation_generator,
        steps_per_epoch=len(train_generator),
        validation_steps=len(validation_generator)
    )
except Exception as e:
    print(f"Error during training: {e}")
    raise

# Vẽ biểu đồ độ chính xác và loss
plt.figure(figsize=(12, 4))

# Biểu đồ accuracy
plt.subplot(1, 2, 1)
plt.plot(history.history['accuracy'], label='Training Accuracy')
plt.plot(history.history['val_accuracy'], label='Validation Accuracy')
plt.title('Model Accuracy')
plt.xlabel('Epoch')
plt.ylabel('Accuracy')
plt.legend()

# Biểu đồ loss
plt.subplot(1, 2, 2)
plt.plot(history.history['loss'], label='Training Loss')
plt.plot(history.history['val_loss'], label='Validation Loss')
plt.title('Model Loss')
plt.xlabel('Epoch')
plt.ylabel('Loss')
plt.legend()

plt.tight_layout()
plt.show()

# Fine-tuning
base_model.trainable = True
fine_tune_at = 100  # Mở khóa từ tầng 100 trở đi
for layer in base_model.layers[:fine_tune_at]:
    layer.trainable = False

model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=0.0001),  # Learning rate nhỏ
    loss='categorical_crossentropy',
    metrics=['accuracy']
)

# Huấn luyện thêm (fine-tuning)
try:
    fine_tune_history = model.fit(
        train_generator,
        epochs=5,
        validation_data=validation_generator,
        steps_per_epoch=len(train_generator),
        validation_steps=len(validation_generator)
    )
except Exception as e:
    print(f"Error during fine-tuning: {e}")
    raise

# Vẽ biểu đồ sau fine-tuning
plt.figure(figsize=(12, 4))

# Biểu đồ accuracy (fine-tuning)
plt.subplot(1, 2, 1)
plt.plot(fine_tune_history.history['accuracy'], label='Training Accuracy')
plt.plot(fine_tune_history.history['val_accuracy'], label='Validation Accuracy')
plt.title('Model Accuracy (Fine-Tuning)')
plt.xlabel('Epoch')
plt.ylabel('Accuracy')
plt.legend()

# Biểu đồ loss (fine-tuning)
plt.subplot(1, 2, 2)
plt.plot(fine_tune_history.history['loss'], label='Training Loss')
plt.plot(fine_tune_history.history['val_loss'], label='Validation Loss')
plt.title('Model Loss (Fine-Tuning)')
plt.xlabel('Epoch')
plt.ylabel('Loss')
plt.legend()

plt.tight_layout()
plt.show()

# Chuyển đổi sang TensorFlow Lite
try:
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]  # Tối ưu kích thước và tốc độ
    tflite_model = converter.convert()

    # Lưu mô hình
    with open('product_classifier.tflite', 'wb') as f:
        f.write(tflite_model)

    # Kiểm tra kích thước mô hình
    print(f"Model size: {os.path.getsize('product_classifier.tflite') / 1024 / 1024:.2f} MB")
except Exception as e:
    print(f"Error converting to TFLite: {e}")
    raise