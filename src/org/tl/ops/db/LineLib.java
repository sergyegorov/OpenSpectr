/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.db;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.tl.ops.Common;

/**
 *
 * @author root
 */
public class LineLib {
    static final Charset Ch = Charset.forName("ASCII");
    ArrayList<LineLibRecord> DataSet = new ArrayList<>();
    public LineLib(String path){
        File f = new File(path);
        if(f.exists() == false || f.isFile() == false)
            return;
        try {
            InputStream fis = new BufferedInputStream(new FileInputStream(f));
            if(fis.read() != 1)
                throw new Exception("Wrong version...");
            int count = readInt(fis);
            for(int i = 0;i<count;i++){
                LineLibRecord el = new LineLibRecord();
                el.load(fis);
                DataSet.add(el);
            }
            if(fis.read() != 33)
                throw new Exception("Wrong end of file...");
        } catch (FileNotFoundException ex) {
            Common.Log.exception("Loading lib exception...",ex);
        } catch (Exception ex) {
            Common.Log.exception("Loading lib exception...",ex);
        }
    }
    
    static final public short readByte(InputStream is) throws IOException{
        return (short)(read(is,1)&0xFF);
    }
    
    static final public short readShort(InputStream is) throws IOException{
        return (short)(read(is,2));
    }
    
    static final public int readInt(InputStream is) throws IOException{
        return (int)(read(is,4));
    }
    
    static final public float readFloat(InputStream is) throws IOException{
        return Float.intBitsToFloat(readInt(is));
    }
    
    static final public boolean readBoolean(InputStream is) throws IOException{
        return is.read() == 1;
    }
    
    static final public String readString(InputStream is) throws IOException{
        int len = is.read();
        if(len > 127)
            return null;
        byte[] buf = new byte[len];
        is.read(buf);
        String ret = new String(buf,Ch);
        return ret;
    }
    
    final static public long read(InputStream is,int len) throws IOException{
        int shift = 0;
        long ret = 0;
        for(int i = 0;i<len;i++){
            long data = is.read();
            data <<= shift;
            ret |= data;
            shift += 8;
        }
        return ret;
    } 
}
