package com.example.tykoon;

// 싱글톤 패턴으로 만든 게임 인스턴스 -> 게임 실행부터 게임 끝까지 살아있으며 단 하나만 생성
// 액티비티에서 사용하려면 GameInstance.getInstance().(함수) 해서 값을 설정하거나 얻어옴
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

    private String playerID = ""; // 플레이어 아이디
    private float rating = 3.0f; // 별점(목숨)
    private int score = 0; // 점수
    private short stage = 1; // 몇 스테이지인지
    private int visited_guest = 0; // 방문한 손님 수

    public void setPlayerID(String playerName) {
        this.playerID = playerName;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setStage(short stage) {
        this.stage = stage;
    }

    public short getStage() {
        return stage;
    }

    public void setVisited_guest(int visited_guest) { this.visited_guest = visited_guest; }

    public int getVisited_guest() { return visited_guest; }

    public void init()
    {
        this.rating = 3.0f;
        this.score = 0;
        this.stage = 1;
        this.visited_guest = 0;
    }
}
