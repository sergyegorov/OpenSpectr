package org.tl.math;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */
public class MinMax2D {
    public double minX;
    public double maxX;
    public double minY;
    public double maxY;
    
    public MinMax2D(){
        minX = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;
    }
    
    public MinMax2D(MinMax2D rect){
        minX = rect.minX;
        minY = rect.minY;
        maxX = rect.maxX;
        maxY = rect.maxY;        
    }
    
    public void intersect(MinMax2D byRect){
        if(minX < byRect.minX)
            minX = byRect.minX;
        if(maxX > byRect.maxX)
            maxX = byRect.maxX;
        if(minY < byRect.minY)
            minY = byRect.minY;
        if(maxY > byRect.maxY)
            maxY = byRect.maxY;
    }
    
    public double getWidth(){
        return maxX - minX;
    }
    
    public double getHeight(){
        return maxY - minY;
    }
    
    public double getMidleX(){
        return minX + (maxX - minX)/2;
    }
    
    public double getMidleY(){
        return minY + (maxY - minY)/2;
    } 
    
    public void addValueX(double x){
        if(minX > x)
            minX = x;
        if(maxX < x)
            maxX = x;
    }
    
    public void addValueY(short[][] data){
        for(short[] dim : data){
            for(short val : dim)
                addValueY(val);
        }
    }
    
    public void addValueY(double y){
        if(minY > y)
            minY = y;
        if(maxY < y)
            maxY = y;
    }
    
    public void addValue(double x,double y){
        if(minX > x)
            minX = x;
        if(maxX < x)
            maxX = x;
        if(minY > y)
            minY = y;
        if(maxY < y)
            maxY = y;
    }
}
