/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import org.tl.ops.AbstractStorable;
import org.tl.util.StreamTools;

/**
 *
 * @author root
 */
public class SpectralPreview implements AbstractStorable{
    File baseDir;
    File getFile(String name){
        String path = baseDir.getAbsolutePath()+File.separator;
        if(name != null)
            path += name;
        return new File(path);
    }
    
    public static final String FileName = "preview.data";
    public SpectralPreview(File path) throws IOException{
        baseDir = path;
        File fl = getFile(FileName);
        if(fl.exists() == false){
            SpectralRawData data = RawDataCash.load(baseDir.getAbsolutePath());
            
            int sn = data.getSensorCount();
            
            MinimalValues = new short[sn][];
            MaximalValues = new short[sn][];
            NullValues = new short[sn][];
            OverloadLevels = new short[sn];
            
            for(int s = 0;s<sn;s++){
                int size = data.getSensorSize(s);
                MinimalValues[s] = new short[size];
                MaximalValues[s] = new short[size];
                NullValues[s] = new short[size];
                for(int i = 0;i<size;i++){
                    MinimalValues[s][i] = Short.MAX_VALUE;
                    MaximalValues[s][i] = -Short.MAX_VALUE;
                }
            }
            
            addRawData(data);
            addCommit();
            Dispers = data.dispers;
            
            store(fl);
        } else {
            restore(fl);
        }
    }

    final public void addCommit(){
        int sn = NullCount.length;
        for(int s = 0;s<sn;s++){
            int size = NullCount[s].length;
            for(int i = 0;i<size;i++){
                NullValues[s][i] /= NullCount[s][i];
            }
        }
        NullCount = null;
    }
    
    int[][] NullCount;
    final public void addRawData(SpectralRawData data){
        if(NullCount == null){
            int sn = data.getSensorCount();
            NullCount = new int[sn][];
            for(int s = 0;s<sn;s++){
                int size = data.getSensorSize(s);
                NullCount[s] = new int[size];
            }
        }
        int rc = data.getRecordsCount();
        for(int r = 0;r<rc;r++){
            SpectralRawDataRecord dr = data.getRecord(r);
            short[] values = dr.Data;
            int sn = dr.Sensor;
            OverloadLevels[sn] = dr.OverloadLevel;
            if(dr.IsSpark){
                for(int i = 0;i<values.length;i++){
                    short val = dr.Data[i];
                    if(val < MinimalValues[sn][i])
                        MinimalValues[sn][i] = val;
                    if(val > MaximalValues[sn][i])
                        MaximalValues[sn][i] = val;
                }
            } else {
                for(int i = 0;i<values.length;i++){
                    short val = dr.Data[i];
                    NullValues[sn][i] += val;
                    NullCount[sn][i] ++;
                }
            }
        }
    }
    
    public short[] OverloadLevels;
    public short[][] MinimalValues;
    public short[][] MaximalValues;
    public short[][] NullValues;
    public SpectralRawDispers Dispers;
    @Override
    final public void store(DataOutputStream dos) throws IOException {
        if(NullCount != null)
            throw new IOException("AddRasData is not commited...");
        StreamTools.versionBlockBegin(dos, 1);
        int sn = MinimalValues.length;
        dos.writeInt(sn);
        for(int s = 0;s<sn;s++){
            StreamTools.writeShortArray(MinimalValues[s], dos);
            StreamTools.writeShortArray(MaximalValues[s], dos);
            StreamTools.writeShortArray(NullValues[s], dos);
        }
        StreamTools.writeShortArray(OverloadLevels, dos);
        Dispers.store(dos);
        StreamTools.versionBlockEnd(dos);
    }

    @Override
    final public void restore(DataInputStream dis) throws IOException {
        StreamTools.versionBlockBegin(dis, 1, 1);
        int sn = dis.readInt();
        MinimalValues = new short[sn][];
        MaximalValues = new short[sn][];
        NullValues = new short[sn][];
        OverloadLevels = new short[sn];
        for(int s = 0;s<sn;s++){
            MinimalValues[s] = StreamTools.readShortArray(dis);
            MaximalValues[s] = StreamTools.readShortArray(dis);
            NullValues[s] = StreamTools.readShortArray(dis);
        }
        OverloadLevels = StreamTools.readShortArray(dis);
        Dispers = new SpectralRawDispers();
        Dispers.restore(dis);
        StreamTools.versionBlockEnd(dis);
    }
}
