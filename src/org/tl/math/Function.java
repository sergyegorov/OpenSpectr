/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.math;

import java.util.*;

import Jama.Matrix;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.tl.util.StreamTools;

/**
 *
 * @author root
 */
public class Function
{
    /**
     * This enum of supported function types
     * @author TхЁц
     *
     */
    public enum FType{
            Poly1,
            Poly2,
            Poly3,
            Poly4,
            Sqrt,
            Log
    }

    public static String[] getTypeSet()
    {
        String[] ret = {
                FType.Poly1.toString(),
                FType.Poly2.toString(),
                FType.Poly3.toString(),
                FType.Poly4.toString(),
                FType.Sqrt.toString(),
                FType.Log.toString()};
        return ret;
    }

    public static FType typeFromString(String type)
    {
        if(type == null)
                return FType.Poly1;
        if(type.equals(FType.Log.toString())) return FType.Log;
        if(type.equals(FType.Sqrt.toString())) return FType.Sqrt;
        if(type.equals(FType.Poly2.toString())) return FType.Poly2;
        if(type.equals(FType.Poly3.toString())) return FType.Poly3;
        if(type.equals(FType.Poly4.toString())) return FType.Poly4;
        return FType.Poly1;
    }

    public static int getIndexFromType(FType type)
    {
        if(type == null)
                return 0;
        if(type == FType.Poly2) return 1;
        if(type == FType.Poly3) return 2;
        if(type == FType.Poly4) return 3;
        if(type == FType.Sqrt) return 4;
        if(type == FType.Log) return 5;
        return 0;
    }

    public static FType getTypeFromIndex(int index)
    {
        switch(index)
        {
        default: return FType.Poly1;
        case 1: return FType.Poly2;
        case 2: return FType.Poly3;
        case 3: return FType.Poly4;
        case 4: return FType.Sqrt;
        case 5: return FType.Log;
        }	
    }

    public double getK(int index)
    {
        return K[index];
    }
    
    /*public void setK(int index,double k){
        K[index] = k;
    }*/
    public void setLine(double k0,double k1){
        K = new double[2];
        K[0] = k0;
        K[1] = k1;
        // y = k0 + k1 * x;
        // x = (y - k0)/k1;
        Kr[0] = -k0/k1;
        Kr[1] = 1/k1;
    }

    //Matrix C;
    double[] K = {0,1},Kr = {0,1};
    public double MinX = Double.MAX_VALUE;
    public double MaxX = -Double.MAX_VALUE;
    public double MinY = Double.MAX_VALUE;
    public double MaxY = -Double.MAX_VALUE;
    public double TrustK = 0;
    public boolean LineExtend;

    boolean isXValid(double x)
    {
        if(TrustK == 0)
                return true;
        if(MathTools.isValid(x) == false)
                return false;
        double dlt = MaxX - MinX;
        dlt *= TrustK;
        return !(x < (MinX - dlt) || x > (MaxX + dlt));
    }

    boolean isYValid(double y)
    {
        if(TrustK == 0)
                return true;
        if(MathTools.isValid(y) == false)
                return false;
        double dlt = MaxY - MinY;
        dlt *= TrustK;
        return !(y < (MinY - dlt) || y > (MaxY + dlt));
    }

    public FType getType()
    {
        return Type;
    }
    FType Type = FType.Poly1;
    
    /**
     * This constructor creates diagonal line function
     */
    public Function()
    {

    }

    /**
     * This contructor creates
     * @param type - FType enum...
     * @param enable - boolean[] flags
     * @param x - double[]
     * @param y - double[]
     * @throws os4.analit.TLExceptionMath
     */
    public Function(FType type,boolean enable[],double x[],double y[],boolean lineExtend)
            throws TLExceptionMath
    {
        Init(type,enable,x,y,lineExtend);
    }
    
    public Function(Function src_fk){
        Type = src_fk.Type;
        K = (double[])src_fk.K.clone();
        Kr = (double[])src_fk.Kr.clone();
        MinX = src_fk.MinX;
        MaxX = src_fk.MaxX;
        MinY = src_fk.MinY;
        MaxY = src_fk.MaxY;
        TrustK = src_fk.TrustK;
        LineExtend = src_fk.LineExtend;
    }
    
    public void save(DataOutputStream os) throws IOException{
        StreamTools.versionBlockBegin(os, 2);
        os.writeInt(getIndexFromType(Type));
        StreamTools.writeDoubleArray(K, os);
        StreamTools.writeDoubleArray(Kr, os);
        os.writeDouble(MinX);
        os.writeDouble(MaxX);
        os.writeDouble(MinY);
        os.writeDouble(MaxY);
        os.writeDouble(TrustK);
        os.writeBoolean(LineExtend);
        StreamTools.versionBlockEnd(os);
    }
    
    public void load(DataInputStream is) throws IOException{
        int ver = StreamTools.versionBlockBegin(is, 1, 2);
        Type = getTypeFromIndex(is.readInt());// os.writeInt(getIndexFromType(Type));
        K = StreamTools.readDoubleArray(is);
        Kr = StreamTools.readDoubleArray(is);
        MinX = is.readDouble();
        MaxX = is.readDouble();
        MinY = is.readDouble();
        MaxY = is.readDouble();
        TrustK = is.readDouble();
        if(ver > 1)
            LineExtend = is.readBoolean();
        StreamTools.versionBlockEnd(is);
    }

    /**
     * This method reinit class
     * @param type - FType enum...
     * @param enable - boolean[] flags
     * @param x - double[]
     * @param y - double[]
     * @param lineExtend
     * @return 
     * @throws org.tl.math.TLExceptionMath
     */
    final public boolean Init(FType type,
            boolean enable[],double x[],double y[],
            boolean lineExtend)
                    throws TLExceptionMath
    {
        LineExtend = lineExtend;
        double minx,maxx;
        double miny,maxy;
        switch(type)
        {
        case Log: 	
            minx = Double.MIN_VALUE*2; maxx = Double.MAX_VALUE;
            miny = Double.MIN_VALUE*2; maxy = Double.MAX_VALUE; break;
        case Sqrt: 	
            minx = 0; maxx = Double.MAX_VALUE; 
            miny = -Double.MAX_VALUE; maxy = Double.MAX_VALUE; break;
        default: 	
            minx = -Double.MAX_VALUE; maxx = Double.MAX_VALUE; 
            miny = -Double.MAX_VALUE; maxy = Double.MAX_VALUE; break;
        }

        int count = 0;
        HashSet<Double> xs = new HashSet<>();
        for(int i = 0;i<x.length;i++)
        {
            if(enable[i] == false)
                    continue;

            if(Double.isInfinite(x[i]) || Double.isNaN(x[i]) || 
                    x[i] < minx || x[i] > maxx || 
                    y[i] < miny || y[i] > maxy)
            {
                //Log.war("Invalid x data in function");
                continue;
            }

            if(Double.isInfinite(y[i]) || Double.isNaN(y[i]))
            {
                //Log.war("Invalid y data in function");
                continue;
            }

            xs.add(x[i]);
            count ++;
        }

        double nx[] = new double[count],
                        ny[] = new double[count];
        for(int i = 0,j = 0;i<x.length;i++)
        {
            if(	enable[i] == false ||
                Double.isInfinite(x[i]) || Double.isNaN(x[i])  || 
                x[i] < minx || x[i] > maxx || x[i] < minx || x[i] > maxx ||
                Double.isInfinite(y[i]) || Double.isNaN(y[i]))
                    continue;
            nx[j] = x[i];
            ny[j] = y[i];
            j++;
            if(MinX > x[i])	MinX = x[i];
            if(x[i] > MaxX)	MaxX = x[i];
            if(MinY > y[i])	MinY = y[i];
            if(y[i] > MaxY)	MaxY = y[i];
        }

        if(xs.size() < 2)
            return false;
        else
        {
            switch(type)
            {
            case Poly2:
                if(xs.size() < 3)
                    type = FType.Poly1;
                break;
            case Poly3:
                if(xs.size() < 3)
                    type = FType.Poly1;
                else
                {
                    if(xs.size() < 4)
                        type = FType.Poly2;
                }
            case Poly4:
                if(xs.size() < 3)
                    type = FType.Poly1;
                else
                {
                    if(xs.size() < 4)
                        type = FType.Poly2;
                    else
                    {
                        if(xs.size() < 5)
                            type = FType.Poly3;
                    }
                }
                break;
            case Log:
                if(xs.size() < 3)
                    type = FType.Poly1;
                break;
            case Sqrt:
                if(xs.size() < 3)
                    type = FType.Poly1;
                break;
            case Poly1:
                    break;
            default:
                    break;
            }
        }

        Type = type;

        Matrix B,A;
        Matrix Br,Ar;
        switch(type)
        {
        case Poly1:
            A = new Matrix(nx.length,2);	B = new Matrix(nx.length,1);
            Ar = new Matrix(nx.length,2);	Br = new Matrix(nx.length,1);
            for(int i = 0;i<nx.length;i++)
            {
                A.set(i, 0, 1);		
                A.set(i, 1, nx[i]);
                B.set(i,0,ny[i]);
                Ar.set(i, 0, 1);	Ar.set(i, 1, ny[i]);
                Br.set(i,0,nx[i]);
            }
            break;
        case Poly2:
            A = new Matrix(nx.length,3);	B = new Matrix(nx.length,1);
            Ar = new Matrix(nx.length,3);	Br = new Matrix(nx.length,1);
            for(int i = 0;i<nx.length;i++)
            {
                A.set(i, 0, 1);		
                A.set(i, 1, nx[i]);		
                A.set(i, 2, nx[i]*nx[i]);
                B.set(i,0,ny[i]);
                Ar.set(i, 0, 1);	Ar.set(i, 1, ny[i]);	Ar.set(i, 2, ny[i]*ny[i]);
                Br.set(i,0,nx[i]);
            }
            break;
        case Poly3:
            A = new Matrix(nx.length,4);	B = new Matrix(nx.length,1);
            Ar = new Matrix(nx.length,4);	Br = new Matrix(nx.length,1);
            for(int i = 0;i<nx.length;i++)
            {
                A.set(i, 0, 1);		A.set(i, 1, nx[i]);		A.set(i, 2, nx[i]*nx[i]);		A.set(i, 3, nx[i]*nx[i]*nx[i]);
                B.set(i,0,ny[i]);
                Ar.set(i, 0, 1);	Ar.set(i, 1, ny[i]);	Ar.set(i, 2, ny[i]*ny[i]);		Ar.set(i, 3, ny[i]*ny[i]*ny[i]);
                Br.set(i,0,nx[i]);
            }
            break;
        case Poly4:
            A = new Matrix(nx.length,4);	B = new Matrix(nx.length,1);
            Ar = new Matrix(nx.length,4);	Br = new Matrix(nx.length,1);
            for(int i = 0;i<nx.length;i++)
            {
                A.set(i, 0, 1);		A.set(i, 1, nx[i]);		A.set(i, 2, nx[i]*nx[i]);		A.set(i, 3, nx[i]*nx[i]*nx[i]);			A.set(i, 3, nx[i]*nx[i]*nx[i]*nx[i]);
                B.set(i,0,ny[i]);
                Ar.set(i, 0, 1);	Ar.set(i, 1, ny[i]);	Ar.set(i, 2, ny[i]*ny[i]);		Ar.set(i, 3, ny[i]*ny[i]*ny[i]);		Ar.set(i, 3, ny[i]*ny[i]*ny[i]*ny[i]);
                Br.set(i,0,nx[i]);
            }
            break;
        default:
            throw new TLExceptionMath("Unsupported type of the function");
        }

        Matrix C = A.solve(B);
        K = C.getColumnPackedCopy();
        C = Ar.solve(Br);
        Kr = C.getColumnPackedCopy();

        return true;
    }

    /**
     * Direct clculation
     * @param x - vluae
     * @return y value
     */
    public double calcDirect(double x)
    {
            if(isXValid(x) == false) return Double.NaN;
            switch(Type)
            {
            default:	return K[0]+K[1]*x;
            case Poly2:	return K[0]+K[1]*x+K[2]*x*x;
            case Poly3:	return K[0]+K[1]*x+K[2]*x*x+K[3]*x*x*x;
            case Poly4:	return K[0]+K[1]*x+K[2]*x*x+K[3]*x*x*x+K[4]*x*x*x*x;
            }
    }

    /**
     * Reverce calculation
     * @param y - input value
     * @return x value
     */
    public double calcRev(double y)
    {
        double preliminaryX = calcRevPrelim(y);
        if(preliminaryX >= MinX && preliminaryX <= MaxX)
            return preliminaryX;
        double realY = calcDirect(preliminaryX);
        double a = 0.8;
        int iterations = 100;
        double prevDlt = Math.abs(realY-y);
        double prevDx = 0;
        while(Math.abs(prevDlt)>0.0000001 && iterations > 0){
            iterations --;
            double dltY = realY-y;
            double k = calcDerivative(preliminaryX);
            if(Math.abs(k) < 0.000001)
                return preliminaryX;
            double dltX = dltY / k;
            if(dltX == 0)
                prevDx = dltX;
            dltX *= a;
            preliminaryX -= dltX;
            realY = calcDirect(preliminaryX);
            double dltCand = Math.abs(realY-y);
            if(dltCand > prevDlt || prevDx*dltX < 0){
                a /= 2;
                preliminaryX += dltX;
                realY = calcDirect(preliminaryX);
            } else 
                prevDlt = dltCand;
        }
        return preliminaryX;
    }
    
    public double calcDerivative(double x){
        if(isYValid(x) == false) return Double.NaN;
        switch(Type)
        {
        default:	return K[1];
        case Poly2:	return K[1]+2*K[2]*x;
        case Poly3:	return K[1]+2*K[2]*x+3*K[3]*x*x;
        case Poly4:	return K[1]+2*K[2]*x+3*K[3]*x*x+4*K[4]*x*x*x;
        }
    }
    
    private double calcRevPrelim(double y)
    {
            if(isYValid(y) == false) return Double.NaN;
            switch(Type)
            {
            default:	return Kr[0]+Kr[1]*y;
            case Poly2:	return Kr[0]+Kr[1]*y+Kr[2]*y*y;
            case Poly3:	return Kr[0]+Kr[1]*y+Kr[2]*y*y+Kr[3]*y*y*y;
            case Poly4:	return Kr[0]+Kr[1]*y+Kr[2]*y*y+Kr[3]*y*y*y+Kr[4]*y*y*y*y;
            }
    }
    
    public String toStringShort(){
        String str = MathTools.getGoodValue(K[0],1);
        for(int i = 1;i<K.length;i++){
            String val = MathTools.getGoodValue(K[i], 2+i*2);// formatToString(K[i]);
            if(val.equals("0") || val.equals("-0"))
                continue;
            if(K[i]>=0)
                str += "+" + val +"*x";
            else 
                str += val + "*x";
            if(i > 1)
                str += "^" + i;
        }
        return str;
    }
    
    /**
     * Store all internal parameters to string
     * @return 
     */
    @Override
    public String toString()
    {
            String str = "#"+Type+";"+K.length+";";
            for(int i = 0;i<K.length;i++)
            {
                    str += K[i]+";";
                    str += Kr[i]+";";
            }
            str += MinX+";";
            str += MaxX+";";
            str += MinY+";";
            str += MaxY+";";
            str += TrustK+";";
            return str;
    }
    
    public String correctXScaleBy(int sn,Function fk) throws TLExceptionMath{
        double[] x = {0,MaxX/3,MaxX*2/3,MaxX};
        double[] y = {
            calcDirect(x[0]),
            calcDirect(x[1]),
            calcDirect(x[2]),
            calcDirect(x[3])
        };
        boolean en[] = {true,true,true,true};
        for(int i = 0;i<x.length;i++)
            x[i] = fk.calcDirect(x[i]);
        //Fk[sn] = new Function(Fk[sn].getType(),en,x,y);
        Init(Type, en, x, y, fk.LineExtend);
        return "Corrected by polinome: "+fk.toStringShort();
    }
}
