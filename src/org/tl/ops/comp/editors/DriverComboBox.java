/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.comp.editors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JComboBox;
import org.tl.ops.Common;

/**
 *
 * @author root
 */
public class DriverComboBox extends AbstractParameterDriver{
    JComboBox cBox;
    public DriverComboBox(JComboBox box,String name,Connection con,
            String defaultValue,String[] valueList) throws SQLException{
        super(name,con,defaultValue);
        cBox = box;
        box.removeAllItems();
        for(String value : valueList){
            box.addItem(value);
        }
        box.setSelectedItem(getValue());
        
        box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    setValue((String)cBox.getSelectedItem());
                }catch(Exception ex){
                    Common.Log.exception("Selection parameter error...", ex);
                }
            }
        });
    }
}
