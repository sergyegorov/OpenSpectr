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
public class MeasuringFolder extends FolderList{
    public MeasuringFolder(File baseDirectory){
        super(baseDirectory);
    }
    
    @Override
    public boolean extraFileFilter(File file) {
        return true;
    }
}
