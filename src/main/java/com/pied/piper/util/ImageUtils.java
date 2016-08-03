package com.pied.piper.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by akshay.kesarwan on 03/08/16.
 */
public class ImageUtils {
    public static String decompressSignature(String compressedSignature){
        Map<Character, String> dict = new HashMap<>();
        char currChar = compressedSignature.charAt(0);
        String oldPhrase = String.valueOf(currChar);
        List<String> out = new ArrayList<>();
        out.add(String.valueOf(currChar));
        int code = 256;
        String phrase = new String();
        for(int i=1; i<compressedSignature.length();i++){
            Character currCode = compressedSignature.charAt(i);
            if(currCode<256){
                phrase = String.valueOf(compressedSignature.charAt(i));
            }else{
                phrase = dict.containsKey(currCode)? dict.get(currCode) : oldPhrase+String.valueOf(currChar);
            }
            out.add(phrase);
            currChar = phrase.charAt(0);
            dict.put((char)code, oldPhrase+String.valueOf(currChar));
            code ++;
            oldPhrase = phrase;
        }
        StringBuilder str= new StringBuilder();
        out.forEach( o -> str.append(o));
        return str.toString();
    }
}
