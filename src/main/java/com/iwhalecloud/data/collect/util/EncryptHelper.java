package com.iwhalecloud.data.collect.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class EncryptHelper {
    private static final Logger log = LoggerFactory.getLogger(EncryptHelper.class);

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

    // 加密算法
    public static String EncryptCodeString(String code) {
        StringBuffer sb = new StringBuffer();
        //（将每个字符按照 ASCII 码增加当前日期为当月第几天然后减掉当前日期为星期的第几天后得到新字符串）
        // 当前日期从 1 开始计算，当前星期第几天从周天开始，周天为 0
        Calendar today = Calendar.getInstance();
        int w = today.get(Calendar.DAY_OF_WEEK) - 1;
        int d = today.get(Calendar.DAY_OF_MONTH);
        int num = (int) (d - w);
        if (num <= 10) {
            num += 7;
        }
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            char temp = (char) ((int) (c) + num);
            sb.append(temp);
        }
        String result = sb.toString();
        try {
            result = URLEncoder.encode(result, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    // 解密算法：
    public static String UnEncryptCodeString(String code) {
        try {
            code = URLDecoder.decode(code, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        StringBuffer sb = new StringBuffer();
        //（将每个字符按照 ASCII 码增加当前日期为当月第几天然后减掉当前日期为星期的第几天后得到新字符串）
        // 当前日期从 1 开始计算，当前星期第几天从周天开始，周天为 0
        Calendar today = Calendar.getInstance();
        int w = today.get(Calendar.DAY_OF_WEEK) - 1;
        int d = today.get(Calendar.DAY_OF_MONTH);
        int num = (int) (d - w);
        if (num <= 10) {
            num += 7;
        }
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            char temp = (char) ((int) (c) - num);
            sb.append(temp);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String s = EncryptHelper.EncryptCodeString("12343333");
        String s1 = EncryptHelper.UnEncryptCodeString(s);
        log.info(s);
        log.info(s1);


    }
}
