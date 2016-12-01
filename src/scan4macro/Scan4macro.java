/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scan4macro;

import java.io.File;

/**
 *
 * @author World2016
 */
public class Scan4macro {

    

    public static void main(String[] args) {
        File csvFiles = new File(args[0]);
        if( csvFiles.isDirectory() ) {
            File[] csvs = csvFiles.listFiles();
            for( int i=0; i<csvs.length; i++ ) {
                if( !csvs[i].getName().endsWith(".csv") )
                    continue;
                if( csvs[i].getName().contains(" ") ) {
                    //System.out.println(csvs[i].getPath());
                    new WhitePackages(csvs[i].getPath());
                }else {
                    new WhiteClasses(csvs[i].getPath(),csvs[i].getName().replace("csv", ""));
                }
            }
        }
        
        File file = new File(args[1]);
        TypesExtractor te = new TypesExtractor(file);
        te.extractAll();
        
        if( te.unsafeList.size() == 0 ) {
            System.out.println("macro is safe");   
            //System.out.println(String.format("%d", scanner.getUnsafeLines()));           
        }else {
            System.out.println("macro contains unsafe commands");   
            System.out.println(String.format("number of unsafed command lines: %d", te.unsafeList.size())); 
            for( int i=0; i<te.unsafeList.size(); i++ ) {
                TypesExtractor.Pair p = te.unsafeList.get(i);
                System.out.println(" \tUnsafed commands: LINE-"+p.nodeNumber+": "+p.nodeName);
            }
        }
        
    }
    
}
