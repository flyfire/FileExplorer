package org.solarex.fileexplorer.utils;

import org.solarex.fileexplorer.bean.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    public static ArrayList<FileInfo> sortAndGenerate(File[] files){
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new FileComparator());
        ArrayList<FileInfo> allFileInfos = new ArrayList<FileInfo>();
        for (File file : fileList) {
            FileInfo info = new FileInfo(file, false);
            allFileInfos.add(info);
        }
        return allFileInfos;
    }
}
