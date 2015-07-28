/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.util;

import java.awt.Window;
import org.tl.math.Function;
import org.tl.util.debug.DataView;
import org.tl.util.debug.FunctionView;

/**
 *
 * @author root
 */
public class DebugTools {
    static public void showFunction(Window parent,String title,Function fk,boolean[] en,double[] x,double[] y){
        FunctionView dv = new org.tl.util.debug.FunctionView();
        dv.init(fk,en,x,y);
        DlgPanelOk.show(parent, title, dv);
    }

    static public void showData(Window parent,String title,double[] data,double mark){
        DataView dv = new org.tl.util.debug.DataView();
        dv.init(data,mark);
        DlgPanelOk.show(parent, title, dv);
    }

    static public void showData(Window parent,String title,float[] sdata,double mark){
        DataView dv = new org.tl.util.debug.DataView();
        double[] data = new double[sdata.length];
        for(int i = 0;i<data.length;i++)
            data[i] = sdata[i];
        dv.init(data,mark);
        DlgPanelOk.show(parent, title, dv);
    }

    static public void showData(Window parent,String title,short[] sdata,double mark){
        DataView dv = new org.tl.util.debug.DataView();
        double[] data = new double[sdata.length];
        for(int i = 0;i<data.length;i++)
            data[i] = sdata[i];
        dv.init(data,mark);
        DlgPanelOk.show(parent, title, dv);
    }
}
