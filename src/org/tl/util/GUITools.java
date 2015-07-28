/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.util;

import java.awt.Container;
import java.awt.Window;

/**
 *
 * @author root
 */
public class GUITools {
    static public Window getParent(Container cont){
        if(cont instanceof Window)
            return (Window)cont;
        Container tmp = cont.getParent();
        if(tmp == null)
            return null;
        return getParent(tmp);
    }
}
