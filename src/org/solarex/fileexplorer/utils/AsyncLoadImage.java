package org.solarex.fileexplorer.utils;

import android.os.Handler;
import android.widget.ImageView;

import org.solarex.fileexplorer.bean.CacheImg;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncLoadImage {
    private int CACHE_IMAGE_SIZE = 100;
    private ConcurrentLinkedQueue<CacheImg> cacheImgs;
    private Handler handler;
    public AsyncLoadImage(Handler handler) {
        this.handler = handler;
        this.cacheImgs = new ConcurrentLinkedQueue<CacheImg>();
    }
    public void loadImage(ImageView imageView){
        String path = (String) imageView.getTag();
        for (CacheImg img : cacheImgs) {
            if (img.getPath().equals(path)) {
                imageView.setImageBitmap(img.getIcon());
                return;
            }
        }
        new LoadImageThread(path, imageView).start();
    }
    class LoadImageThread extends Thread{
        private String path;
        private ImageView imageView;
        
        public LoadImageThread(String path, ImageView imageView){
            this.path = path;
            this.imageView = imageView;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
        }
        
    }
}
