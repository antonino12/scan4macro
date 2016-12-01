/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scan4macro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author World2016
 */
public class ScanUnsafe4Macro {
    
    protected final static String[] templTypes= {
      "Iterator",
      "Vector"
    };
    
    protected int unsafeLines = 0;
    protected Vector<Integer> unsafeCommands = new Vector<Integer>();
    
    public ScanUnsafe4Macro(){
        unsafeLines = 0;
    }
    
    public void parseLine(String line,int lineNo){
        boolean contain = false; // true if contain unsafe
        if( line.length() > 0 && (line.charAt(0) == ' ' || line.charAt(0) == '\t' || line.charAt(0) == '\n') )
            line = line.replaceFirst("\\s", "");
        if( line.startsWith("//") )
            return;
        line = line.replaceAll("//.*", "");
        String[] words = line.split("[!;?:\\s]+");
        for( int i=0; i<words.length; i++ ) {
            if( WhitePackages.getInstance() != null ) {
                String packName = "";
                if( words[i].endsWith(".*") ) 
                    packName = words[i].substring(0,words[i].length()-2);
                else 
                    packName = words[i];
//                else if( words[i].contains(".") ){
//                    String[] p = words[i].split("\\.");
//                    for( int j=0; j<p.length-1; j++ ) {
//                        if( j>0 ) packName += ".";
//                        packName += p[j] ;
//                    }
//                }
                    
                if( !packName.equals("") && WhitePackages.getInstance().isSafePackage(packName) == WhitePackages.WHITE_LISTED_NO ){
                    contain = true;
                }
            }else if( WhiteClasses.classNames.containsKey(words[i]) ) {
                if( WhiteClasses.classNames.get(words[i]) == WhiteClasses.WHITE_LISTED_NO ) {
                    contain = true;
                }
            }
            if( contain ) break;
        }
        if( !contain ) return ;
        unsafeLines ++;
        unsafeCommands.add(lineNo);
    }
    
    public int getUnsafeLines() {
        return unsafeLines;
    }
    
    public int getUnsafeCommand(int nth) {
        if( nth<0 || nth>=unsafeLines ) 
            return -1;
        return unsafeCommands.get(nth);
    }
    
//    public static void main(String[] args) {
//        try {
//            JavaTokenManager jtm = new JavaTokenManager(new FileReader(new File(args[1])));
//            Object token = null;
//            while( (token=jtm.getNextToken()) != null ) {
//                System.out.println(token.toString());
//                
//            }
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(ScanUnsafe4Macro.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//    }
//
//    /**
//     * @param args the command line arguments
//     */
    public static void main(String[] args) {
        File csvFiles = new File(args[0]);
        if( csvFiles.isDirectory() ) {
            File[] csvs = csvFiles.listFiles();
            for( int i=0; i<csvs.length; i++ ) {
                if( csvs[i].getName().endsWith(".csv") && csvs[i].getName().contains(" ") ) {
                    new WhitePackages(csvs[i].getPath());
                }else {
                    new WhiteClasses(csvs[i].getPath(),csvs[i].getName());
                }
            }
        }
        File file = new File(args[1]);
        ScanUnsafe4Macro scanner = new ScanUnsafe4Macro();
        try( BufferedReader br = new BufferedReader(new FileReader(file))){
            String line = "";
            int lineNo = 0;
            while( (line=br.readLine()) != null ) {
                scanner.parseLine(line,lineNo++);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.getLocalizedMessage());
        }
        
        if( scanner.getUnsafeLines() == 0 ) {
            System.out.println("macro is safe");   
            //System.out.println(String.format("%d", scanner.getUnsafeLines()));           
        }else {
            System.out.println("macro contains unsafe commands");   
            System.out.println(String.format("number of unsafed command lines: %d", scanner.getUnsafeLines())); 
            for( int i=0; i<scanner.getUnsafeLines(); i++ ) {
                System.out.println(String.format(" \tunsafe command(%d) line: %d",(i+1),scanner.getUnsafeCommand(i)));
            }
        }
    }
    
}
