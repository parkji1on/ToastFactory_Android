package com.example.tykoon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    CountDownTimer countDownTimer0, countDownTimer1, countDownTimer2;
    TextView timer[] = new TextView[6];
    ImageButton guest[] = new ImageButton[6];
    TextView tv_world_time; // 게임 세계 시간
    TextView order_list[] = new TextView[6];

    int guest_id[] = {R.id.guest1,R.id.guest2,R.id.guest3,R.id.guest4,R.id.guest5,R.id.guest6};
    int timer_id[] = {R.id.timer1, R.id.timer2, R.id.timer3, R.id.timer4, R.id.timer5, R.id.timer6};
    int order_id[] = {R.id.order1, R.id.order2, R.id.order3, R.id.order4, R.id.order5, R.id.order6};
    String menu[] = {"치즈토스트","햄토스트","햄치즈토스트","불고기토스트","콘치즈토스트","치킨토스트","소세지토스트","베이컨토스트"};

    boolean istimeover;

    long time = 0;
    long temptime = 0;
    long worldtime = 9 * 3600000; // 게임 세계 시간, 09:00 부터 시작

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_world_time = findViewById(R.id.world_time);

        for(int i = 0; i < guest_id.length; i++){
            final int index;
            index = i;
            timer[index] = findViewById(timer_id[index]);
            guest[index] = findViewById(guest_id[index]);
            order_list[index] = findViewById(order_id[index]);
        }

        // 처음 오픈하면 모든 손님들이 들어옴
        for(int i = 0; i < 6; i++)
        {
            final int index;
            index = i;
            comeGuest(index);
        }

        // 실제로 10초가 지날때마다 게임 세계 시간 10분 증가
        countDownTimer0 = new CountDownTimer(1 * 1000, 1000) {
            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish() {
                worldtime += 600000;

                // 여기 마감 시간이 되면 다음 스테이지로 넘어가는 코드 추가


                int hour = (int) worldtime / 3600000;
                int minute = (int) worldtime % 3600000 / 60000;

                String curTime = "";

                if(hour<10) curTime += "0";
                curTime += hour + ":";

                if(minute < 10) curTime += "0";
                curTime += minute;

                tv_world_time.setText(curTime);

                countDownTimer0.start();
            }
        }.start();

    }

    private void comeGuest(int index)
    {
        countDownTimer1 = new CountDownTimer(5 * 1000, 5 * 1000) {
            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish() {
                Human human = new Human();
                int patience = human.patience;
                guest[index].setVisibility(View.VISIBLE);
                Order(index);

                switch (patience)
                {
                    case 0:
                        timer[index].setText(Integer.toString(50));
                        break;
                    case 1:
                        timer[index].setText(Integer.toString(60));
                        break;
                    case 2:
                        timer[index].setText(Integer.toString(70));
                        break;
                }
                time = Long.parseLong(timer[index].getText().toString()) * 1000;

                // 손님의 인내심 정보로 정한 시간을 바탕으로 타이머가 작동하게 됨
                countDownTimer2 = new CountDownTimer(time, 1000) {
                    @Override
                    public void onTick(long l) {
                        temptime = l;
                        updateTimer(index);
                    }

                    @Override
                    public void onFinish() {
                        // 타이머가 종료되면 손님이 나가고 다음 손님이 들어오게 됨
                        guest[index].setVisibility(View.INVISIBLE);
                        order_list[index].setText("");
                        comeGuest(index);
                    }
                }.start();
            }
        }.start();
    }

    // 1초마다 타이머가 작동
    private void updateTimer(int index)
    {
        int seconds = (int) temptime / 1000;
        String Lefttime = "";
        if(seconds < 10) Lefttime += "0";
        Lefttime += seconds;

        timer[index].setText(Lefttime);
    }

    // 손님의 주문
    private void Order(int index)
    {
        Random random = new Random();
        int select = random.nextInt(menu.length);

        order_list[index].setText(menu[select]);
    }
}

// 손님의 정보를 담는 Human 클래스
class Human
{
    int patience; // 사람 인내심
    private int type; // 사람 이미지

    // 랜덤으로 사람의 인내심과 이미지 타입을 정함
    Human()
    {
        Random random = new Random();
        patience = random.nextInt(3);
        type = random.nextInt(6);
    }
}