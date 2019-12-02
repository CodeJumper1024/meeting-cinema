package com.stylefeng.guns.rest.utils;

import java.io.*;

public class FileUtils {

    public static String readFileToString(String path) {

        String jsonString = "";

        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
            String thisLine = null;
            while ((thisLine = in.readLine()) != null) {
                jsonString += thisLine;
            }
            in.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonString;
    }
}
