/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.mls;

/**
 *
 * @author root
 */
public class Mls {
    public String get(String msg){
        return "."+msg;
    }
    
    public String get(String msg,String ... params){
        String ret = "."+msg;
        for(String par : params)
            ret += " "+par;
        return ret;
    }
}
