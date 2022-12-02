package com.example.tykoon;


public class RankVO {
    private Long rank;
    private String name;
    private Long score;

    public RankVO(Long rank, String name, Long score) {
        this.rank = rank;
        this.name = name;
        this.score = score;
    }

    public Long getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public Long getScore() {
        return score;
    }
}
