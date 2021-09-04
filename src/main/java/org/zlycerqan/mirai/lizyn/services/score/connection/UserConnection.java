package org.zlycerqan.mirai.lizyn.services.score.connection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.zlycerqan.mirai.lizyn.services.score.connection.exception.CookiesErrorException;
import org.zlycerqan.mirai.lizyn.services.score.connection.exception.PasswordWrongException;
import org.zlycerqan.mirai.lizyn.services.score.connection.exception.ServerErrorException;
import org.zlycerqan.mirai.lizyn.services.score.connection.model.scoremodel.ScoreModel;
import org.zlycerqan.mirai.lizyn.services.score.connection.utils.Base64;
import org.zlycerqan.mirai.lizyn.services.score.connection.utils.PermutationGenerator;
import org.zlycerqan.mirai.lizyn.services.score.connection.utils.RSAEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class UserConnection {

    private final String id;

    private final String password;

    private Map<String, String> cookies;

    private String host;

    public UserConnection(String id, String password) throws PasswordWrongException, ServerErrorException {
        this.id = id;
        this.password = password;
        buildConnection();
    }

    private void buildConnection() throws PasswordWrongException, ServerErrorException {
        int[] permutation = PermutationGenerator.generatorRandomPermutation(JWGLXTInfo.BASE_URLS.length);
        for (int i : permutation) {
            String host = JWGLXTInfo.BASE_URLS[i];
            try {
                this.cookies = fetchCookies(host, id, password);
                this.host = host;
                return;
            } catch (IOException ignore) {
            }

        }
        throw new ServerErrorException();
    }

    private ArrayList<ScoreModel> doFetchScoreModelList(String xnm,
                                                        String xqm,
                                                        String _search,
                                                        String nd,
                                                        String queryModel_showCount,
                                                        String queryModel_currentPage,
                                                        String queryModel_sortName,
                                                        String queryModel_sortOrder) throws CookiesErrorException, IOException {
        Connection connection = Jsoup.connect(host + "/jwglxt/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005&su=" + id);
        JWGLXTInfo.setHeader(connection);
        connection.cookies(cookies);

        connection.data("xnm", xnm);
        connection.data("xqm", xqm);
        connection.data("_search", _search);
        connection.data("nd", nd);
        connection.data("queryModel.showCount", queryModel_showCount);
        connection.data("queryModel.currentPage", queryModel_currentPage);
        connection.data("queryModel.sortName", queryModel_sortName);
        connection.data("queryModel.sortOrder", queryModel_sortOrder);

        Connection.Response response = connection.ignoreContentType(true).timeout(JWGLXTInfo.TIME_OUT * 2).method(Connection.Method.POST).execute();
        Document document = Jsoup.parse(response.body());
        String text = Objects.requireNonNull(document.getElementsByTag("body")).text();
        if (!text.contains(JWGLXTInfo.SCORE_GET_SUCCESSFUL_STRING)) {
            throw new CookiesErrorException();
        }

        JSONObject obj = JSONObject.parseObject(text);
        ArrayList<?> a = (ArrayList<?>) obj.getObject("items", ArrayList.class);
        ArrayList<ScoreModel> lis = new ArrayList<>();
        for (Object x : a) {
            JSONObject y = (JSONObject) x;
            lis.add(y.toJavaObject(ScoreModel.class));
        }
        return lis;
    }

    public ArrayList<ScoreModel> fetchScoreModelList(String xnm,
                                                     String xqm,
                                                     String _search,
                                                     String nd,
                                                     String queryModel_showCount,
                                                     String queryModel_currentPage,
                                                     String queryModel_sortName,
                                                     String queryModel_sortOrder) throws PasswordWrongException, ServerErrorException, CookiesErrorException, IOException {
        ArrayList<ScoreModel> arr;
        try {
            arr = doFetchScoreModelList(xnm, xqm, _search, nd, queryModel_showCount, queryModel_currentPage, queryModel_sortName, queryModel_sortOrder);
        } catch (CookiesErrorException e) {
            buildConnection();
            arr = doFetchScoreModelList(xnm, xqm, _search, nd, queryModel_showCount, queryModel_currentPage, queryModel_sortName, queryModel_sortOrder);
        }
        return arr;
    }


    private Map<String, String> fetchCookies(String host, String id, String password) throws IOException, PasswordWrongException {
        String ul = "/jwglxt/xtgl/login_slogin.html";
        Map<String, String> result;
        Connection connection;

        // fetch csrftoken and cookies
        connection = Jsoup.connect(host + ul);
        JWGLXTInfo.setHeader(connection);
        Connection.Response response;
        response = connection.timeout(JWGLXTInfo.TIME_OUT).execute();
        String csrfToken = Objects.requireNonNull(Jsoup.parse(response.body()).getElementById("csrftoken")).val();
        Map<String, String> cookies = response.cookies();

        // fetch publickey
        connection = Jsoup.connect(host + "/jwglxt/xtgl/login_getPublicKey.html");
        JWGLXTInfo.setHeader(connection);
        response = connection.cookies(cookies).ignoreContentType(true).timeout(JWGLXTInfo.TIME_OUT).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        String modulus = jsonObject.getString("modulus");
        String exponent = jsonObject.getString("exponent");
        password = Base64.hexToBase64(RSAEncoder.RSAEncrypt(password, Base64.base64ToHex(modulus), Base64.base64ToHex(exponent)));

        // login
        Date date = new Date();
        connection = Jsoup.connect(host + "/jwglxt/xtgl/login_slogin.html?_t=" + date.getTime());
        JWGLXTInfo.setHeader(connection);
        connection.header("Referer", host + "/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t=" + date.getTime());
        connection.header("Proxy-Connection", "keep-alive");
        connection.header("Pragma", "no-cache");

        connection.data("csrftoken", csrfToken);
        connection.data("yhm", id);
        connection.data("mm", password);
        connection.data("mm", password);

        response = connection.cookies(cookies).ignoreContentType(true).timeout(JWGLXTInfo.TIME_OUT).method(Connection.Method.POST).execute();

        result = response.cookies();
        if (response.body().contains(JWGLXTInfo.PASSWORD_WRONG_STRING)) {
            throw new PasswordWrongException();
        }

        return result;
    }
}
