package com.blstream.as;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.widget.ImageView;

import java.lang.ref.SoftReference;


public class AnimationsContainer {

    private static AnimationsContainer instance;

    private AnimationsContainer() {
    };

    public static AnimationsContainer getInstance() {
        if (instance == null)
            instance = new AnimationsContainer();
        return instance;
    }

    private int[] animFrames = { R.drawable.photo_1, R.drawable.photo_2, R.drawable.photo_3,
            R.drawable.photo_4, R.drawable.photo_5, R.drawable.photo_6 };

    public FramesSequenceAnimation createAnim(ImageView imageView) {
        return new FramesSequenceAnimation(imageView, animFrames);
    }

    public class FramesSequenceAnimation {
        private int[] frames;
        private int index;
        private boolean shouldRun;
        private boolean isRunning;
        private SoftReference<ImageView> softReferenceImageView;
        private Handler handler;
        private int delayMillis;

        private Bitmap bitmap = null;
        private BitmapFactory.Options bitmapOptions;

        public FramesSequenceAnimation(ImageView imageView, int[] frames) {
            handler = new Handler();
            this.frames = frames;
            index = -1;
            softReferenceImageView = new SoftReference<ImageView>(imageView);
            shouldRun = false;
            isRunning = false;
            delayMillis = 4000;

            imageView.setImageResource(this.frames[0]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            Bitmap.Config config = bmp.getConfig();
            bitmap = Bitmap.createBitmap(width, height, config);
            bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inBitmap = bitmap;
            bitmapOptions.inMutable = true;
        }

        private int getNext() {
            index++;
            if (index >= frames.length)
                index = 0;
            return frames[index];
        }

        public synchronized void start() {
            shouldRun = true;
            if (isRunning)
                return;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = softReferenceImageView.get();
                    if (!shouldRun || imageView == null) {
                        isRunning = false;
                        return;
                    }

                    isRunning = true;
                    handler.postDelayed(this, delayMillis);

                    if (imageView.isShown()) {
                        int imageRes = getNext();
                        if (bitmap != null) {
                            Bitmap bitmap = null;
                            try {
                                bitmap = BitmapFactory.decodeResource(imageView.getResources(), imageRes, bitmapOptions);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            } else {
                                imageView.setImageResource(imageRes);
                                FramesSequenceAnimation.this.bitmap.recycle();
                                FramesSequenceAnimation.this.bitmap = null;
                            }
                        } else {
                            imageView.setImageResource(imageRes);
                        }
                    }

                }
            };

            handler.post(runnable);
        }

        public synchronized void stop() {
            shouldRun = false;
        }
    }
}
