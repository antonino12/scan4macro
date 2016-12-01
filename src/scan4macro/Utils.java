/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scan4macro;

/**
 *
 * @author World2016
 */
public class Utils {
    public static String getSubPackage(String fullPath) {
        String[] tags = fullPath.split("\\.");
        String subPack = "";
        for( int j=0; j<tags.length-1; j++ )  {
            if( j>0 ) subPack += ".";
            subPack += tags[j];
        }
        return subPack;
    }
}
