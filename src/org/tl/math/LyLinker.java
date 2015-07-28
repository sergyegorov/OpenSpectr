/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.math;

import java.util.ArrayList;
import org.tl.ops.dev.data.SpectralMask;
import org.tl.ops.dev.data.SpectralPreview;
import org.tl.ops.dev.data.SpectralRawDispers;
import org.tl.util.DebugTools;
import org.tl.util.GUITools;

/**
 *
 * @author root
 */
public class LyLinker {
    public static SpectralRawDispers link(SpectralPreview currentSpectr,
            ArrayList<SpectralMask> masks,SpectralRawDispers defaultDispers) throws TLExceptionMath{
        int sn = currentSpectr.Dispers.getSensorCount();
        ArrayList<double[]>[] links = new ArrayList[sn];
        for(int i = 0;i<sn;i++)
            links[i] = new ArrayList<double[]>();
        short[][] values = new short[sn][];
        for(int s = 0;s<sn;s++){
            int size = currentSpectr.Dispers.getSensorSize(s);
            values[s] = new short[size];
            short[] min = new short[size];
            short[] max = new short[size];
            boolean[] en = new boolean[size];
            short maxLevel = currentSpectr.OverloadLevels[s];
            for(int i = 0;i<size;i++){
                if(currentSpectr.MaximalValues[s][i] < maxLevel && currentSpectr.MinimalValues[s][i] < maxLevel)
                    en[i] = true;
                else
                    en[i] = false;
                min[i] = (short)(currentSpectr.MinimalValues[s][i] - currentSpectr.NullValues[s][i]);
                max[i] = (short)(currentSpectr.MaximalValues[s][i] - currentSpectr.NullValues[s][i]);
            }
            values[s] = Analit.mix(en,min,max,maxLevel);
        }
        
        for(int s = 0;s<sn;s++)
            values[s] = Analit.diff(PicDetector.pickFilter(values[s]));
        
        final int searchingRange = 25;
        for(SpectralMask mask : masks){
            if(mask.Enabled == false)
                continue;
            int n = mask.Sn;
            float pixel = mask.Pixel-mask.MaskCenter;
            double cur_position = Analit.findPatternPosition(values[n], mask.getDiff(), 
                    pixel-searchingRange, pixel+searchingRange);
            if(cur_position > 0){
                double[] tmp = new double[2];
                tmp[0] = pixel+mask.MaskCenter;
                tmp[1] = mask.getLy();
                links[n].add(tmp);
            }
        }
        
        SpectralRawDispers ret = new SpectralRawDispers(defaultDispers);
        for(int s = 0;s<links.length;s++){
            ArrayList<double[]> snShifts = links[s];
            double[] x,y;
            boolean[] en;
            if(snShifts.size() >= 0){
                switch(snShifts.size()){
                    case 0:
                    case 1:
                        ret.setFunction(s, null);
                        break;
                    default:
                        en = new boolean[snShifts.size()];
                        x = new double[snShifts.size()];
                        y = new double[snShifts.size()];
                        double minx = Double.MAX_VALUE;
                        double maxx = -Double.MAX_VALUE;
                        for(int i = 0;i<snShifts.size();i++){
                            double[] tmp = snShifts.get(i);
                            en[i] = true;
                            x[i] = tmp[0];
                            y[i] = tmp[1];
                            if(minx > x[i])
                                minx = x[i];
                            if(maxx < x[i])
                                maxx = x[i];
                        }
                        Function fk;
                        if(en.length < 3)
                            fk = new Function(Function.FType.Poly1, en, x, y,false);
                        else {
                            if(en.length < 10 || (maxx-minx) < 1000)
                                fk = new Function(Function.FType.Poly2, en, x, y,false);
                            else 
                                fk = new Function(Function.FType.Poly3, en, x, y,false);
                        }
                        //DebugTools.showFunction(null, fk, en, x, y);
                        ret.setFunction(s, fk);
                        break;
                }
            }
        }
        if(ret.getFunction(0) == null){
            Function linefk = null;
            int[] sizes = new int[sn];
            //int[] position = new int[sn];
            int sensorInited = 0;
            for(int s = 0;s<links.length;s++){
                sizes[s] = currentSpectr.Dispers.getSensorSize(s);
                Function cand = ret.getFunction(s);
                if(cand != null){
                    sensorInited = s;
                    boolean[] en = {true,true};
                    double[] x = {0,5};
                    double[] y = {cand.calcDirect(x[0]),cand.calcDirect(x[1])};
                    linefk = new Function(Function.FType.Poly1, en, x, y, false);
                    break;
                }
            }
            int position = 0;
            if(linefk != null){
                for(int s = sensorInited-1;s>=0;s--){
                    position -= sizes[s];
                    boolean[] en = {true,true};
                    double[] x = {0,10};
                    double[] y = {linefk.calcDirect(position+x[0]),linefk.calcDirect(position+x[1])};
                    Function fk = new Function(Function.FType.Poly1, en, x, y, false);
                    ret.setFunction(s, fk);
                }
            }
        }
        
        Function prevFk = null;
        int prevSize = 0;
        for(int s = 0;s<links.length;s++){
            Function curFk = ret.getFunction(s);
            if(curFk == null && prevFk != null){
                boolean[] en = {true,true};
                double[] x = {0,10};
                double[] y = {prevFk.calcDirect(prevSize+x[0]),prevFk.calcDirect(prevSize+x[1])};
                curFk = new Function(Function.FType.Poly1, en, x, y, false);
                ret.setFunction(s, curFk);
            }
            prevFk = curFk;
            prevSize = currentSpectr.Dispers.getSensorSize(s);
        }
        
        for(int s = 0;s<links.length;s++){
            if(ret.getFunction(s) == null)
                ret.setFunction(s, defaultDispers.getFunction(s));
        }
        return ret;
    } 
}
