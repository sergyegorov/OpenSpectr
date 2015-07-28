/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.math;

/**
 *
 * @author root
 */
public class Stat {
    public static double calcEver(short[] input,int len){
        int ever = 0;
        for(int i = 0;i<len;i++)
            ever += input[i];
        return ever/(double)len;
    }
    
    public static double calcSKO(short[] input,int len,double ever){
        double sko = 0;
        for(int i = 0;i<len;i++){
            double dlt = input[i] - ever;
            sko += dlt*dlt;
        }
        return Math.sqrt(sko/len);
    }
    
    public static double clacEverFiltered(short[] input,int len){
        double ever = calcEver(input,len);
        double sko = calcSKO(input,len,ever);
        if(len < 10)
            return ever;
        double ret = 0;
        int n = 0;
        sko *= Math.sqrt(2);
        for(int i = 0;i<len;i++){
            double dlt = Math.abs(ever-input[i]);
            if(dlt > sko)
                continue;
            ret += input[i];
            n ++;
        }
        if(n < 10)
            return ever;
        return ever / n;
    }
}
