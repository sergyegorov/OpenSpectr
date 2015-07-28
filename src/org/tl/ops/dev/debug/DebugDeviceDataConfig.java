/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev.debug;

import java.io.File;
import java.io.IOException;
import org.tl.util.StreamTools;

/**
 *
 * @author root
 */
public class DebugDeviceDataConfig {
    public int[] CommonTimes;
    public int[][] Exps;
    public boolean[] IsActive;
    public int[] FileIndex;
    public int Divider;
    public String Conditions;
    public String[] ExpConditionString;
    public DebugDeviceDataConfig(String rootDir) throws IOException{
        Conditions = StreamTools.readText(new File(rootDir+File.separator+"cur_prog.txt"));
        String[] lines = Conditions.split(";");
        Divider = Integer.parseInt(String.valueOf(lines[2].charAt(1)));
        CommonTimes = new int[lines.length-4];
        Exps = new int[CommonTimes.length][];
        IsActive = new boolean[CommonTimes.length];
        FileIndex = new int[CommonTimes.length];
        ExpConditionString = new String[CommonTimes.length];
        for(int l = 4;l<lines.length;l++){
            int frameIndex = l-4;
            ExpConditionString[frameIndex] = lines[l];
            String[] values = lines[l].split("_");
            IsActive[frameIndex] = values[0].startsWith("E");
            FileIndex[frameIndex] = Integer.parseInt(values[0].substring(1));
            CommonTimes[frameIndex] = Integer.parseInt(values[1]);
            String[] sexps = values[2].split(" ");
            int[] exps = new int[sexps.length];
            for(int i = 0;i<sexps.length;i++)
                exps[i] = Integer.parseInt(sexps[i]);
            Exps[frameIndex] = exps;
        }
    }
}
