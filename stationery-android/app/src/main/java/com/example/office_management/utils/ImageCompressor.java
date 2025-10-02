package com.example.office_management.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressor {

    /**
     * Nén + resize sao cho <= maxBytes, và down-sample ngay khi đọc.
     */
    public static byte[] compressImage(Context context, Uri imageUri, long maxBytes) throws IOException {
        // 1) Decode down-sampled bitmap
        Bitmap bitmap = decodeSampledBitmap(context, imageUri, 1000, 1000);

        // 2) Nén JPEG với quality bắt đầu 100 → giảm dần
        int quality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        while (baos.size() > maxBytes && quality > 10) {
            baos.reset();
            quality -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }

        // 3) Nếu vẫn quá lớn, resize tiếp 90%
        while (baos.size() > maxBytes) {
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, bitmap.getWidth()*9/10, bitmap.getHeight()*9/10, true);
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }

        return baos.toByteArray();
    }

    /**
     * Decode một phiên bản bitmap down-sampled để mỗi cạnh ≤ reqWidth/reqHeight.
     */
    private static Bitmap decodeSampledBitmap(Context ctx, Uri uri,
                                              int reqWidth, int reqHeight) throws IOException {
        // 1) Đọc bounds
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        try (InputStream is = ctx.getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(is, null, opts);
        }

        // 2) Tính inSampleSize
        opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

        // 3) Decode với sample size
        try (InputStream is2 = ctx.getContentResolver().openInputStream(uri)) {
            return BitmapFactory.decodeStream(is2, null, opts);
        }
    }

    /**
     * Tính sample size (luôn là lũy thừa 2) cho việc down-sampling.
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfH = height / 2;
            final int halfW = width / 2;

            while ((halfH / inSampleSize) >= reqHeight
                    && (halfW / inSampleSize) >= reqWidth) {
                inSampleSize <<= 1;
            }
        }
        return inSampleSize;
    }
}
