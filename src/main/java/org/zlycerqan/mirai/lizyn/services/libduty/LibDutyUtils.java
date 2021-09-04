package org.zlycerqan.mirai.lizyn.services.libduty;

import java.util.Calendar;
import java.util.Date;

public class LibDutyUtils {
    public static Date transDate(String time) {
        String[] dat = time.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dat[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(dat[1]));
        return calendar.getTime();
    }

    public static int getCurrentDayOfWeek() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        return  day == 0 ? 7 : day;
    }

    public static void nextDay(Date date) {
        date.setTime(date.getTime() + 24L * 60L * 60L * 1000L);
    }
}
