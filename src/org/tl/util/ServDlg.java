/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.util;

import java.awt.Component;
import java.io.File;
import javax.swing.JOptionPane;
import org.tl.ops.Common;

/**
 *
 * @author root
 */
public class ServDlg {
    public static String getNewFileName(Component parent,String txt,String title,File baseDirectory){
        String selected;
        String msg = "";
        do{
            selected = javax.swing.JOptionPane.showInputDialog(parent, 
                    txt,title+msg, 
                    JOptionPane.QUESTION_MESSAGE);
            if(selected == null)
                return null;
            if(selected.indexOf(':') >= 0){
                msg = Common.Mls.get("FileNameErrorSymbol:");
                continue;
            }
            if(selected.indexOf(';') >= 0){
                msg = Common.Mls.get("FileNameErrorSymbol;");
                continue;
            }
            if(selected.indexOf('\\') >= 0){
                msg = Common.Mls.get("FileNameErrorSymbol\\");
                continue;
            }
            File f = new File(baseDirectory.getAbsoluteFile()+File.separator+selected);
            if(f.exists()){
                msg = Common.Mls.get("FileExists");
                continue;
            }
            return selected;
        } while(true);
    }
}
