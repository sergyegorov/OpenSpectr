/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.comp.editors;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.tl.ops.Common;

/**
 *
 * @author root
 */
public class DriverStringValue  extends AbstractParameterDriver{
    JTextField component;
    public DriverStringValue(JTextField comp,String name,Connection con,String defaultValue) throws SQLException{
        super(name,con,defaultValue);
        component = comp;
        comp.setText(getValue());
        comp.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            
            public void changed(){
                try{
                    setValue(component.getText());
                }catch(Exception ex){
                    Common.Log.exception("Value change exception "+parameterName, ex);
                }
            }
        });
    }
}
