/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author root
 */
public interface AbstractStorable {
    public void store(DataOutputStream dos) throws IOException;
    public void restore(DataInputStream dis) throws IOException;
    
    public default void store(File file) throws IOException{
        try(FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);){
            store(dos);
            dos.flush();
        }
    }
    
    public default void restore(File file) throws IOException{
        try(FileInputStream fos = new FileInputStream(file);
            DataInputStream dos = new DataInputStream(fos);){
            restore(dos);
        }
    }
}
