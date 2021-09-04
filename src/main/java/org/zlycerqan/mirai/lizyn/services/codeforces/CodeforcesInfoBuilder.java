package org.zlycerqan.mirai.lizyn.services.codeforces;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class CodeforcesInfoBuilder {
    /**
     * Get the information of current or upcoming contests
     * Note: This function may take a long time
     * @return ContestInfo array
     */
    public static ArrayList<ContestInfo> getCurrentOrUpcomingContestsInfo() throws IOException {
        final String url = "https://codeforces.com/contests?complete=true";
        Document document;
        document = Jsoup.connect(url).get();
        Elements elements = document.select("div[class=datatable]");
        if (!elements.isEmpty()) {
            elements = elements.get(0).select("tr[data-contestid]");
        }
        ArrayList<ContestInfo> contestInfos = new ArrayList<>();
        for (Element element : elements) {
            Elements contest = element.select("td");
            int id = Integer.parseInt(element.attr("data-contestid").trim());
            String name = contest.get(0).text().trim();
            Elements writerElements = contest.get(1).select("a");
            ArrayList<String> writers = new ArrayList<>();
            ArrayList<String> colors = new ArrayList<>();
            for (Element writerElement : writerElements) {
                writers.add(writerElement.text());
                colors.add(CodeforcesUtils.getColorFromString(writerElement.attr("class").trim()));
            }
            Date start = CodeforcesUtils.stringToDate(contest.get(2).text().trim());
            if (start == null) {
                return null;
            } else {
                CodeforcesUtils.utc3ToUtc8(start);
            }
            int length = CodeforcesUtils.stringLengthToSeconds(contest.get(3).text().trim());
            String registerText = contest.get(5).text().trim();
            Date register = new Date();
            int registerLastTime = 0;
            int status;
            if (registerText.contains("Register")) {
                status = 1;
                String res = contest.get(5).select("span[title]").attr("title");
                if (res.length() == 0) {
                    res = contest.get(5).select("span").text().trim();
                }
                registerLastTime = CodeforcesUtils.stringLengthToInt(res);
            } else if (registerText.contains("closed")) {
                status = 2;
            } else {
                status = 0;
                String res = contest.get(5).select("span[title]").attr("title");
                if (res.length() == 0) {
                    res = contest.get(5).select("span").text().trim();
                }
                registerLastTime = CodeforcesUtils.stringLengthToInt(res);
            }
            if (status == 2) {
                register.setTime(0);
            } else {
                register.setTime(register.getTime() + registerLastTime * 1000L);
            }
            CodeforcesUtils.dropSecond(register);
            contestInfos.add(new ContestInfo(id, name, writers.toArray(new String[0]), colors.toArray(new String[0]), length, start, register, status));
        }
        return contestInfos;
    }
}
