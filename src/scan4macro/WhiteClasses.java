/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scan4macro;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author World2016
 */
public class WhiteClasses extends CSVLoader{
    
    public final static Integer WHITE_LISTED_NO = 0;
    public final static Integer WHITE_LISTED_INTERFACE = 1;
    public final static Integer WHITE_LISTED_CLASS = 2;
    public final static Integer WHITE_LISTED_FIELD = 3;
    public final static Integer WHITE_LISTED_METHOD = 4;
    
    public static Map<String,Integer> methodNames = new HashMap<String,Integer>();
    public static Map<String,Integer> classNames = new HashMap<String,Integer>();
//    
//    public void printUnsafe(String clsName,int line){
//        System.out.println(String.format(" \tUnsafe command: LINE-%d: %s",line,clsName));
//    }
//    
    public static int isSafeClass(String className,TypesExtractor.Pair fullClassName,boolean langClass) {
        if( classNames.containsKey(className) )
            return classNames.get(className);
        if( langClass && WhitePackages.getPackages() != null ) {
            Iterator<String> it = WhitePackages.getPackages().iterator();
            while( it.hasNext() ) {
                String packPrefix = it.next();
                String fcn = packPrefix + "." + className;
                if( classNames.containsKey(fcn) ) {
                    fullClassName.nodeName = fcn;
                    return classNames.get(fcn);
                }
                //return WHITE_LISTED_CLASS;
            }
            return WHITE_LISTED_CLASS;
        }
        return WHITE_LISTED_NO;
    }
    
    public static int isSafeMethod(String methodName) {
        if( !methodName.contains(".") )
            return WHITE_LISTED_METHOD;
        if( methodNames.containsKey(methodName) ) {
            return methodNames.get(methodName);
        }
        return WHITE_LISTED_METHOD;
    }
    
    protected String packageName = "";
    protected String prefix = "";
    
    public WhiteClasses(String fname,String pname) {
        super(fname);
        packageName = pname;
        prefix = pname; //prefix = prefix;
        init();
        //System.out.println("fname::::: ============ " + pname);
    }
    
    public String getMethodName(String methodExpr) {
        return methodExpr.replaceAll("\\(.*", "");
    }
    
    public void init() {
        // first type of def: interface/class
        if( fieldsCount == 2 && headerFields1[0].equals(CH_WHITE) && // here fields count must be 1 and all headers
             (headerFields1[1].equals(CH_INTERFACE) && headerFields2[1].equals(CH_CLASS)) &&
             headerFields1[2].equals(CH_DESC) ) { // must be in above csv header type.
            for( int i=0; i<fields1.size(); i++ ) {
                if( fields1.get(i)[0].toUpperCase().contains(FV_YES) ) {
                    classNames. put(prefix + getMethodName(fields1.get(i)[1]), WHITE_LISTED_INTERFACE);
                }else if( fields1.get(i)[0].toUpperCase().contains(FV_NO) )
                    classNames. put(prefix + getMethodName(fields1.get(i)[1]), WHITE_LISTED_NO);
            }
            for( int i=0; i<fields2.size(); i++ ) {
                if( fields2.get(i)[0].toUpperCase().contains(FV_YES) ) {
                    classNames. put(prefix + getMethodName(fields2.get(i)[1]), WHITE_LISTED_CLASS);
                }else if( fields2.get(i)[0].toUpperCase().contains(FV_NO) )
                    classNames. put(prefix + getMethodName(fields2.get(i)[1]), WHITE_LISTED_NO);
                
                //System.out.println(prefix + getMethodName(fields2.get(i)[1]) +" <<<< " + fields2.get(i)[0].equalsIgnoreCase(FV_YES));
            }
        } else if( fieldsCount >= 1 && headerFields1[0].equals(CH_WHITE) && // here fields count must be 1 and all headers
             headerFields1[1].equals(CH_MODIFY_TYPE) ) { // must be in above csv header type.
            if( fieldsCount == 2 ) {
                for( int i=0; i<fields1.size(); i++ ) {
                    if( fields1.get(i)[0].toUpperCase().contains(FV_YES) ) {
                        methodNames. put(prefix + getMethodName(fields1.get(i)[2]), WHITE_LISTED_FIELD);
                    }else if( fields1.get(i)[0].toUpperCase().contains(FV_NO) )
                        methodNames. put(prefix + getMethodName(fields1.get(i)[2]), WHITE_LISTED_NO);
                }
                for( int i=0; i<fields2.size(); i++ ) {
                    if( fields2.get(i)[0].toUpperCase().contains(FV_YES) ) {
                        methodNames. put(prefix + getMethodName(fields2.get(i)[2]).replaceAll("\"", ""), WHITE_LISTED_METHOD);
                    }else if( fields2.get(i)[0].toUpperCase().contains(FV_NO) )
                        methodNames. put(prefix + getMethodName(fields2.get(i)[2]).replaceAll("\"", ""), WHITE_LISTED_NO);
                }
            }else if( fieldsCount == 1 ) {
                for( int i=0; i<fields1.size(); i++ ) {
                    if( fields1.get(i)[0].toUpperCase().contains(FV_YES) ) {
                        methodNames. put(prefix + getMethodName(fields1.get(i)[2]), WHITE_LISTED_METHOD);
                    }else if( fields1.get(i)[0].toUpperCase().contains(FV_NO) )
                        methodNames. put(prefix + getMethodName(fields1.get(i)[2]), WHITE_LISTED_NO);
                }                
            }
        }
    }
    
}
