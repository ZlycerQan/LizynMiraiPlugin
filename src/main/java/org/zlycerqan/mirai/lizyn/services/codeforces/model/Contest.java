package org.zlycerqan.mirai.lizyn.services.codeforces.model;

import lombok.Data;

@Data
public class Contest {

    private Integer id;

    private String name;

    private ContestType type;

    private ContestPhase phase;

    private Boolean frozen;

    private Integer durationSeconds;

    private Integer startTimeSeconds;

    private Integer relativeTimeSeconds;

    private String preparedBy;

    private String websiteUrl;

    private String description;

    private String difficulty;

    private String kind;

    private String icpcRegion;

    private String country;

    private String city;

    private String season;

}
