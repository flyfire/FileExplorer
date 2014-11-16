package org.solarex.fileexplorer.bean;

public class FileInfo {
    private String fileName;
    private boolean isSelected;
    
    public FileInfo(){
        this.isSelected = false;
    }
    
    public FileInfo(String name, boolean isSelected){
        this.fileName = name;
        this.isSelected = isSelected;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    
}
