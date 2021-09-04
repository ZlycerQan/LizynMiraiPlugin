package org.zlycerqan.mirai.lizyn.services.score.connection;

import org.jsoup.Connection;

public class JWGLXTInfo {

    public final static String[] BASE_URLS = new String[]{
            "http://jwglxt1.qust.edu.cn",
            "http://jwglxt2.qust.edu.cn",
            "http://jwglxt3.qust.edu.cn",
            "http://jwglxt4.qust.edu.cn",
            "http://jwglxt5.qust.edu.cn",
            "http://jwglxt6.qust.edu.cn"
    };

    public static void setHeader(Connection connection) {
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36");
        connection.header("Cache-Control", "no-cache");
        connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        connection.header("Accept-Language", "zh-CN,zh;q=0.9");
        connection.header("Upgrade-Insecure-Requests", "1");
        connection.header("Connection", "keep-alive");
    }

    public static int TIME_OUT = 5000;

    public static String ACCEPT_LOGIN_STRING = "xsxkzt";

    public static String PASSWORD_WRONG_STRING = "Content-Security-Policy";

    public static String SCORE_GET_SUCCESSFUL_STRING = "currentPage";
}
