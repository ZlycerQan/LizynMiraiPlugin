package org.zlycerqan.mirai.lizyn.services.codeforces;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.zlycerqan.mirai.lizyn.services.codeforces.model.Contest;
import org.zlycerqan.mirai.lizyn.services.codeforces.model.ContestPhase;
import org.zlycerqan.mirai.lizyn.services.codeforces.model.QueryContestResult;

import java.io.IOException;
import java.util.*;

public class ContestUtils {

    private final Gson json = new Gson();

    private List<Contest> getContests(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://codeforces.com/api/contest.list")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                try {
                    return json.fromJson(Objects.requireNonNull(response.body()).string(), QueryContestResult.class).getResult();
                } catch (JsonSyntaxException e) {
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    public List<Contest> getCurrentOrUpcomingContests() {
        List<Contest> contests = getContests();
        if (contests == null) {
            return null;
        }
        ArrayList<Contest> result = new ArrayList<>();
        first: for (Contest contest : contests) {
            String name = contest.getName();
            for (int i = 0; i < name.length(); ++ i) {
                if (name.charAt(i) > 255) {
                    continue first;
                }
            }
            if (contest.getPhase() == ContestPhase.BEFORE || contest.getPhase() == ContestPhase.CODING)  {
                result.add(contest);
            }
        }
        result.sort(Comparator.comparing(Contest::getStartTimeSeconds));
        return result;
    }
}
