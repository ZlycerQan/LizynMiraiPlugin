package org.zlycerqan.mirai.lizyn.services.codeforces;

import org.zlycerqan.mirai.lizyn.services.codeforces.model.Contest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CodeforcesUtils {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public static String formatSeconds(Integer seconds) {
        int minute = seconds / 60 % 60;
        int hour = seconds / 60 / 60;
        if (hour < 100) {
            return String.format("%02d:%02d", hour, minute);
        } else {
            return hour + ":" + minute;
        }
    }

    public static String formatContest(Contest contest) {
        Date date = new Date();
        StringBuilder builder = new StringBuilder();
        builder.append("name: ").append(contest.getName()).append("\n");
        builder.append("length: ").append(formatSeconds(contest.getDurationSeconds())).append("\n");
        builder.append("start: ").append(simpleDateFormat.format(new Date((long) contest.getStartTimeSeconds() * 1000L))).append("\n");
        builder.append("register: ").append(simpleDateFormat.format(new Date(date.getTime() - (long) contest.getRelativeTimeSeconds() * 1000L))).append("\n");
        return builder.toString();
    }

    public static String formatContestList(List<Contest> contests) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < contests.size(); ++ i) {
            builder.append(formatContest(contests.get(i)));
            if (i != contests.size() - 1) {
                builder.append("----------");
                builder.append("\n");
            }
        }
        return builder.toString().trim();
    }
}
