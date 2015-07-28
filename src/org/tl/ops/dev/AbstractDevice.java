/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev;

import java.awt.Component;
import javax.swing.JFrame;
import org.tl.ops.dev.data.SpectralRawData;
import org.tl.ops.dev.data.SpectralRawDataPreViewer;
import org.tl.util.DlgPanelOk;

/**
 *
 * @author root
 */
public abstract class AbstractDevice {
    public abstract String getDefaultMeasuringProgram();
    public abstract String editMeasuringProgram(JFrame parent,String src);
    SpectralRawDataPreViewer viewer;
    
    public SpectralRawData measuringWithPreview(JFrame parent) throws Exception{
        SpectralRawData ret = measuring(parent);
        if(viewer == null)
            viewer = new SpectralRawDataPreViewer();
        viewer.showSpectr(ret);
        if(DlgPanelOk.show(parent, "Spectr preview...", viewer) == false)
            return null;
        return ret;
    }
    
    public abstract SpectralRawData measuring(Component parent) throws Exception;
    public abstract int[] getSensorSizes();
    public abstract short getMaxLevel(int sensor);
}
