package com.skillforge.Utilities;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class securityUtils {
    public static String hashPassword(String passToHash)
    {
        try {
            // first , get the algo itself
            MessageDigest digest = MessageDigest.getInstance("SHA-256") ;
            // second , transform the raw pass into bytes so the complex maths be easier
            byte [] encodedHash = digest.digest(passToHash.getBytes(StandardCharsets.UTF_8));
            // third , transform your bytes into the hash as a Hex == String
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String bytesToHex(byte[] bytes)
    {
        StringBuilder hexString = new StringBuilder( 2* bytes.length) ;
        // a mutable version of the String , as below the appending is used
        // so , we can evade the case of huge garbage collector when creating new strings each time iterating
        for (byte aByte : bytes) {
            // as this converts an integer into a hexa ,  so we done that conversion
            // this conversion assures that we are unsigned value of bytes{+ve}
            // so , when converted we are 00 into FF number
            String hex = Integer.toHexString(0xff /* a 255 in hexa , a mask*/ & aByte);
            if (hex.length() == 1)
                /*the padding , as we turn A into 0A*/
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString() ;
    }
    /*
    public static void main(String[] args)
    {
        String pass = "ana Mo'men the fool" ;
        String pass1 = "an Mo'men the fool" ;
        System.out.println(hashPassword(pass).equals(hashPassword(pass1))) ;
    }
     */
}
