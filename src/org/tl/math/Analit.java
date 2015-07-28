/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.math;

import org.tl.util.DebugTools;

/**
 *
 * @author root
 */
public class Analit {
    public static short[] diff(short[] data){
        short[] ret = new short[data.length];
        for(int i = 1;i<ret.length-1;i++)
            ret[i] = (short)(data[i+1] - data[i-1]);
        return ret;
    }
    
    public static short[] mix(short[] dataMin,short[] dataMax) throws TLExceptionMath{
        short overloadMaxValue = 0;
        short overloadMinValue = 0;
        double[] min = new double[dataMin.length];
        double[] max = new double[dataMax.length];
        for(int i = 0;i<dataMin.length;i++){
            min[i] = dataMin[i];
            max[i] = dataMax[i];
            if(dataMin[i] > overloadMinValue)
                overloadMinValue = dataMin[i];
            if(dataMax[i] > overloadMaxValue)
                overloadMaxValue = dataMax[i];
        }
        overloadMaxValue = (short)(overloadMaxValue * 7 / 10);
        overloadMinValue = (short)(overloadMinValue * 7 / 10);
        boolean[] en = new boolean[dataMin.length];
        for(int i = 0;i<dataMin.length;i++){
            if(dataMin[i] > overloadMinValue || dataMax[i] > overloadMaxValue)
                en[i] = false;
            else
                en[i] = true;
        }
        return mix(en,dataMin,dataMax,overloadMaxValue);
    }
    
    public static short[] mix(boolean[] en,short[] dataMin,short[] dataMax,short overloadMaxValue) throws TLExceptionMath{
        //boolean[] en = new boolean[dataMin.length];
        double[] min = new double[dataMin.length];
        double[] max = new double[dataMax.length];
        for(int i = 0;i<dataMin.length;i++){
            //en[i] = dataMin[i] < maxValue && dataMax[i] < maxValue;
            min[i] = dataMin[i];
            max[i] = dataMax[i];
        }
        Function fk = new Function(Function.FType.Poly1, en, min, max, true);
        DebugTools.showFunction(null,"org.tl.math.Analit.mix trunslate function", fk, en, min, max);
        double[] val = new double[dataMin.length];
        double maxVal = 0;
        for(int i = 0;i<dataMin.length;i++){
            if(max[i] < overloadMaxValue){
                val[i] = max[i];//
            } else {
                val[i] = fk.calcDirect(min[i]);;
            }
            maxVal = val[i];
        }
        short[] ret = new short[val.length];
        for(int i = 0;i<val.length;i++){
            ret[i] = (short)(val[i] * overloadMaxValue / maxVal);
        }
        return ret;
    }
    
    static byte[] getShiftedMask(byte[] mask,double factor){
        //factor = -factor;
        byte[] retMask = new byte[mask.length];
        if(factor < 0){
            double left = -factor;
            double center = 1 - left;
            for(int i = 1;i<mask.length-1;i++){
                double val = mask[i - 1]*left + mask[i]*center;
                if(val > 127)
                    val = 127;
                if(val < -127)
                    val = -127;
                retMask[i] = (byte)val;
            }
        } else {
            double right = factor;
            double center = 1 - right;
            for(int i = 1;i<mask.length-1;i++){
                double val = mask[i]*center + mask[i + 1]*right;
                if(val > 127)
                    val = 127;
                if(val < -127)
                    val = -127;
                retMask[i] = (byte)val;
            }
        }
        return retMask;
    }
    
    public static double findPatternPosition(short[] spectr,byte[] mask,float from,float to){
        double max = 0;
        int maxX = -1;
        double cor_mask = 0;
        for(byte val : mask)
            if(val != 0)
                cor_mask ++;
        for(int x = (int)from;x<(int)to;x++){
            double tmp = 0;
            int cor = 0;
            for(int i = 0;i<mask.length;i++){
                if(i < 0 || i+x < 32 ||
                        i >= mask.length ||
                        i+x >= spectr.length-32)
                    continue;
                tmp += spectr[i+x]*mask[i];
                if((int)(spectr[i+x]) != 0 && mask[i] != 0)
                    cor ++;
            }
            if(cor / cor_mask < 0.5)
                continue;
            if(max < tmp){
                max = tmp;
                maxX = x;
            }
        }
        if(maxX > 0){
            double tinyMax = 0;
            double tinyMaxX = -1;
            double maxPos;
            maxPos = maxX + 1; 
            for(double cur = maxX - 1;cur<=maxPos;cur+=0.1){
                byte[] newMask = getShiftedMask(mask,cur-(int)cur);
                double tmp = 0;
                int cor = 0;
                for(int i = 0;i<mask.length;i++){
                    tmp += spectr[(int)cur+i]*newMask[i];
                }
                if(tinyMax < tmp){
                    tinyMax = tmp;
                    tinyMaxX = cur;
                }
            }
            return tinyMaxX;
        }
        return -1;
    }
}
