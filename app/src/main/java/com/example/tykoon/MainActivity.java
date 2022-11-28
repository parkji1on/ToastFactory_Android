package com.example.tykoon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // 시간 상수 설정
    final static int SEC = 1000;
    final static int MIN = 60 * SEC;
    final static int HOUR = 60 * MIN;

    // 태그
    final static int TABLE_1 = 0;
    final static int TABLE_2 = 1;
    final static int TABLE_3 = 2;
    final static int TABLE_4 = 3;
    final static int TABLE_5 = 4;
    final static int TABLE_6 = 5;
    final static int TRASH = 6;
    static String toast_name, toast_quality; // 커스텀 다이얼로그와 값 주고 받음

    final String menu[] = {"치즈토스트","햄토스트","햄치즈토스트","불고기토스트","콘치즈토스트","치킨토스트","소세지토스트","베이컨토스트"};

    CountDownTimer countDownTimer0, countDownTimer1, countDownTimer2;
    TextView timer[] = new TextView[6]; // Guest 시간
    ImageButton guest[] = new ImageButton[6]; // Guest 이미지버튼
    ImageButton table[] = new ImageButton[6]; // Table 이미지버튼
    ImageButton item_btn;

    TextView tv_world_time; // 게임 세계 시간 텍스트뷰
    TextView order_list[] = new TextView[6]; // 테이블 별 주문 리스트뷰
    ListView listView; // 완성된 음식 들어가는 리스트뷰

    // Guest 관련
    int guest_id[] = {R.id.guest1,R.id.guest2,R.id.guest3,R.id.guest4,R.id.guest5,R.id.guest6};
    int guest_image[] = {R.drawable.guest1, R.drawable.guest2, R.drawable.guest3, R.drawable.guest4,
            R.drawable.guest5,R.drawable.guest6,R.drawable.guest7,R.drawable.guest8,R.drawable.guest9,
            R.drawable.guest10};

    int table_id[] = {R.id.Table1, R.id.Table2, R.id.Table3, R.id.Table4, R.id.Table5, R.id.Table6};
    int timer_id[] = {R.id.timer1, R.id.timer2, R.id.timer3, R.id.timer4, R.id.timer5, R.id.timer6};
    int order_id[] = {R.id.order1, R.id.order2, R.id.order3, R.id.order4, R.id.order5, R.id.order6};

    // 각 테이블별로 시간이 0초 아래가 됐는지 -> 필요할까? 일단 나둠
    boolean is_time_over[] = {false, false, false, false, false, false};

    long guest_time = 0; // 손님 별 시간
    long temp_time = 0; // 시간 계산 위해
    long world_time = 9 * HOUR; // 게임 세계 시간, 09:00 부터 시작

    ArrayList<Food> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // xml, JAVA 연결해주는 부분
        listView = findViewById(R.id.listView);
        tv_world_time = findViewById(R.id.world_time);
        item_btn = findViewById(R.id.itembtn);

        items = new ArrayList<>();
        final GameAdapter adapter = new GameAdapter(this, items);
        listView.setAdapter(adapter);

        for(int i = 0; i < guest_id.length; i++){
            final int index;
            index = i;
            timer[index] = findViewById(timer_id[index]);
            guest[index] = findViewById(guest_id[index]);
            table[index] = findViewById(table_id[index]);
            order_list[index] = findViewById(order_id[index]);
        }

        // 처음 오픈하면 모든 손님들이 들어옴
        for(int i = 0; i < 6; i++)
        {
            final int index;
            index = i;

            comeGuest(index);
        }

        // 첫번째 매개변수에 있는 시간 만큼 스테이지가 실행되고 끝나면 onFinish, 두번째 매개변수 시간마다 onTick 함수 실행됨
        countDownTimer0 = new CountDownTimer(60 * MIN, SEC) {
            @Override
            public void onTick(long l) {

                int hour = (int) (world_time / HOUR);
                int minute = (int) ((world_time % HOUR) / MIN);

                String cur_Time = "";

                if(hour<10) cur_Time += "0";
                cur_Time += hour + ":";

                if(minute < 10) cur_Time += "0";
                cur_Time += minute;

                tv_world_time.setText(cur_Time);

                world_time += (10 * MIN);
            }

            @Override
            public void onFinish() {
                // 여기 마감 시간이 되면 다음 스테이지로 넘어가는 코드 추가
                onFinish();
            }
        }.start();

        // 완성된 음식 목록에 있는 음식 선택하면 대화상자가 뜨고 대화상자에서 음식을 몇 번 테이블에 서빙할지, 버릴 지 정함
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomDialog customDialog = new CustomDialog(MainActivity.this);

                toast_name = items.get(i).name;
                toast_quality = Float.toString(items.get(i).quality);

                customDialog.setDialogListener(new CustomDialog.CustomDialogInterface() {
                    @Override
                    public void BtnClicked(int Tag) {
                        switch (Tag)
                        {
                            case TABLE_1:
                            case TABLE_2:
                            case TABLE_3:
                            case TABLE_4:
                            case TABLE_5:
                            case TABLE_6:
                                // 서빙 성공 시 손님 나가고 다음 손님 들어올 준비 -> 손님 나가고 점수 계산하는 코드 필요
                                if(CheckOrder(i,Tag))
                                {
                                    Toast.makeText(getApplicationContext(),"서빙 성공",Toast.LENGTH_SHORT).show();
                                    items.remove(i);
                                    adapter.notifyDataSetChanged();

                                    order_list[Tag].setText(""); // 주문 목록 제거
                                    guest[Tag].setVisibility(View.INVISIBLE); // 게스트 안보이게
                                    comeGuest(Tag); // 다음 게스트 들어옴

                                }
                                // 서빙 실패 시 토스트만 사라짐
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"서빙 실패",Toast.LENGTH_SHORT).show();
                                    items.remove(i);
                                    adapter.notifyDataSetChanged();
                                }
                                break;
                            case TRASH:
                                items.remove(i);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(),"버리기",Toast.LENGTH_SHORT);
                                break;

                        }
                    }

                });
                customDialog.show();

            }
        });

        // 임시로 완성된 음식 목록에 올리려고 한 코드 -> 나중에 주방이랑 연계
        item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();

                String str = menu[rand.nextInt(menu.length)];

                Food food = new Food(str, 50.0f);
                items.add(food);

                adapter.notifyDataSetChanged();
            }
        });
    }

    // 테이블이 비고 손님이 들어오기 까지의 시간이 지나면 손님이 들어오게 되고 주문을 한다.
    private void comeGuest(int index)
    {
        if(guest[index].getVisibility() == View.VISIBLE)
        {
            return;
        }

        // 5초가 지나면 손님 들어옴 Tick은 필요없음
        countDownTimer1 = new CountDownTimer(5 * SEC, 5 * SEC) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                Human human = new Human();
                int patience = human.patience;
                int type = human.type;

                guest[index].setImageResource(guest_image[type]);
                // 손님이 들어오고 나감을 Visibility로 설정
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
                guest_time = Long.parseLong(timer[index].getText().toString()) * SEC;

                // 손님의 인내심 정보로 정한 시간을 바탕으로 타이머가 작동하게 됨
                countDownTimer2 = new CountDownTimer(guest_time, SEC) {
                    @Override
                    public void onTick(long l) {
                        if(guest[index].getVisibility() == View.INVISIBLE)
                        {
                            this.cancel();
                        }
                        temp_time = l;
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

    // 손님이 주문한 음식과 서빙할 음식이 맞는지 틀린지
    private boolean CheckOrder(int index, int table)
    {
        if(items.get(index).name == order_list[table].getText().toString())
            return true;
        else
            return false;
    }

    // 정해진 시간이 1초마다 감소되고 텍스트로 나타냄
    private void updateTimer(int index)
    {
        int seconds = (int) temp_time / SEC;
        String Left_time = "";

        if(seconds < 10) Left_time += "0";
        Left_time += seconds;

        timer[index].setText(Left_time);
    }

    // 손님의 주문
    private void Order(int index)
    {
        Random random = new Random();
        int select = random.nextInt(menu.length);

        order_list[index].setText(menu[select]);
    }

    // 리스트뷰와 연결할 어댑터 설정
    public class GameAdapter extends BaseAdapter {

        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<Food> items;

        public GameAdapter(Context context, ArrayList<Food> items)
        {
            mContext = context;
            this.items = items;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = mLayoutInflater.inflate(R.layout.listview_item, null);

            Button btn = (Button) view.findViewById(R.id.serve_list);
            btn.setText(items.get(position).name);

            return view;
        }

        @Override
        public Food getItem(int i) {
            return items.get(i);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }

}

// 손님의 정보를 담는 Human 클래스
class Human
{
    int patience; // 사람 인내심 -> 상/중/하로 구분
    int type; // 사람 이미지
    float satisfaction; // 만족도

    // 랜덤으로 사람의 인내심과 이미지 타입을 정함
    Human()
    {
        Random random = new Random();
        patience = random.nextInt(3);
        type = random.nextInt(10);
    }
}

// 만들어진 토스트의 정보
class Food
{
    String name; // 무슨 토스트인지
    float quality; // 토스트의 음식 완성도

    Food(String name, float quality)
    {
        this.name = name;
        this.quality = quality;
    }
}