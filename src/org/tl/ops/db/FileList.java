/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.db;

import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author root
 */
public abstract class FileList {
    private File[] subFileList;
    private File baseFolder;
    public FileList(File fl){
        baseFolder = fl;
        subFileList = fl.listFiles((File pathname) -> pathname.isFile() && extraFileFilter(pathname));
    }
    
    JList<String> showInList;
    public void showInList(JList<String> list){
        showInList = list;
        update();
    }
    
    public void update(){
        subFileList = baseFolder.listFiles((File pathname) -> pathname.isFile()&& extraFileFilter(pathname));
        DefaultListModel dlm = new DefaultListModel();
        dlm.clear();
        if(subFileList == null)
            return;
        for(File f : subFileList){
            String name = f.getName();
            dlm.addElement(name);
        }
        showInList.setModel(dlm);
    }
    
    abstract public boolean extraFileFilter(File file);
    
    public int getCount(){
        return subFileList.length;
    }
     
    public File getSubFolder(int index){
        return subFileList[index];
    }
}
