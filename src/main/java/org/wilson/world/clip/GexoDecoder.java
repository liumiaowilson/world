package org.wilson.world.clip;

import java.util.HashMap;
import java.util.Map;

public class GexoDecoder {
    private static Map<Character, Character> map = new HashMap<Character, Character>();
    
    static {
        map.put('a', 'h');
        map.put('d', 'e');
        map.put('e', 'v');
        map.put('f', 'o');
        map.put('g', 'f');
        map.put('i', 'd');
        map.put('l', 'n');
        map.put('m', 'a');
        map.put('n', 'm');
        map.put('p', 'u');
        map.put('q', 't');
        map.put('r', 's');
        map.put('v', 'p');
        map.put('x', 'r');
        map.put('y', 'l');
        map.put('z', 'i');
        map.put('$', ':');
        map.put('&', '.');
        map.put('(', '=');
        map.put('^', '&');
        map.put('=', '/');
    }
    
    public static String decode(String input) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            Character new_c = map.get(c);
            if(new_c == null) {
                new_c = c;
            }
            sb.append(new_c);
        }
        return sb.toString();
    }
}
