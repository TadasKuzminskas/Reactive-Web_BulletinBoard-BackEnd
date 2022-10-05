package com.example.demo2.util;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class UtilMethods {

    public String hashingFunction(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(string.getBytes(StandardCharsets.UTF_8));

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public String imageHashString(String filename) {

        String[] arr = filename.split("[.]");
        System.out.println(arr.length);
        if (arr.length != 2) {
            throw new RuntimeException("Test ERROR: fileName " + filename + " arr.length: " + arr.length);
        }
        if (arr[1].equals("apng") || arr[1].equals("avif") || arr[1].equals("gif")
                || arr[1].equals("jpg") || arr[1].equals("jpeg") || arr[1].equals("jfif")
                || arr[1].equals("pjpeg") || arr[1].equals("pjp") || arr[1].equals("png")
                || arr[1].equals("svg") || arr[1].equals("webp")) {

            String str = hashingFunction(arr[0] + (100*Math.random()));

            return str + "." + arr[1];
        }
        return "Inoperable file name.";
    }


}
