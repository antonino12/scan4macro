/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scan4macro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

/**
 *
 * @author World2016
 */
public class CSVLoader {
    
    protected String fileName = "";
    protected String[] headerFields1 = null;
    protected String[] headerFields2 = null;
    protected Vector<String[]> fields1 = new Vector<String[]>();
    protected Vector<String[]> fields2 = new Vector<String[]>();
    protected int fieldsCount = 0;
    
    protected final static String PH_WHITE = "white listed";
    protected final static String PH_PACKAGE = "Packages";
    protected final static String PH_COMMENT = "Comment";
    
    protected final static String CH_WHITE = "WhiteListed";
    protected final static String CH_INTERFACE = "Interface";
    protected final static String CH_CLASS = "Class";
    protected final static String CH_DESC = "Description";
    protected final static String CH_MODIFY_TYPE = "Modifier Type";
    protected final static String CH_METHOD = "Method";
    protected final static String CH_FIELD = "Field";
    
    protected final static String FV_YES = "YES";
    protected final static String FV_NO = "NO";
    protected final static String FV_ENTIRE = "entirely whitelisted";
    
    public CSVLoader(String fname) {
        fileName = fname;
        if( !loadCSVFile() ) {
            fieldsCount = 0;
        }
    }
    
    public boolean loadCSVFile() {        
        File file = new File(fileName);
        ScanUnsafe4Macro scanner = new ScanUnsafe4Macro();
        try( BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = "";
            while( (line=br.readLine()) != null ) {
                String[] fields = line.split(",");
                if( 0 == fields.length ) continue;
                if( fields[0].equals("WhiteListed") || fields[1].equals("white listed") ) {
                    if( 0 == fieldsCount ) {
                        headerFields1 = fields;
                        fieldsCount ++;  
                    }else if( 1 == fieldsCount ) {
                        headerFields2 = fields;
                        fieldsCount ++;
                    }else {
                        continue;
                    }
                }else {
                    if( 0 == fields[0].length() || 0 == fields[1].length())
                        continue;
                    if( headerFields2 != null ) {
                        fields2.add(fields);
                    }else if( headerFields1 != null ) {
                        fields1.add(fields);
                    }
                }
            }
            return true;
        }catch(Exception ex){
            return false;
        }
    }
}
