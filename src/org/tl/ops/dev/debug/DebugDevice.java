/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev.debug;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.tl.ops.Common;
import org.tl.ops.dev.AbstractDevice;
import org.tl.ops.dev.data.SpectralRawData;

/**
 *
 * @author root
 */
public class DebugDevice extends AbstractDevice{
    @Override
    public String getDefaultMeasuringProgram() {
        return "test";
    }

    @Override
    public String editMeasuringProgram(JFrame parent,String src) {
        
        return src;
    }

    @Override
    public SpectralRawData measuring(Component parent) throws Exception{
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(Common.DBDir+"Debug"+File.separator));
        fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            DebugDeviceDataConfig conf = new DebugDeviceDataConfig(selectedFile.getAbsolutePath());
            SpectralRawData rawData = new SpectralRawData(conf.Conditions,Common.getDispers());
            long timeFrom = System.currentTimeMillis();
            for(int e = 0;e<conf.CommonTimes.length;e++){
                File dataFile = new File(selectedFile.getAbsolutePath()+File.separator+"data"+e);
                short[][] data = new short[15][];
                int[] dataLength = new int[15];
                long[] dataTimeFrom = new long[15];
                for(int i = 0;i<data.length;i++){
                    data[i] = new short[4096];
                    dataTimeFrom[i] = timeFrom;
                }
                byte[] buffer = new byte[2];
                try(FileInputStream fis = new FileInputStream(dataFile)){
                    while(fis.available() > 0){
                        //int bl = fis.read();
                        //int bh = fis.read();
                        fis.read(buffer);
                        int bl = buffer[0]&0xFF;
                        int sn = buffer[1]>>4;
                        short val = (short)(((buffer[1] & 0xF) << 8) | bl);
                        data[sn][dataLength[sn]] = val;
                        dataLength[sn] ++;
                        if(dataLength[sn] == data[e].length){
                            long duration = (long)(0.008*conf.Exps[e][sn]*conf.Divider*1000);
                            int realSn = Common.Setup.getVisibleSensor(sn);
                            boolean direct;
                            if(realSn >= 0)
                                direct = true;
                            else {
                                direct = false;
                                realSn = -realSn;
                            }
                            rawData.addShort(realSn, data[sn], 32, 4096-414, 
                                    dataTimeFrom[sn], dataTimeFrom[sn]+duration,
                                    direct,getMaxLevel(realSn),
                                    conf.CommonTimes[e],
                                    conf.Exps[e][sn],
                                    conf.Divider,
                                    conf.ExpConditionString[e],
                                    conf.IsActive[e]);
                            dataLength[sn] = 0;
                            dataTimeFrom[sn] += duration;
                        }
                    }
                }
                timeFrom = (long)(timeFrom + 0.008*conf.CommonTimes[e]*conf.Divider*1000);
            }
            return rawData;
        }
        return null;
    }

    @Override
    public int[] getSensorSizes() {
        return new int[]{4096,4096,4096,4096,4096,4096};
    }

    @Override
    public short getMaxLevel(int sensor) {
        return 3500;
    }
}
