package org.tl.ops.comp.editors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */
public class AbstractParameterDriver {
    Connection connection;
    PreparedStatement updateStatement; 
    PreparedStatement selectStatement;
    protected String parameterName;
    public AbstractParameterDriver(String parameterName,Connection connection,
            String defaultValue) throws SQLException{
        this.connection = connection;
        this.parameterName = parameterName;
        modefiedFlag = false;
        try {
            connection.prepareStatement("select * from t_parameters").execute();
        } catch (SQLException ex) {
            Statement st = connection.createStatement();
            st.executeQuery("create table t_parameters (name VARCHAR(64) NOT NULL,value VARCHAR(1024) NOT NULL, PRIMARY KEY(name))");
            connection.commit();
        }
        updateStatement = connection.prepareStatement("UPDATE t_parameters SET value = ? where name = '"+parameterName+"'");
        selectStatement = connection.prepareStatement("SELECT value FROM t_parameters where name = '"+parameterName+"'");
        ResultSet rs = selectStatement.executeQuery();
        if(rs.next() == false){
            connection.createStatement().execute("INSERT INTO t_parameters (name,value) values ('"+
                    parameterName+"','"+defaultValue+"')");
            connection.commit();
        }
    }
    
    boolean modefiedFlag;
    public boolean isModefied(){
        return modefiedFlag;
    }
    
    String newValue;
    public void setValue(String value){
        newValue = value;
        modefiedFlag = true;
    }
    
    public String getValue() throws SQLException{
        ResultSet rs = selectStatement.executeQuery();
        if(rs.next())
            return rs.getString(1);
        return null;
    }
    
    public synchronized void commit() throws SQLException{
        if(modefiedFlag == false)
            return;
        updateStatement.setString(1, newValue);
        updateStatement.executeUpdate();
        modefiedFlag = false;
    }
}
