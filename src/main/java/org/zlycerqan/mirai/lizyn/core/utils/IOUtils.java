package org.zlycerqan.mirai.lizyn.core.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class IOUtils {

    public static String readFromFile(String filename) throws IOException {
        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStream.read(bytes);
        return new String(bytes);
    }

    public static void writeToFile(String filename, String text) throws IOException {
        File file = new File(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean checkFileIfExists(String filename) {
        File file = new File(filename);
        return file.exists();
    }

    public static boolean createFile(String filename) throws IOException {
        File file = new File(filename);
        return file.createNewFile();
    }


    public static boolean checkDirectionIfExists(String dir) {
        File d = new File(dir);
        return d.exists();
    }

    public static boolean createDirections(String dir) {
        File d = new File(dir);
        return d.mkdirs();
    }
}
