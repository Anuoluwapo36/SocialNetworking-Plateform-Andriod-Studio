package com.example.ziniraproject.until;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {

    /**
     * Search a directory and return a list of all sub-directories
     * @param directory
     * @return
     */

    public  static ArrayList<String> getDirectoryPaths(String directory) {

        ArrayList<String> pathsArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();

        for (int i = 0; i < listfiles.length; i++) {
            if (listfiles[i].isDirectory()) {
                pathsArray.add(listfiles[i].getAbsolutePath());
            }
        }

        return pathsArray;
    }

    /**
     * Search a directory and return a list of all files
     * @param directory
     * @return
     */
    public  static ArrayList<String>getFilePaths(String directory){

        ArrayList<String> pathsArray = new ArrayList<>();
        File file = new File(directory);

        File[] listfiles = file.listFiles();


        if (listfiles != null){

            for (int i = 0; i < listfiles.length; i++) {
                if (listfiles[i].isFile()) {
                    pathsArray.add(listfiles[i].getAbsolutePath());
                }
            }

        }


        return  pathsArray;
    }
}
