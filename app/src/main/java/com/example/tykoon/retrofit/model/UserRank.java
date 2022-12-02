package com.example.tykoon.retrofit.model;

public class UserRank {
    private Long rank;
    private String name;
    private Double score;

    public UserRank(Long rank, String name, Double score) {
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

    public Double getScore() {
        return score;
    }
}
