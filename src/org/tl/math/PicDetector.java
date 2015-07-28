/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.math;

import java.util.ArrayList;

/**
 *
 * @author root
 */
public class PicDetector {
    public int LeftPosition,
            Center,
            RightPosition;
    
    static public short[] pickFilter(short[] data){
        short[] retData = new short[data.length];
        int from = 10;
        int noise = findNoiseLevel(data)*2;
        for(int i = 0;true;i++){
            PicDetector pick = detect(data, from, 1,noise);
            if(pick == null)
                break;
            pick.copyPick(data, retData);
            from = pick.RightPosition+2;
        }
        return retData;
    }
    
    public void copyPick(short[] from,short[] to){
        for(int i = LeftPosition;i<RightPosition+1;i++)
            to[i] = from[i];
    }
    
    private PicDetector(int left,int center,int right){
        LeftPosition = left;
        Center = center;
        RightPosition = right;
    }
    
    public static short findNoiseLevel(short[] data){
        short[] tmp = data;
        short[] dlt = new short[data.length];
        int dlt_index = 0;
        for(int i = 4;i<tmp.length-4;i++){
            if(tmp[i] < tmp[i+1] && tmp[i+1] < tmp[i+2] && tmp[i+2] < tmp[i+3]){
                while(i<tmp.length-4 && tmp[i] < tmp[i+1])i++;
                continue;
            }
            if(tmp[i] > tmp[i+1] && tmp[i+1] > tmp[i+2] && tmp[i+2] > tmp[i+3]){
                while(i<tmp.length-4 && tmp[i] > tmp[i+1])i++;
                continue;
            }
            dlt[dlt_index++] = (short)Math.abs(tmp[i]-tmp[i+1]);
        }
        return (short)Stat.clacEverFiltered(dlt, dlt_index);
    }
    
    public static PicDetector detect(short[] data,int fromPixel,int step,int noise){
        if(fromPixel < 8)
            fromPixel = 8;
        //int noise = findNoiseLevel(data)*2;
        for(int i = fromPixel;i > 8 && i < data.length-8;i+=step){
            if(data[i-2] < data[i-1] && data[i+2] < data[i+1]){
                int left = i;
                while(left >= 1 && data[left-1] <= data[left])
                    left --;
                left = i - left;
                if(left < 1)
                    continue;
                int right = i;
                while(right < data.length-1 && data[right] >= data[right+1])
                    right ++;
                if(right < 1)
                    continue;
                right = right - i;
                if(left+right >=5 && ((data[i]-data[left])>noise || (data[i]-data[right])>noise))
                    return new PicDetector(i-left, i, right+i);
            }
        }
        return null;
    }
    
    /*public static PicDetector detectBackward(short[] data,int fromPixel){
        if(fromPixel < 8)
            fromPixel = 8;
        for(int i = fromPixel; i>8;i--){
            if(data[i-2] < data[i-1] && data[i+2] < data[i+1]){
                int left = i;
                while(left >= 1 && data[left-1] < data[left])
                    left --;
                left = i - left;
                int right = i;
                while(right < data.length-1 && data[right] > data[right+1])
                    right ++;
                right = right - i;
                if(right >= 2 && left >=2 && left+right >=8)
                    return new PicDetector(i-left, i, right+i);
            }
        }
        return null;
    }*/
}
