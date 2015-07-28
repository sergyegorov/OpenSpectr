/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev.data;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author root
 */
public class SpectralMeasuring {
    String folder;
    public SpectralMeasuring(String folder) throws IOException{
        if(folder.endsWith(File.separator) == false)
            folder += File.separator;
        this.folder = folder;
        File fl = new File(folder);
        if(fl.exists() == false)
            if(fl.mkdir() == false)
                throw new IOException("Can't make direaction"+folder);
    }
    
    File getFile(String path){
        if(path == null)
            return new File(folder);
        return new File(folder+path);
    }
    
    public void delete(){
        File base = getFile(null);
        File[] list = base.listFiles();
        for(File fl : list)
            fl.delete();
        base.delete();
    }
    
    public SpectralRawData getData() throws IOException{
        return RawDataCash.load(folder);//ret;
    }
    
    public void setData(SpectralRawData data) throws IOException{
        data.setupBaseDirectory(folder);
        data.store();
        RawDataCash.save(folder, data);
    }
    
    public SpectralPreview getPreview() throws IOException{
        return new SpectralPreview(new File(folder));
    }
}
