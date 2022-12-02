package com.example.tykoon;

public class GameInstance {
    private static GameInstance GI = null;
    private GameInstance() { }
    public static GameInstance getInstance()
    {
        if(GI == null)
        {
            GI = new GameInstance();
        }
        return GI;
    }

    private String playerName; // 플레이어 이름
    private float rating; // 별점(목숨)
    private int score; // 점수
    private int stage; // 몇 스테이지인지
}
