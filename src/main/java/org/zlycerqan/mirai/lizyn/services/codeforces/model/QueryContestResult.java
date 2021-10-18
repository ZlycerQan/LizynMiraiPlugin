package org.zlycerqan.mirai.lizyn.services.codeforces.model;

import lombok.Data;

import java.util.List;

@Data
public class QueryContestResult {

    private String status;

    private List<Contest> result;
}
