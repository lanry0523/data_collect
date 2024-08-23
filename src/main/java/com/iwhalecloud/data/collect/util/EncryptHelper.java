package com.iwhalecloud.data.collect.util;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EncryptHelper {
    /**
     * 签名算法
     */
    public static String SignUp(String secretKey, String plain) {
        byte[] keyBytes = secretKey.getBytes();
        byte[] plainBytes = plain.getBytes();
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(keyBytes, "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hashs = sha256_HMAC.doFinal(plainBytes);
            StringBuilder sb = new StringBuilder();
            for (byte x : hashs) {
                String b = Integer.toHexString(x & 0XFF);
                if (b.length() == 1) {
                    b = '0' + b;
                }
            // sb.append(String.format("{0:x2}", x));
                sb.append(b);
            }
            return sb.toString();
            // String hash =
            // Base64.encodeToString(sha256_HMAC.doFinal(plainBytes),
            // Base64.DEFAULT);
            // return hash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
