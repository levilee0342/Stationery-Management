package com.example.office_management.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.InterpreterApi;
import org.tensorflow.lite.InterpreterFactory;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class TFLiteImageClassifier {
    private static final String TAG = "TFLiteImageClassifier";
    private static final String MODEL_PATH = "product_classifier.tflite";
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 224;

    private final InterpreterApi interpreter;
    private final List<String> labels;
    private final ImageProcessor imageProcessor;

    public TFLiteImageClassifier(Context context) throws IOException {
        // Load TFLite model
        InterpreterFactory interpreterFactory = new InterpreterFactory();
        InterpreterApi.Options options = new InterpreterApi.Options();

        MappedByteBuffer modelFile;
        try {
            modelFile = loadModelFile(context);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load model file: " + MODEL_PATH, e);
            throw new IOException("Cannot load model file: " + MODEL_PATH, e);
        }

        try {
            interpreter = interpreterFactory.create(modelFile, options);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create TFLite interpreter for model: " + MODEL_PATH, e);
            throw new IOException("Failed to initialize TFLite interpreter", e);
        }

        try {
            labels = loadLabels(context);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load labels file: " + LABEL_PATH, e);
            throw new IOException("Cannot load labels file: " + LABEL_PATH, e);
        }

        imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                .build();
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        try {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_PATH);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY,
                    fileDescriptor.getStartOffset(), fileDescriptor.getDeclaredLength());
        } catch (IOException e) {
            throw new IOException("Failed to access model file: " + MODEL_PATH, e);
        }
    }

    private List<String> loadLabels(Context context) throws IOException {
        List<String> labelList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(LABEL_PATH)));
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new IOException("Failed to read labels file: " + LABEL_PATH, e);
        }
        return labelList;
    }

    public String classifyImage(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap is null");
            return "Unknown";
        }

        try {
            TensorImage tensorImage = new TensorImage();
            tensorImage.load(bitmap);
            tensorImage = imageProcessor.process(tensorImage);

            // Chuẩn bị output buffer
            TensorBuffer outputBuffer = TensorBuffer.createFixedSize(
                    new int[]{1, labels.size()}, org.tensorflow.lite.DataType.FLOAT32);

            // Chạy mô hình
            interpreter.run(tensorImage.getBuffer(), outputBuffer.getBuffer().rewind());

            float[] scores = outputBuffer.getFloatArray();
            int maxIndex = 0;
            float maxScore = scores[0];

            for (int i = 1; i < scores.length; i++) {
                if (scores[i] > maxScore) {
                    maxScore = scores[i];
                    maxIndex = i;
                }
            }

            String label = labels.get(maxIndex);
            Log.d(TAG, "Label: " + label + ", confidence: " + maxScore);
            return label;

        } catch (Exception e) {
            Log.e(TAG, "Classification failed: ", e);
            return "Unknown";
        }
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
        }
    }
}