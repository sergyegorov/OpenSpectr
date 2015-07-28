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
import java.util.ArrayList;
import org.tl.ops.AbstractStorable;
import org.tl.util.StreamTools;

/**
 *
 * @author root
 */
public class SpectralRawData implements AbstractStorable{
    String rootDirectory;
    String conditions;
    SpectralRawDispers dispers;
    public SpectralRawData(File directory){
        setupBaseDirectory(directory.getAbsolutePath());
    }
    
    public final void setupBaseDirectory(String directory){
        if(directory.endsWith("\\") == false)
            directory = directory.concat(File.separator);
        rootDirectory = directory;        
    }
    
    public SpectralRawData(String conditions,SpectralRawDispers dispers){
        this.conditions = conditions;
        this.dispers = dispers;
    }
    
    ArrayList<SpectralRawDataRecord> measuringRecords = new ArrayList<>();
    public void addShort(int sensor,short[] data,int visibleFrom,int visibleTo,
            long startTime,long endTime,boolean directOrder,short maxLevel,
            int commonTiks,int exposition,int divider,String expString,
            boolean isSpark){
        measuringRecords.add(new SpectralRawDataRecord(sensor, data,
                visibleFrom,visibleTo,
                startTime, endTime,
                directOrder,maxLevel,
                commonTiks,exposition,
                divider,expString,
                isSpark));
    }
    
    public int getSensorCount(){
        int ret = 0;
        for(SpectralRawDataRecord r : measuringRecords){
            if(r.Sensor > ret)
                ret = r.Sensor;
        }
        return ret+1;
    }
    
    public int getSensorSize(int sn){
        for(SpectralRawDataRecord r : measuringRecords){
            if(r.Sensor == sn)
                return r.Data.length;
        }
        return -1;
    }
    
    public int getSensorFrameCount(int sensor){
        int count = 0;
        for(SpectralRawDataRecord r : measuringRecords){
            if(r.Sensor == sensor)
                count ++;
        }
        return count;
    }
    
    public int getRecordsCount(){
        return measuringRecords.size();
    }
    
    public SpectralRawDataRecord getRecord(int index){
        return measuringRecords.get(index);
    }
    
    File getFile(String name){
        String path = rootDirectory;
        if(name != null)
            path += name;
        return new File(path);
    }
    
    public final static String rawFileName = "raw.data";
    public void store() throws IOException{
        store(getFile(rawFileName));
    }
    
    @Override
    public void store(DataOutputStream dos) throws IOException {
        StreamTools.versionBlockBegin(dos, 1);
        dos.writeShort(1); // type
        StreamTools.writeString(conditions, dos);
        dispers.store(dos);
        dos.writeInt(measuringRecords.size());
        for(SpectralRawDataRecord record : measuringRecords)
            record.store(dos);
        StreamTools.versionBlockEnd(dos);
    }

    public void restore() throws IOException{
        restore(getFile(rawFileName));
    }
    
    @Override
    public void restore(DataInputStream dis) throws IOException {
        int ver = StreamTools.versionBlockBegin(dis, 1, 1);
        int type = dis.readShort();
        if(type != 1)
            throw new IOException("Wrong data type...");
        conditions = StreamTools.readString(dis);
        dispers = new SpectralRawDispers();
        dispers.restore(dis);
        int n = dis.readInt();
        for(int i = 0;i<n;i++){
            SpectralRawDataRecord r = new SpectralRawDataRecord();
            r.restore(dis);
            measuringRecords.add(r);
        }
        StreamTools.versionBlockEnd(dis);
    }
}
