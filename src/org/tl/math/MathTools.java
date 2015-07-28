/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.math;

import java.util.Vector;

/**
 *
 * @author root
 */
public class MathTools {
    public static String getGoodValue(double v,int count)
	{
		int c = 0;
		
		if(v == 0)
			return "0";
		
		double val = v;
		if(val < 0)
			val *= -1;
		while(val < 1)
		{
			val *= 10;
			c ++;
		}
		c += count;
				
		String ret = String.format("%."+c+"f",v);
                if(ret.endsWith(".0") || ret.endsWith(",0"))
                    ret = ret.substring(0,ret.length()-2);
                return ret;
	}
	
	public static double getEverSmart(Vector<Double> vect,Vector<Boolean> use,double trust)
	{
		double ever = 0;
		int found = 0;
		for(int i = 0;i<vect.size();i++)
		{
			if(use.get(i) == true)
			{
				ever += vect.get(i);
				found ++;
			}
		}
		
		if(found == 0)
			return getEver(vect);
		
		if(found < 5)
			return ever;

		double sko = 0;
		for(int i = 0;i<vect.size();i++)
		{
			if(use.get(i) == false)
				continue;
			double v = ever - vect.get(i);
			sko += v*v;
		}
		sko = Math.sqrt(sko) / (vect.size()-1);
		
		double trust_sko = sko * trust;
		double ret = 0;
		double ret_count = 0;
		for(int i = 0;i<vect.size();i++)
		{
			if(use.get(i) == false)
				continue;
			double v = vect.get(i);
			double dlt = Math.abs(ever-v);
			if(dlt > trust_sko)
			{
				use.set(i, false);
				continue;
			}
			ret += v;
			ret_count ++;
		}
		if(ret_count < 4 || ret_count < vect.size()*0.8)
			return ever;
		return ret/ret_count;
	}
	
	public static double getEver(Vector<Double> vect)
	{
		double ret = 0;
		for(int i = 0;i<vect.size();i++)
			ret += vect.get(i);
		return ret / vect.size();
	}
	
	/**
	 * This method returns min and max value in array
	 * @param vals double[] - values
	 * @return double[] {minimal,maximal}
	 */
	public static double[] findMinMax(double[] vals)
	{
		double ret[] = {vals[0],vals[0]};
		for(int i = 0;i<vals.length;i++)
		{
			ret[0] = java.lang.Math.min(ret[0], vals[i]);
			ret[1] = java.lang.Math.max(ret[1], vals[i]);
		}
		return ret;
	}
	
	/**
	 * This method returns min and max value in array
	 * @param vals double[] - values array
	 * @param ret - double[]{minimal,maximal} - initial values
	 * @return double[] {minimal,maximal}
	 */
	public static double[] findMinMax(double[] vals,double ret[])
	{
		if(ret == null)
		{
			ret = new double[2];
			ret[0] = vals[0];
			ret[1] = vals[0];
		}
		for(int i = 0;i<vals.length;i++)
		{
			ret[0] = java.lang.Math.min(ret[0], vals[i]);
			ret[1] = java.lang.Math.max(ret[1], vals[i]);
		}
		return ret;
	}
	/**
	 * Returns everage value of float array
	 * @param data - float array
	 * @return float - everage value
	 */
	public static float getEver(float[] data)
	{
		double ret = 0;
		for(int i = 0;i<data.length;i++)
			ret += data[i];
		ret /= data.length;
		return (float)ret;
	}
	
	public static float parseFloat(String str)
	{
		return Float.parseFloat(str);
	}
	
	public static double parseDouble(String str)
	{
		return Double.parseDouble(str);
	}
	
	public static boolean isValid(double val)
	{
		if(Double.isInfinite(val) ||
				Double.isNaN(val))
			return false;
		return true;
	}
	
	public static double round(double d, int c) 
	{
		int temp=(int)Math.round((d*Math.pow(10,c)));
		double ret = (((double)temp)/Math.pow(10,c));
		return ret;
	}
	
	public static double getGoodValue(double lVal, double lStep)
    {
        if (isValid(lVal) == false || isValid(lStep) == false)
            return 0;
        
        if (lStep >= 1)
            return (int)lVal;

        if (lStep == 0)
            return lVal;

        if (lVal == 0)
            return 0;

        int por = 0;
        while (lStep < 1)
        {
            lStep *= 10;
            por++;
        }

        return round(lVal, por);
    }
    
    public static double[] getGoodValues(double lfrom, double lto, int n)
    {
        if (isValid(lfrom) == false || isValid(lto) == false)
            return null;

        if (n <= 0)
            n = 1;

        double dlt = lto - lfrom;
        double step = dlt / n;

        if (step < 0.00000000001)
            step = 0.00000000001;

        int por = 0;
        double from = lfrom - step;
        double dir;
        if (step < 1)
            dir = 10;
        else
            dir = 0.1;

        while (step < 1 || step > 10)
        {
            from *= dir;
            step *= dir;
            por++;
        }

        from = (int)from;
        step = (int)step;
        for (int i = 0; i < por; i++)
        {
            step /= dir;
            from /= dir;
        }

        for (int i = 0; i < 10 && from >= lfrom; i++)
            from -= step;

        int rn = (int)((lto - from) / step);

        if (rn > 0 && rn < 200)
        {
            rn++;
            double[] ret = new double[rn];
            for (int i = 0; i < rn; i++)
            {
                ret[i] = getGoodValue(from, step);
                from += step;
            }
            return ret;
        }
        return null;
    }
    
    static double DefaultSteps[] = {
                0.0000001,0.0000002,0.0000005,
                0.000001,0.000002,0.000005,
                0.00001,0.00002,0.00005,
                0.0001,0.0002,0.0005,
                0.001,0.002,0.005,
                0.01,0.02,0.05,
                0.1,0.2,0.5,
                1,2,5,
                10,20,50,
                100,200,500,
                1000,2000,5000,
                10000,20000,50000,
                100000,200000,500000,
                1000000,2000000,5000000};
        
    public static double selectStep(double from,double to,int count){
        double dlt = to-from;
        for(int i = 0;i<DefaultSteps.length;i++){
            int count_cand = (int)(dlt/DefaultSteps[i]);
            if(count_cand > count)
                return DefaultSteps[i-1];
            if(count_cand == count)
                return DefaultSteps[i];
        }
        return dlt/count;
    }
}
