/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev.data;

import org.tl.math.MinMax2D;
import org.tl.math.Function;
import org.tl.ops.AbstractStorable;
import org.tl.util.StreamTools;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.tl.ops.Common;


/**
 *
 * @author root
 */
public class SpectralRawDispers implements AbstractStorable{
    ArrayList<Function> functions = new ArrayList<>();
    int[] sensorSizes;
    int[] basePixels;
    static public SpectralRawDispers getNullDisp(){
        return new SpectralRawDispers(Common.Setup.getDevice().getSensorSizes());
    }
    
    public MinMax2D getLyDimenstion(MinMax2D ret){
        if(ret == null)
            ret = new MinMax2D();
        for(int sn = 0;sn<functions.size();sn++){
            ret.addValueX(getLambdaForSensor(sn, 0));
            ret.addValueX(getLambdaForSensor(sn, sensorSizes[sn]));
        }
        return ret;
    }
    
    public MinMax2D getPixelDimenstion(MinMax2D ret){
        if(ret == null) 
            ret = new MinMax2D();
        ret.addValueX(0);
        int size = basePixels.length-1;
        ret.addValueX(basePixels[size]+sensorSizes[size]);
        return ret;
    }
    
    public SpectralRawDispers(){
        
    }
    
    public SpectralRawDispers(int[] sensorSizes){
        int basePixel = 0;
        functions.clear();
        basePixels = new int[sensorSizes.length];
        for(int i = 0;i<sensorSizes.length;i++){
            basePixels[i] = basePixel;
            int sSize = sensorSizes[i];
            double[] n = {0,sSize};
            double[] la = {basePixel,basePixel+sSize};
            boolean[] en = {true,true};
            try {
                functions.add(new Function(Function.FType.Poly1, en, n, la,false));
            } catch (org.tl.math.TLExceptionMath ex) {
                Common.Log.exception("Setup default dispers",ex);
                throw new RuntimeException("Unexpected exception...");
            }
            basePixel += sSize;
        }
        this.sensorSizes = (int[])sensorSizes.clone();
    }

    public ArrayList<Integer> getSensorList(double ly,ArrayList<Integer> toStore){
        if(toStore == null)
            toStore = new ArrayList<>();
        toStore.clear();
        for(int s = 0;s<functions.size();s++){
            double pixel = getPixelForSensor(s, ly);
            if(pixel < 0 || pixel > sensorSizes[s])
                continue;
            toStore.add(s);
        }
        return toStore;
    }
    
    public double getLambdaForSensor(int sn,double pixel){
        return functions.get(sn).calcDirect(pixel);
    }

    public double getPixelForSensor(int sn,double lambda){
        return functions.get(sn).calcRev(lambda);
    }
    
    public void findPixelSensor(double ly,ArrayList<Integer> snOut){
        snOut.clear();
        for(int s = 0;s<functions.size();s++){
            double lyFrom = getLambdaForSensor(s, 0);
            double lyTo = getLambdaForSensor(s, sensorSizes[s]);
            if(lyFrom <= ly && ly < lyTo)
                snOut.add(new Integer(s));
        }
    }

    final public void init(SpectralRawDispers fromSoruce){
        functions.clear();
        for(Function fk : fromSoruce.functions){
            if(fk == null)
                functions.add(null);
            else
                functions.add(new Function(fk));
        }
        sensorSizes = (int[])fromSoruce.sensorSizes.clone();
        basePixels = (int[])fromSoruce.basePixels.clone();
    }
    
    public SpectralRawDispers(SpectralRawDispers fromSoruce){
        init(fromSoruce);
    }

    public void setFunction(int sensorIndex,Function fk){
        while(sensorIndex > functions.size())
            functions.add(null);
        if(sensorIndex == functions.size())
            functions.add(fk);
        else
            functions.set(sensorIndex, fk);
    }

    public Function getFunction(int index){
        return functions.get(index);
    }
    
    public int getSensorCount(){
        return functions.size();
    }
    
    public int getSensorSize(int index){
        return sensorSizes[index];
    }

    /*public void save(File file) throws Exception{
        try(FileOutputStream fos = new FileOutputStream(file);
                DataOutputStream dos = new DataOutputStream(fos);){
            store(dos);
        }
    }*/
    
    @Override
    public void store(DataOutputStream dos) throws IOException {
        StreamTools.versionBlockBegin(dos, 1);
        dos.writeInt(functions.size());
        for(Function fk : functions){
            if(fk != null){
                dos.writeBoolean(true);
                fk.save(dos);
            } else
                dos.writeBoolean(false);
        }
        StreamTools.writeIntArray(basePixels, dos);
        StreamTools.writeIntArray(sensorSizes, dos);
        StreamTools.versionBlockEnd(dos);
    }

    /*public void load(File file) throws Exception{
        try(FileInputStream fis = new FileInputStream(file);
                DataInputStream dis = new DataInputStream(fis);){
            restore(dis);
        }
    }*/

    @Override
    public void restore(DataInputStream dis) throws IOException {
        functions.clear();
        StreamTools.versionBlockBegin(dis, 1, 1);
        int n = dis.readInt();
        for(int i = 0;i<n;i++){
            if(dis.readBoolean() == false)
                functions.add(null);
            else{
                Function fk = new Function();
                fk.load(dis);
                functions.add(fk);
            }
        }
        basePixels = StreamTools.readIntArray(dis);
        sensorSizes = StreamTools.readIntArray(dis);
        StreamTools.versionBlockEnd(dis);
    }
}
