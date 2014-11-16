package org.solarex.fileexplorer.utils;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {

    @Override
    public int compare(File lhs, File rhs) {
        if (lhs.isDirectory() && rhs.isFile()) {
            return -1;
        }else if (lhs.isFile() && rhs.isDirectory()) {
            return 1;
        }else {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    }
    
}
