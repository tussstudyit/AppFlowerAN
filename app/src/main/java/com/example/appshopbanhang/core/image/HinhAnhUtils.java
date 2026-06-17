package com.example.appshopbanhang.core.image;

import com.example.appshopbanhang.core.network.ApiConfig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HinhAnhUtils {
    private static final int DEFAULT_IMAGE_SIZE = 360;
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);
    private static final LruCache<String, Bitmap> BITMAP_CACHE = new LruCache<String, Bitmap>(
            (int) (Runtime.getRuntime().maxMemory() / 1024 / 8)
    ) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }
    };

    private HinhAnhUtils() {
    }

    public static void loadUrl(ImageView imageView, String imageUrl, int fallbackResId) {
        loadUrl(imageView, imageUrl, fallbackResId, DEFAULT_IMAGE_SIZE, DEFAULT_IMAGE_SIZE);
    }

    public static void loadUrl(ImageView imageView, String imageUrl, int fallbackResId, int reqWidth, int reqHeight) {
        String absoluteUrl = ApiConfig.imageUrl(imageUrl);
        if (absoluteUrl == null || absoluteUrl.isEmpty()) {
            imageView.setTag(null);
            imageView.setImageResource(fallbackResId);
            return;
        }

        String key = "url:" + absoluteUrl + ":" + reqWidth + "x" + reqHeight;
        imageView.setTag(key);
        Bitmap cachedBitmap = BITMAP_CACHE.get(key);
        if (cachedBitmap != null && !cachedBitmap.isRecycled()) {
            imageView.setImageBitmap(cachedBitmap);
            return;
        }

        imageView.setImageResource(fallbackResId);
        EXECUTOR.execute(() -> {
            Bitmap bitmap = null;
            try {
                byte[] data = download(absoluteUrl);
                bitmap = decodeSampledBitmap(data, reqWidth, reqHeight);
            } catch (Exception ignored) {
            }
            if (bitmap != null) {
                BITMAP_CACHE.put(key, bitmap);
            }
            Bitmap finalBitmap = bitmap;
            MAIN_HANDLER.post(() -> {
                Object currentTag = imageView.getTag();
                if (key.equals(currentTag)) {
                    if (finalBitmap != null) {
                        imageView.setImageBitmap(finalBitmap);
                    } else {
                        imageView.setImageResource(fallbackResId);
                    }
                }
            });
        });
    }

    public static Bitmap decodeSampledBitmap(byte[] data, int reqWidth, int reqHeight) {
        if (data == null || data.length == 0) {
            return null;
        }

        BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
        boundsOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, boundsOptions);

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inSampleSize = calculateInSampleSize(boundsOptions, reqWidth, reqHeight);
        decodeOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return Math.max(1, inSampleSize);
    }

    private static byte[] download(String imageUrl) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(15000);
        try (InputStream input = connection.getInputStream();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return output.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
}
