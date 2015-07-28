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
public abstract class FolderList {
    private File[] subFolderList;
    private File baseFolder;
    public FolderList(File fl){
        baseFolder = fl;
        subFolderList = fl.listFiles((File pathname) -> pathname.isDirectory() && extraFileFilter(pathname));
    }
    
    JList<String> showInList;
    public void showInList(JList<String> list){
        showInList = list;
        update();
    }
    
    public void update(){
        subFolderList = baseFolder.listFiles((File pathname) -> pathname.isDirectory() && extraFileFilter(pathname));
        DefaultListModel dlm = new DefaultListModel();
        dlm.clear();
        if(subFolderList == null)
            return;
        for(File f : subFolderList)
            dlm.addElement(f.getName());
        showInList.setModel(dlm);
    }
    
    abstract public boolean extraFileFilter(File file);
    
    public int getCount(){
        return subFolderList.length;
    }
     
    public File getSubFolder(int index){
        return subFolderList[index];
    }
}
