/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.tl.log.TLLog;
import org.tl.mls.Mls;
import static org.tl.ops.Common.LineDb;
import org.tl.ops.comp.SetupDialog;
import org.tl.ops.db.LineLib;
import org.tl.ops.dev.data.SpectralRawDispers;

/**
 *
 * @author root
 */
public class Common {
    public static String DBDir = "Data"+File.separator;
    public static TLLog Log;
    public static Mls Mls = new org.tl.mls.Mls();
    
    static SpectralRawDispers curDisp;
    public static SpectralRawDispers getDispers(){
        if(curDisp == null){
            curDisp = new SpectralRawDispers(Setup.getDevice().getSensorSizes());
        }
        return curDisp;
    }
    
    public static SetupDialog Setup;
    public static LineLib LineDb;
    public static void init() throws ClassNotFoundException, IOException{
        Class.forName("org.hsqldb.jdbc.JDBCDriver" );
        File db = new File(DBDir);
        if(db.exists() == false)
            db.mkdirs();
        DBDir = db.getAbsolutePath()+File.separator;
        LineDb = new org.tl.ops.db.LineLib("lib"+File.separator+"data.bin");
    }
    
    static Connection commonConnection;
    public static Connection getDbConnectionCommon() throws SQLException{
        if(commonConnection == null){
            String workingDir = DBDir + "Setup"+File.separator+"Common";
            commonConnection = DriverManager.getConnection("jdbc:hsqldb:file:"+workingDir, "SA", "");
        }
        return commonConnection;
    }
}
