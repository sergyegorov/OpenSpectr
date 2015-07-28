/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev.data;

import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.tl.math.PicDetector;
import org.tl.ops.AbstractStorable;
import org.tl.ops.Common;
import org.tl.ops.db.ElementTable;
import org.tl.util.DebugTools;
import org.tl.util.StreamTools;

/**
 *
 * @author root
 */
public class SpectralMask implements AbstractStorable{
    public File BaseDirectory;
    public String BaseElement;
    private float Ly;
    public int Sn;
    public float Pixel;
    public byte[] MaskReal;
    public int MaskCenter;
    public boolean Enabled;
    public SpectralMask(File baseDirectory,String baseElement,float ly){
        BaseDirectory = baseDirectory;
        BaseElement = baseElement;
        Ly = ly;
        Enabled = true;
    }
    
    public SpectralMask(File baseDirectory){
        BaseDirectory = baseDirectory;
    }
    
    byte[] diff;
    public byte[] getDiff(){
        if(diff == null){
            diff = new byte[MaskReal.length];
            for(int i = 1;i<MaskReal.length - 1;i++){
                if(     MaskReal[i] <= 0 ||
                        MaskReal[i-1] <= 0 ||
                        MaskReal[i+1] <= 0      )
                    continue;
                diff[i] = (byte)(MaskReal[i+1] - MaskReal[i-1]);
            }
        }
        return diff;
    }
    /*static public class ShortNameInfo{
        public int ElementIndex;
        public String ElementName;
        public int Sn;
        public double Ly;
    };
    
    static public ShortNameInfo getParameters(String fileName){
        ShortNameInfo ret = new ShortNameInfo();
        int n = fileName.indexOf(LINK_EXT);
        fileName = fileName.substring(0,n);
        String[] tmp = fileName.split("_");
        ret.ElementIndex = ElementTable.FindIndex(tmp[0]);
        ret.ElementName = tmp[0];
        ret.Sn = Integer.parseInt(tmp[1]);
        ret.Ly = Double.parseDouble(tmp[2]);
        return ret;
    }*/
    
    static final String LINK_EXT = ".link";
    public File getFile(){
        return new File(BaseDirectory.getAbsoluteFile()+File.separator+BaseElement+"_"+Sn+"_"+Ly+LINK_EXT);
    }
    
    public class SpectralMaskView extends JPanel{
        byte[] Data;
        int linePosition;
        byte min = Byte.MAX_VALUE,max = -Byte.MAX_VALUE;
        public SpectralMaskView(byte[] data,int linePosition){
            this.linePosition = linePosition;
            Data = data;
            for(byte val : Data){
                if(val < min)
                    min = val;
                if(val > max)
                    max = val;
            }
            setPreferredSize(new java.awt.Dimension(640,200));
        }
        
        @Override
        public void paint(Graphics g){
            try{
                int h = getHeight();
                int w = getWidth();
                g.setColor(Color.white);
                g.fillRect(0, 0, w, h);
                if(Data.length == 0 || (max-min) == 0)
                    return;
                g.setColor(Color.blue);
                double kx = w/(double)Data.length;
                double ky = h/(double)(max-min);
                int px = -10000;
                int py = 0;
                for(int i = 0;i<Data.length;i++){
                    int x = (int)(i*kx);
                    int y = h - (int)((Data[i]-min)*ky);
                    g.drawLine(px, py, x, y);
                    px = x;
                    py = y;
                }
                g.setColor(Color.red);
                px = (int)(linePosition*kx);
                g.drawLine(px, 0, px, h);//*/
            }catch(Exception ex){
                Common.Log.exception("", ex);
            }
        }
    }
    
    public SpectralMaskView getMaskView(){
        return new SpectralMaskView(MaskReal,MaskCenter);
    }
    
    public void delete(){
        File fl = getFile();
        if(fl.exists() == true)
            fl.delete();
    }
    
    public float getLy(){
        return Ly;
    }
    
    public void setLy(float ly){
        if(ly == Ly)
            return;
        File fl = getFile();
        if(fl.exists() == true)
            fl.delete();
        Ly = ly;
    }
    
    public static byte[] createMask(short[] values,int centralPixel,int overloadLevel,int[] retValues){
        ArrayList<PicDetector> forward = new ArrayList<>();
        ArrayList<PicDetector> backward = new ArrayList<>();
        
        DebugTools.showData(null, "SpectralMask.createMask "+overloadLevel, values, centralPixel);
        int from = centralPixel;
        int noise = PicDetector.findNoiseLevel(values)*2;
        while((from-centralPixel) < 400 && forward.size() < 10){
            PicDetector cur = PicDetector.detect(values,from,1,noise);
            if(cur == null){
                //PicDetector.detectForward(values,from);
                break;
            }
            forward.add(cur);
            from = cur.RightPosition+1;
        }
        
        from = centralPixel-1;
        while((centralPixel-from) < 400 && backward.size() < 10){
            PicDetector cur = PicDetector.detect(values,from,-1,noise);
            if(cur == null)
                break;
            backward.add(cur);
            from = cur.LeftPosition-1;
        }
        
        int fromPixel = backward.get(backward.size()-1).LeftPosition;
        int toPixel = forward.get(forward.size()-1).RightPosition;
        
        short max = 0;
        short[] dlt = new short[values.length];
        for(int i = fromPixel;i<toPixel;i++)
        {
            if(i < 32 || i > values.length-32)
                continue;
            if(values[i+2] > overloadLevel ||
                    values[i+1] > overloadLevel || 
                    values[i] > overloadLevel || 
                    values[i-1] > overloadLevel ||
                    values[i-2] > overloadLevel)
                dlt[i] = 0;
            else {
                short val = (short)((values[i]+values[i-1]*0.5+values[i+1]*0.5)/2);//(short)(values[i+1] - values[i-1]);
                dlt[i] = val;
                if(val < 0)
                    val = (short)-val;
                if(val > max)
                    max = val;
            }
        }
        
        for(PicDetector cond : forward){
            backward.add(cond);
        }
        
        byte[] ret = new byte[toPixel-fromPixel];
        for(PicDetector cond : backward){
            for(int i = cond.LeftPosition;i<cond.RightPosition;i++){
                int val = dlt[i] * (Byte.MAX_VALUE-1) / max;
                if(val > Byte.MAX_VALUE)
                    val = Byte.MAX_VALUE;
                ret[i-fromPixel] = (byte)val;
            }
        }
        
        retValues[0] = centralPixel - fromPixel;
        
        return ret;
    }
    
    public void initBy(short[] values,float ly,int sn,int pixel,int overloadLevel){
        Pixel = pixel;
        Sn = sn;
        int[] ret = new int[1];
        MaskReal = createMask(values,pixel,overloadLevel,ret);
        MaskCenter = ret[0];
        setLy(ly);
    }

    public void store(float ly) throws IOException{
        if(ly != Ly)
            setLy(ly);
        store(getFile());
    }
    
    public void storeNew(float ly) throws IOException{
        Ly = ly;
        store(getFile());
    }
    
    @Override
    public void store(DataOutputStream dos) throws IOException {
        StreamTools.versionBlockBegin(dos, 2);
        StreamTools.writeString(BaseElement, dos);
        dos.writeFloat(Ly);
        dos.writeInt(Sn);
        dos.writeFloat(Pixel);
        dos.writeInt(MaskReal.length);
        dos.write(MaskReal);
        dos.writeInt(MaskCenter);
        dos.writeBoolean(Enabled);
        StreamTools.versionBlockEnd(dos);
    }

    @Override
    public void restore(DataInputStream dis) throws IOException {
        int ver = StreamTools.versionBlockBegin(dis, 1,2);
        BaseElement = StreamTools.readString(dis);
        Ly = dis.readFloat();
        Sn = dis.readInt();
        Pixel = dis.readFloat();
        int n = dis.readInt();
        MaskReal = new byte[n];
        dis.read(MaskReal);
        MaskCenter = dis.readInt();
        if(ver > 1)
            Enabled = dis.readBoolean();
        else
            Enabled = true;
        StreamTools.versionBlockEnd(dis);
    }
}
