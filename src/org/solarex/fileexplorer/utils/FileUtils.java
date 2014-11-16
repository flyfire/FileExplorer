package org.solarex.fileexplorer.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    public static File[] sort(File[] files){
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new FileComparator());
        return (File[]) fileList.toArray();
    }
}
