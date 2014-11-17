package org.solarex.fileexplorer.bean;

import android.graphics.drawable.Drawable;

public class CacheApkIcon {
    public String path;
    public Drawable icon;
    
    public CacheApkIcon(String path, Drawable icon){
        this.path = path;
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    
    
}
