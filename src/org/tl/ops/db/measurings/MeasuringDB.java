/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.db.measurings;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author root
 */
public class MeasuringDB {
    File baseFolder;
    public MeasuringDB(String baseFolder){
        this.baseFolder = new File(baseFolder);
        if(this.baseFolder.exists() == false)
            this.baseFolder.mkdirs();
    }
    
    File[] baseFolderList;
    public String[] getBaseList(){
        baseFolderList = baseFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        String[] ret = new String[baseFolderList.length];
        for(int i = 0;i<baseFolderList.length;i++){
            ret[i] = baseFolderList[i].getName();
        }
        return ret;
    }
    
    public BaseFolder getBaseFolder(int folderIndex){
        return new BaseFolder(baseFolderList[folderIndex]);
    }
}
