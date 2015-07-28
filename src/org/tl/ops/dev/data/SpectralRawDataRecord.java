/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.tl.ops.AbstractStorable;
import org.tl.util.StreamTools;

/**
 *
 * @author root
 */
public class SpectralRawDataRecord  implements AbstractStorable{
    public int Sensor;
    public short[] Data;
    public long StartTime,EndTime;
    public int VisibleFrom,VisibleTo;
    public boolean DirectOrder;
    public short OverloadLevel;
    public int CommonTicks;
    public int Exposition;
    public int Divider;
    public String ExpInitString;
    public boolean IsSpark;
    public SpectralRawDataRecord(int sensor,short[] data,
            int visibleFrom,int visibleTo,
            long startTime,long endTime,
            boolean directOrder,
            short maxLevel,
            int commonTicks,
            int exposition,
            int divider,
            String expString,
            boolean isSpark){
        IsSpark = isSpark;
        Sensor = sensor;
        DirectOrder = directOrder;
        OverloadLevel = maxLevel;
        CommonTicks = commonTicks;
        Exposition = exposition;
        Divider = divider;
        ExpInitString = expString;
        if(directOrder){
            Data = data;
            VisibleFrom = visibleFrom;
            VisibleTo = visibleTo;
        } else {
            Data = new short[data.length];
            int from = data.length-1;
            for(int i = 0;i<data.length;i++)
                Data[from-i] = data[i];
            VisibleTo = visibleFrom;
            VisibleFrom = visibleTo;
        }
        StartTime = startTime;
        EndTime = endTime;
    }
    
    public SpectralRawDataRecord(){
        
    }

    @Override
    public void store(DataOutputStream dos) throws IOException {
        StreamTools.versionBlockBegin(dos, 1);
        dos.writeInt(Sensor);
        dos.writeShort(OverloadLevel);
        StreamTools.writeShortArray(Data, dos);
        dos.writeInt(VisibleFrom);
        dos.writeInt(VisibleTo);
        dos.writeBoolean(DirectOrder);
        dos.writeLong(StartTime);
        dos.writeLong(EndTime);
        dos.writeInt(CommonTicks);
        dos.writeInt(Exposition);
        dos.writeInt(Divider);
        StreamTools.writeString(ExpInitString, dos);
        dos.writeBoolean(IsSpark);
        StreamTools.versionBlockEnd(dos);
    }

    @Override
    public void restore(DataInputStream dis) throws IOException {
        int ver = StreamTools.versionBlockBegin(dis, 1, 1);
        Sensor = dis.readInt();
        OverloadLevel = dis.readShort();
        Data = StreamTools.readShortArray(dis);
        VisibleFrom = dis.readInt();
        VisibleTo = dis.readInt();
        DirectOrder = dis.readBoolean();
        StartTime = dis.readLong();
        EndTime = dis.readLong();
        CommonTicks = dis.readInt();
        Exposition = dis.readInt();
        Divider = dis.readInt();
        ExpInitString = StreamTools.readString(dis);
        IsSpark = dis.readBoolean();
        StreamTools.versionBlockEnd(dis);
    }
}
