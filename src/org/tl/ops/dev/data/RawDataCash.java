/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author root
 */
public class RawDataCash {
    static HashMap<String,SpectralRawData> DataMap = new HashMap<>();
    
    static String checkPath(String path){
        if(path.endsWith(File.separator) == false)
            path = path + File.separator;
        return path;
    }
    
    public static void save(String path,SpectralRawData data){
        path = checkPath(path);
        DataMap.put(path, data);
    }
    
    public static SpectralRawData load(String path) throws IOException{
        path = checkPath(path);
        if(DataMap.containsKey(path))
            return DataMap.get(path);
        SpectralRawData ret = new SpectralRawData(new File(path));
        ret.restore();
        return ret;
    }
}
