package org.solarex.fileexplorer.bean;

import java.io.File;

public class FileInfo {
    private File file;
    private boolean isSelected;
    
    public FileInfo(File file, boolean isSelected){
        this.file = file;
        this.isSelected = isSelected;
    }
    
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    
}
