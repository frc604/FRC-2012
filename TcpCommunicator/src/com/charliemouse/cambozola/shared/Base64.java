package com.charliemouse.cambozola.shared;

/**
 ** com/charliemouse/cambozola/shared/CamStream.java </br> Copyright (C) Andy Wilcock, 2001. </br> Available from
 * http://www.charliemouse.com </br> </br>
 * 
 * This file is part of the Cambozola package (c) Andy Wilcock, 2001. </br> Available from http://www.charliemouse.com
 * </br> </br>
 * 
 * Cambozola is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later
 * version. </br> </br>
 * 
 * Cambozola is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * </br> </br>
 * 
 * You should have received a copy of the GNU General Public License along with Cambozola; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA </br>
 * 
 * </br> </br>
 *
 *
 * @see CamStream CamStream for more information.
 *
 *
 **/
public class Base64 {
    private static final char[] S_BASE64CHAR = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', '+', '/'
    };
    private static final char S_BASE64PAD = '=';

    /**
     * Returns base64 representation of specified byte array.
     * @param data The data to be encoded
     * @return The base64 encoded data
     */
    public static String encode(byte[] data) {
        return encode(data, 0, data.length);
    }

    /**
     * Returns base64 representation of specified byte array.
     * @param data The data to be encoded
     * @param off The offset within the data at which to start encoding
     * @param len The length of the data to encode
     * @return The base64 encoded data
     */
    public static String encode(byte[] data, int off, int len) {
        if (len <= 0)  return "";
        char[] out = new char[len/3*4+4];
        int rindex = off;
        int windex = 0;
        int rest = len;
        while (rest >= 3) {
            int i = ((data[rindex]&0xff)<<16)
                +((data[rindex+1]&0xff)<<8)
                +(data[rindex+2]&0xff);
            out[windex++] = S_BASE64CHAR[i>>18];
            out[windex++] = S_BASE64CHAR[(i>>12)&0x3f];
            out[windex++] = S_BASE64CHAR[(i>>6)&0x3f];
            out[windex++] = S_BASE64CHAR[i&0x3f];
            rindex += 3;
            rest -= 3;
        }
        if (rest == 1) {
            int i = data[rindex]&0xff;
            out[windex++] = S_BASE64CHAR[i>>2];
            out[windex++] = S_BASE64CHAR[(i<<4)&0x3f];
            out[windex++] = S_BASE64PAD;
            out[windex++] = S_BASE64PAD;
        } else if (rest == 2) {
            int i = ((data[rindex]&0xff)<<8)+(data[rindex+1]&0xff);
            out[windex++] = S_BASE64CHAR[i>>10];
            out[windex++] = S_BASE64CHAR[(i>>4)&0x3f];
            out[windex++] = S_BASE64CHAR[(i<<2)&0x3f];
            out[windex++] = S_BASE64PAD;
        }
        return new String(out, 0, windex);
    }
}