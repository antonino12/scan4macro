/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scan4macro;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 *
 * @author World2016
 */
public class WhitePackages extends CSVLoader {
    
    // **** CSV TYPE ****
    // Packages white listed    Comment
    // Csv file must be formed as above type.
    
    protected final static Integer WHITE_LISTED_NO = 0;
    protected final static Integer WHITE_LISTED_YES = 1;
    protected final static Integer WHITE_LISTED_ENTIRELY = 2;
    
    protected Map<String,Integer> packages = new HashMap<String,Integer>();
    public static WhitePackages _instance = null;
    
    public WhitePackages(String fileName) {
        super(fileName);
        init();
        _instance = this;
    }
    
    public static WhitePackages getInstance() {
        return _instance;
    }
    
    public static Set<String> getPackages() {
        if( _instance == null ) return null;
        return _instance.packages.keySet();
    }
    
    public void init() {
        if( fieldsCount == 1 && headerFields1[0].equals(PH_PACKAGE) && // here fields count must be 1 and all headers
            headerFields1[1].equals(PH_WHITE) && headerFields1[2].equals(PH_COMMENT) ) { // must be in above csv header type.
            for( int i=0; i<fields1.size(); i++ ) {
                if( fields1.get(i)[1].toUpperCase().contains(FV_YES) ) {
                    if( fields1.get(i)[2].toLowerCase().contains(FV_ENTIRE) )
                        packages. put(fields1.get(i)[0], WHITE_LISTED_ENTIRELY);
                    else
                        packages. put(fields1.get(i)[0], WHITE_LISTED_YES);
                }else if( fields1.get(i)[1].toUpperCase().contains(FV_NO) )
                    packages. put(fields1.get(i)[0], WHITE_LISTED_NO);
            }
        }
    }
    
    public Integer isSafePackage(String packName) {
        String[] p = packName.split("\\.");
        String tempPackName = "";
        for( int i=0; i<p.length-1; i++ ) {
            if( i>0 ) tempPackName += ".";
            tempPackName += p[i];
            String tpn = tempPackName + ".*";
            if( packages.containsKey(tpn) && packages.get(tpn) == WHITE_LISTED_ENTIRELY )
                return WHITE_LISTED_YES;
        }
        if( !packages.containsKey(packName) )
            return WHITE_LISTED_NO;
        return packages.get(packName);
    }
}
