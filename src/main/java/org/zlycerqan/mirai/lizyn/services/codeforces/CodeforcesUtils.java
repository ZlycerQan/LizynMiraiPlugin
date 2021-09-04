package org.zlycerqan.mirai.lizyn.services.codeforces;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CodeforcesUtils {

    private static final String[] MONTHS = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private static final Map<String, Color> colorMap = new HashMap<>();

    static {
        colorMap.put("legendary", new Color(255, 0, 0));
        colorMap.put("red", new Color(255, 0, 0));
        colorMap.put("orange", new Color(255, 165, 0));
        colorMap.put("violet", new Color(238, 130, 238));
        colorMap.put("cyan", new Color(0, 255, 255));
        colorMap.put("gray", new Color(128, 128, 128));
        colorMap.put("blue", new Color(0, 0, 255));
        colorMap.put("admin", new Color(0, 0, 0));
        colorMap.put("green", new Color(0,128,0));
        colorMap.put("black", new Color(0,0,0));
    }

    /**
     *
     * Convert String representing the contest date to Date
     *
     * @author ZlycerQan
     * @param s String format "MONTH/dd/yyyy HH:mm" MONTH is the abbreviation of month like "August -> Aug"
     *
     * @return Date
     */
    public static Date stringToDate(String s) {
        StringBuilder stringBuilder = new StringBuilder(s);
        for (int i = 0; i < MONTHS.length; ++ i) {
            if (s.contains(MONTHS[i])) {
                stringBuilder.replace(0, MONTHS[i].length(), String.valueOf(i + 1));
                break;
            }
        }
        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try {
            date = simpleDateFormat.parse(stringBuilder.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }


    /**
     *
     * Convert the string representing of the contest length to long representing the second
     *
     * @author ZlycerQan
     * @param s String format "HH:mm" or "HH:mm:ss"
     *
     * @return int
     */
    public static int stringLengthToInt(String s) {
        StringBuilder stringBuilder = new StringBuilder(s);
        int result;
        int pos1 = stringBuilder.indexOf(":");
        result = Integer.parseInt(stringBuilder.substring(0, pos1)) * 60 * 60;
        int pos2 = stringBuilder.indexOf(":", pos1 + 1);
        if (pos2 == -1) {
            result += Integer.parseInt(stringBuilder.substring(pos1 + 1, s.length())) * 60;
        } else {
            result += Integer.parseInt(stringBuilder.substring(pos1 + 1, pos2)) * 60 + Integer.parseInt(stringBuilder.substring(pos2 + 1, s.length()));
        }
        return result;
    }

    public static int stringLengthToSeconds(String s) {
        StringBuilder stringBuilder = new StringBuilder(s);
        int result;
        int pos1 = stringBuilder.indexOf(":");
        int pos2 = stringBuilder.indexOf(":", pos1 + 1);
        if (pos2 == -1) {
            result = Integer.parseInt(stringBuilder.substring(0, pos1)) * 60 * 60;
            result += Integer.parseInt(stringBuilder.substring(pos1 + 1, s.length())) * 60;
        } else {
            result = Integer.parseInt(stringBuilder.substring(0, pos1)) * 24 * 60 * 60;
            result += Integer.parseInt(stringBuilder.substring(pos1 + 1, pos2)) * 60 * 60 + Integer.parseInt(stringBuilder.substring(pos2 + 1, s.length())) * 60;
        }
        return result;
    }

    /**
     *
     * Convert date from UTC+3 to UTC+8
     *
     * @param date UTC+3 date
     */
    public static void utc3ToUtc8(Date date) {
        date.setTime(date.getTime() + 5L * 60L * 60L * 1000L);
    }

    /**
     *
     * Convert the second of the date to zero
     * @param date result
     */
    public static void dropSecond(Date date) {
        date.setTime(date.getTime() - date.getTime() % (60L * 1000L));
    }

    /**
     *
     * Extract color from a string in a specific format
     *
     * @param s String waiting to be processed
     * @return String representing the color
     */
    public static String getColorFromString(String s) {
        String[] p = s.split(" ");
        if (p.length > 1) {
            p = p[1].split("-");
            if (p.length > 1) {
                return p[1];
            }
        }
        return null;
    }

    public static Color getColor(String s) {
        return colorMap.get(s);
    }

    public static void saveContestPicture(String filename, BufferedImage bufferedImage) throws IOException {
        ImageIO.write(bufferedImage, "jpg", new File(filename));
    }




}
