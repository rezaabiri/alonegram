package ir.cenlearn.alonegram.Others;

import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Encoder {

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static String MD5(String password){
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(password.getBytes(), 0, password.length());
            password = new BigInteger(1, mdEnc.digest()).toString(16);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    public static String escapeJava(String value){
        return StringEscapeUtils.escapeJava(value);
    }

    public static String unescapeJava(String value){
        return StringEscapeUtils.unescapeJava(value);
    }
}
