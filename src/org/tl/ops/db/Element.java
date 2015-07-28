/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.db;

/**
 *
 * @author root
 */
public class Element {
    public String Name;
    public String FullName;
    public int Num;
    public ElementTypes Type;
    public int Col, Row;
    public boolean RadioActive;

    public Element(int num, String name,String name_full,
        ElementTypes type,
        int col,int row)
    {
        Num = num;
        Name = name;
        FullName = name_full;
        Type = type;
        Col = col;
        Row = row;
    }

    public Element(int num, String name,String name_full,
        ElementTypes type,
        int col,int row,boolean radio_active)
    {
        Num = num;
        Name = name;
        FullName = name_full;
        Type = type;
        Col = col;
        Row = row;
        RadioActive = radio_active;
    }
}
