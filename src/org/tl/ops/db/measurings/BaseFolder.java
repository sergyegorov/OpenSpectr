/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.db.measurings;

import java.io.File;
import org.tl.ops.db.FolderList;

/**
 *
 * @author root
 */
public class BaseFolder extends FolderList{
    public BaseFolder(File fl){
        super(fl);
    }
    
    public ProbFolder getProb(int index){
        return new ProbFolder(getSubFolder(index));
    }

    @Override
    public boolean extraFileFilter(File file) {
        return true;
    }
}
