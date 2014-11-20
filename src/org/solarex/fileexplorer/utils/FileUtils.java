package org.solarex.fileexplorer.utils;

import android.os.Handler;
import android.util.Log;

import org.solarex.fileexplorer.bean.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtils";
    
    public static ArrayList<FileInfo> getFiles(String path){
        File[] files = new File(path).listFiles(new SolarexFilter());
        return sortAndGenerate(files);
    }
    public static ArrayList<FileInfo> sortAndGenerate(File[] files){
        assert(files!=null);
        Log.v(TAG, "files = " + files);
        List<File> fileList = null;
        try {
            fileList = Arrays.asList(files);
        } catch (Exception e) {
            Log.v(TAG, "Exception happened, ex = " + e.getMessage());
        }
        
        Collections.sort(fileList, new FileComparator());
        ArrayList<FileInfo> allFileInfos = new ArrayList<FileInfo>();
        for (File file : fileList) {
            FileInfo info = new FileInfo(file, false);
            allFileInfos.add(info);
        }
        return allFileInfos;
    }
    public static void PrintFileInfos(ArrayList<FileInfo> fileInfos){
        for (FileInfo fileInfo : fileInfos) {
            Log.v(TAG, "file = " + fileInfo);
        }
    }
    
    public static String makePath(String path, String name){
        if (path.endsWith("/")) {
            return path + name;
        }
        return path + "/" + name;
    }
    
    public static boolean CreateFolder(String dest, String name){
        String newFolderName = makePath(dest, name);
        File file = new File(newFolderName);
        if (file.exists()) {
            return false;
        }
        return file.mkdir();
    }
    
    public static void CopyFile(FileInfo fileInfo, String dest){
        if (fileInfo == null || dest == null ) {
            Log.e(TAG, "CopyFile: null parameter");
        }
        File file = fileInfo.getFile();
    }
    
    
}
