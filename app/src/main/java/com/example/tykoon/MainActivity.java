package com.example.tykoon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.tykoon.retrofit.Retrofit_interface;
import com.example.tykoon.retrofit.model.BaseResponse;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // 현재 뷰가 홀인지, 주방인지
    boolean IsInHall = true;

    // 시간 상수
    final static long SEC = 1000;
    final static long MIN = 60 * SEC;
    final static long HOUR = 60 * MIN;

    // 태그
    final static int TABLE_1 = 0;
    final static int TABLE_2 = 1;
    final static int TABLE_3 = 2;
    final static int TABLE_4 = 3;
    final static int TABLE_5 = 4;
    final static int TABLE_6 = 5;
    final static int TRASH = 6;

    // 손님의 인내심(상,중,하)에 따라 처음에 다르게 설정되는 시간
    int waiting_time_long, waiting_time_middle, waiting_time_short;

    static String toast_name, toast_quality; // 커스텀 다이얼로그와 값 주고 받음

    // 토스트 메뉴들
    final String toast_menu[] = {
            "햄치즈스페셜","베이컨베스트","햄치즈포테이토","더블소세지","새우","그릴드불고기","베이컨치즈베이글"};

    // 각 테이블별로 시간이 0초 아래가 됐는지
    boolean is_time_over[] = {false, false, false, false, false, false};

    CountDownTimer cdT_World;
    CountDownTimer cDT_Table[] = new CountDownTimer[6];
    CountDownTimer cDT_Guest[] = new CountDownTimer[6];

    TextView timer_h[] = new TextView[6]; // Guest 시간 -> 홀에서 보이게
    TextView timer_k[] = new TextView[6]; // Guest 시간 -> 주방에서 보이게
    TextView tv_Ingredient, tv_Score, tv_Day; // 재료, 점수, Day 텍스트 뷰
    TextView tv_world_time; // 게임 시간 나타내는 텍스트 뷰
    TextView order_list_h[] = new TextView[6]; // 테이블 별 주문 리스트뷰 -> 홀 텍스트
    TextView order_list_k[] = new TextView[6]; // 테이블 별 주문 리스트뷰 -> 주방 텍스트

    ImageView guest[] = new ImageView[6]; // Guest 이미지버튼 -> 버튼 기능 필요없을 시 뷰로 변경
    ImageView table[] = new ImageView[6]; // Table 이미지버튼 -> 버튼 기능 필요없을 시 뷰로 변경
    ImageButton ingreButtons[] = new ImageButton[16];
    ImageButton btnItem, btnPause, btnRecipe, btnStart, btnRestart;;
    Button btnChangeView;

    ListView listView; // 완성된 음식 들어가는 리스트뷰
    ViewFlipper vFlipper; // 주방, 홀 왔다갔다
    View dialogView;    // 아이템 버튼을 눌렀을때 나오는 대화상자

    RatingBar ratingBar;
    ProgressBar progFood;

    String ingredient = "\n\n\n";   //TextView에 보여줄 텍스트 생성
    String choiceMenu; //선택한 메뉴의 이름

    int[] ingredientList = new int[16]; //선택한 재료가 들어 있는지 여부
    //메뉴의 리스트 (menu) - (choicemenu와 ingredientList를 저장함) - 홀에서 필요한 정보
    //레시피의 확인 절차를 위한 recipeBook
    HashMap<String,int[]> recipeBook = new HashMap<String,int[]>(){{
        put("햄치즈스페셜", new int[]{1,0,1,1,1,1,0,0,0,0,0,0,1,0,0,0});
        put("베이컨베스트", new int[]{1,0,1,0,1,1,1,0,0,0,0,0,1,0,0,0});
        put("햄치즈포테이토", new int[]{1,0,1,0,1,0,0,1,0,0,0,0,0,0,1,1});
        put("더블소세지", new int[]{1,0,1,0,1,1,0,0,1,0,0,0,0,0,1,0});
        put("새우", new int[]{1,0,1,0,1,1,0,0,0,0,1,0,1,0,0,0});
        put("그릴드불고기", new int[]{1,0,1,0,1,1,0,0,0,0,0,1,1,0,0,0});
        put("베이컨치즈베이글", new int[]{0,1,1,0,1,0,1,0,0,0,0,0,0,0,1,1});
    }};

    // 리소스 ID 담아둔 배열
    // Guest 관련
    int guest_id[] = {R.id.guest1,R.id.guest2,R.id.guest3,R.id.guest4,R.id.guest5,R.id.guest6};
    int guest_image[] = {R.drawable.guest1, R.drawable.guest2, R.drawable.guest3, R.drawable.guest4,
            R.drawable.guest5,R.drawable.guest6,R.drawable.guest7,R.drawable.guest8,R.drawable.guest9,
            R.drawable.guest10};
    int table_id[] = {R.id.Table1, R.id.Table2, R.id.Table3, R.id.Table4, R.id.Table5, R.id.Table6};
    int timer_id_h[] = {R.id.timer1_h, R.id.timer2_h, R.id.timer3_h, R.id.timer4_h, R.id.timer5_h, R.id.timer6_h};
    int timer_id_k[] = {R.id.timer1_k, R.id.timer2_k, R.id.timer3_k, R.id.timer4_k, R.id.timer5_k, R.id.timer6_k};
    int order_id_h[] = {R.id.order1_h, R.id.order2_h, R.id.order3_h, R.id.order4_h, R.id.order5_h, R.id.order6_h};
    int order_id_k[] = {R.id.order1_k, R.id.order2_k, R.id.order3_k, R.id.order4_k, R.id.order5_k, R.id.order6_k};
    int ingreBtnIDs[] = {R.id.ingre0, R.id.ingre1, R.id.ingre2, R.id.ingre3,
                         R.id.ingre4, R.id.ingre5, R.id.ingre6, R.id.ingre7,
                         R.id.ingre8, R.id.ingre9,R.id.ingre10, R.id.ingre11,
                         R.id.ingre12, R.id.ingre13, R.id.ingre14, R.id.ingre15};

    long[] guest_time = {0, 0, 0, 0, 0 ,0}; // 손님 별 시간
    long[] temp_time = {0, 0, 0, 0, 0, 0}; // 시간 계산 위해
    long world_time = 11 * HOUR + 10 * MIN; // 게임 세계 시간, 09:00 부터 시작

    ArrayList<Food> items;

    @Override
    protected void onResume() {
        super.onResume();
        world_time -= 10 * MIN; // 정지했다가 시작하면 10분씩 증가해서 임시로 막음

        if(cdT_World == null) {
            // 첫번째 매개변수에 있는 시간 만큼 스테이지가 실행되고 끝나면 onFinish, 두번째 매개변수 시간마다 onTick 함수 실행됨
            cdT_World = new CountDownTimer(10 * MIN, 5 * SEC) {
                @Override
                public void onTick(long l) {
                    // 별점이 0점 아래가 되면 게임 오버 후 홈 화면으로 이동
                    if (GameInstance.getInstance().getRating() <= 0.0f) {

                        // 기존 브금 잠시 끄고 게임 오버 효과음 흘러나옴
                        HomeActivity.mP.stop();
                        HomeActivity.mP = MediaPlayer.create(getApplicationContext(),R.raw.mp_gameover);
                        HomeActivity.mP.start();

                        HomeActivity.mP = null;

                        GameInstance.getInstance().init(); // 인스턴스 초기화해서 게임 정보 초기화함

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }
                    int hour = (int) (world_time / HOUR);
                    int minute = (int) ((world_time % HOUR) / MIN);

                    String cur_Time = "";

                    if (hour < 10) cur_Time += "0";
                    cur_Time += hour + ":";

                    if (minute < 10) cur_Time += "0";
                    cur_Time += minute;

                    tv_world_time.setText(cur_Time);

                    world_time += (10 * MIN);

                    // 시간이 일정시간이 되면 cdT_World의 onFinish 함수 실행 -> 정산 화면 출력
                    if (world_time > 15 * HOUR) {
                        this.onFinish();
                    }
                }

                @Override
                public void onFinish() {
                    // 한 스테이지가 끝나면 정산 화면이 출력됨
                    Intent intent = new Intent(getApplicationContext(), SettlementActivity.class);
                    intent.putExtra("status", 1);

                    startActivity(intent);
                }
            }.start();
        }

        // 메인 액티비티가 처음 실행되거나 재실행됐을 때
        // 테이블마다 comeGuest 함수를 실행해서 손님이 들어와야하는지, 멈춘 시간을 다시 흘러가게 해야 하는지
        for(int i = 0; i < 6; i++)
        {
            final int index = i;
            comeGuest(index);
        }
    }

    // 메인액티비티에서 다른 액티비티로 넘어가거나 그럴 때
    // onPause 함수가 실행되면서 기존에 있던 타이머들을 멈춰야함 -> 안 멈추면 다른 액티비티에 영향
    @Override
    protected void onPause() {
        super.onPause();

        if(cdT_World != null) {
            cdT_World.cancel();
            cdT_World = null;
        }

        for(int i = 0; i < 6; i++)
        {
            final int index = i;

            if(cDT_Table[index] != null) {
                cDT_Table[index].cancel();
                cDT_Table[index] = null;
            }

            if(cDT_Guest[index] != null) {
                cDT_Guest[index].cancel();
                cDT_Guest[index] = null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();
        final GameAdapter adapter = new GameAdapter(this, items);
        listView.setAdapter(adapter); // 리스트뷰에 어댑터 설정

        // 완성된 음식 목록에 있는 음식 선택하면 대화상자가 뜨고 대화상자에서 음식을 몇 번 테이블에 서빙할지, 버릴 지 정함
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomDialog customDialog = new CustomDialog(MainActivity.this);

                toast_name = items.get(i).name;
                toast_quality = Integer.toString(items.get(i).quality);

                // 대화상자 꼭짓점 부분 둥글게
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.setDialogListener(new CustomDialog.CustomDialogInterface() {

                    // customDialog에서 버튼 클릭하면 클릭한 Tag 값이 전달됨
                    // Tag는 테이블의 번호 그리고 버리기에 대한 정보
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
                                    // 서빙 성공하면 성공한 횟수 1 증가
                                    GameInstance.getInstance().setSuccess_serv(GameInstance.getInstance().getSuccess_serv() + 1);

                                    int food_quality = items.get(i).quality;

                                    items.remove(i);
                                    adapter.notifyDataSetChanged();

                                    order_list_h[Tag].setText(""); // 주문 목록 제거
                                    order_list_k[Tag].setText("");

                                    guest[Tag].setVisibility(View.INVISIBLE); // 게스트 안보이게
                                    cDT_Table[Tag] = null;
                                    cDT_Guest[Tag] = null;

                                    exitGuest(Tag, food_quality);
                                    comeGuest(Tag); // 다음 게스트 들어옴
                                }
                                // 서빙 실패 시 토스트만 사라짐
                                else
                                {
                                    items.remove(i);
                                    adapter.notifyDataSetChanged();
                                }
                                break;
                            case TRASH:
                                items.remove(i);
                                adapter.notifyDataSetChanged();
                                break;

                        }
                    }

                });
                customDialog.show();
            }
        });

        //조리시작 버튼을 눌렀을 때
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //음식의 이름과 재료로 점수를 계산(scoring)
                Food food = new Food(choiceMenu, scoring(choiceMenu, ingredientList));

                //재료 객체 초기화(다음 음식을 받을 준비)
                ingredientList=new int[16];
                //progress bar 진행률 업데이트
                for(int i=0; i < progFood.getMax(); i++)
                {
                    progFood.setProgress(i+1);
                    //텀을 줄 수 있는 공간 필요
                }

                // 홀에 있는 서빙 리스트로 감
                items.add(food);
                adapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), choiceMenu+"의 조리가 완료되었습니다!", Toast.LENGTH_SHORT).show();
                tv_Ingredient.setText("");
            }
        });

        //초기화 버튼을 눌렀을 경우
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //메뉴 객체를 어떻게 할지 정해야 함 //굳이 어떻게 할 필요 없나? 새로운 객체를 생성하면 되서
                tv_Ingredient.setText("");
                progFood.setProgress(0);
                choiceMenu = "";//굳이 필요없음
                ingredientList = new int[16];
            }
        });


        btnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = (View) View.inflate(MainActivity.this, R.layout.item_box, null);
                android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("아이템");
                dlg.setView(dialogView);

                dlg.show();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SettlementActivity.class);
                intent.putExtra("status",0);

                startActivity(intent);
            }
        });
        // 주방 -> 홀 / 홀 -> 주방으로 이동할 수 있음
        btnChangeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IsInHall = !IsInHall;

                if(IsInHall)
                {
                    btnChangeView.setText("주방");
                }
                else
                {
                    btnChangeView.setText("홀");
                }
                vFlipper.showNext();
            }
        });

        //재료버튼이 눌렸을 때
        for(int i = 0; i < ingreBtnIDs.length; i++){
            final int index;
            index = i;

            ingreButtons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch(index){
                        case 0: ingredient = "빵"; break;
                        case 1: ingredient = "베이글"; break;
                        case 2: ingredient = "계란"; break;
                        case 3: ingredient = "햄"; break;
                        case 4: ingredient = "치즈"; break;
                        case 5: ingredient = "양상추"; break;
                        case 6: ingredient = "베이컨"; break;
                        case 7: ingredient = "해시브라운"; break;
                        case 8: ingredient = "소세지"; break;
                        case 9: ingredient = "마카로니"; break;
                        case 10: ingredient = "새우"; break;
                        case 11: ingredient = "불고기"; break;
                        case 12: ingredient = "피클"; break;
                        case 13: ingredient = "케챱"; break;
                        case 14: ingredient = "머스타드"; break;
                        case 15: ingredient = "치즈소스"; break;
                    }
                    //ingre = txtGredient.getText().toString() + "  " +ingreButtons[index].getText().toString();
                    ingredient = tv_Ingredient.getText().toString() + "  " + ingredient;
                    tv_Ingredient.setText(ingredient);

                    ingredientList[index]++;    //해당 재료의 갯수 1 증가(여러번 눌러도 되게 만들었음)
                }
            });
        }
    }

    // 게임 시작 시
    private void Init()
    {
        // xml, JAVA 연결해주는 부분
        listView        = (ListView)    findViewById(R.id.listView);
        tv_world_time   = (TextView)    findViewById(R.id.world_time);
        btnItem = (ImageButton) findViewById(R.id.itembtn);
        btnChangeView = (Button)      findViewById(R.id.btn_changeview);
        vFlipper        = (ViewFlipper) findViewById(R.id.viewFlipper);
        btnRecipe       = (ImageButton) findViewById(R.id.btnRecipe);
        btnRestart      = (ImageButton) findViewById(R.id.btnRestart);
        btnStart        = (ImageButton) findViewById(R.id.btnStart);
        tv_Ingredient = (TextView)    findViewById(R.id.txtGredient);
        progFood        = (ProgressBar) findViewById(R.id.progFood);
        tv_Day = (TextView)    findViewById(R.id.day);
        tv_Score = (TextView)    findViewById(R.id.score);
        ratingBar       = (RatingBar)   findViewById(R.id.life);
        btnPause = (ImageButton) findViewById(R.id.pausebtn);

        items = new ArrayList<>();

        for(int i = 0; i < guest_id.length; i++){
            final int index;
            index = i;
            timer_h[index] = findViewById(timer_id_h[index]);
            timer_k[index] = findViewById(timer_id_k[index]);
            guest[index] = findViewById(guest_id[index]);
            table[index] = findViewById(table_id[index]);
            order_list_h[index] = findViewById(order_id_h[index]);
            order_list_k[index] = findViewById(order_id_k[index]);
        }

        for(int i=0; i<ingreBtnIDs.length; i++){
            ingreButtons[i] = (ImageButton) findViewById(ingreBtnIDs[i]);
        } //재료버튼 등록

        // menu등록
        registerForContextMenu(btnRecipe);

        // 게임 인스턴스 통한 설정
        tv_Day.setText(Short.toString(GameInstance.getInstance().getStage()));
        ratingBar.setRating(GameInstance.getInstance().getRating());
        tv_Score.setText(Integer.toString(GameInstance.getInstance().getScore()));

        // 스테이지 별로 손님이 기다리는 시간을 다르게
        short Stage = GameInstance.getInstance().getStage();
        switch (Stage)
        {
            case 1:
                waiting_time_long = 70;
                waiting_time_middle = 60;
                waiting_time_short = 50;
                break;
            case 2:
                waiting_time_long = 60;
                waiting_time_middle = 50;
                waiting_time_short = 40;
                break;
            case 3:
                waiting_time_long = 50;
                waiting_time_middle = 40;
                waiting_time_short = 30;
                break;
        }
    }

    // 테이블이 비고 손님이 들어오기 까지의 시간이 지나면 손님이 들어오게 되고 주문을 한다.
    private void comeGuest(int index)
    {
        // cdT가 null이 아니라는 것은 이미 손님이 있다는 것이니 리턴
        if(cDT_Table[index] != null)
        {
            return;
        }
        else if(cDT_Table[index] == null)
        {
            // 일시정지하고 메인액티비티로 돌아왔을 때
            // guest_time[]을 Tick마다 기록해줘서 돌아왔을 때 그 시간으로 다시 타이머를 실행시킨다
            if(guest[index].getVisibility() == View.VISIBLE)
            {
                cDT_Guest[index] = new CountDownTimer(guest_time[index], SEC) {
                    @Override
                    public void onTick(long l) {
                        if(guest[index].getVisibility() == View.INVISIBLE)
                        {
                            this.cancel();
                        }

                        guest_time[index] = l;
                        temp_time[index] = l;
                        updateTimer(index);
                    }

                    @Override
                    public void onFinish() {
                        // 타이머가 종료되면 손님이 나가고 다음 손님이 들어오게 됨
                        is_time_over[index] = true;
                        guest[index].setVisibility(View.INVISIBLE);

                        order_list_h[index].setText("");
                        order_list_k[index].setText("");

                        cDT_Table[index] = null;
                        cDT_Guest[index] = null;

                        exitGuest(index, 0);
                        comeGuest(index);
                    }
                }.start();
            }
            // 처음 메인액티비티를 실행했을 때
            else if(guest[index].getVisibility() == View.INVISIBLE)
            {
                cDT_Table[index] = new CountDownTimer(5 * SEC, SEC) {
                    @Override
                    public void onTick(long l) {
                    }

                    @Override
                    public void onFinish() {
                        Human human = new Human();
                        int patience = human.patience;
                        int type = human.type;
                        // 손님 들어오면 손님 수 증가
                        GameInstance.getInstance().setVisited_guest(GameInstance.getInstance().getVisited_guest() + 1);

                        guest[index].setImageResource(guest_image[type]);
                        // 손님이 들어오고 나감을 Visibility로 설정
                        guest[index].setVisibility(View.VISIBLE);
                        Order(index);

                        switch (patience)
                        {
                            case 0:
                                timer_h[index].setText(Integer.toString(waiting_time_short));
                                timer_k[index].setText(Integer.toString(waiting_time_short));
                                break;
                            case 1:
                                timer_h[index].setText(Integer.toString(waiting_time_middle));
                                timer_k[index].setText(Integer.toString(waiting_time_middle));
                                break;
                            case 2:
                                timer_h[index].setText(Integer.toString(waiting_time_long));
                                timer_k[index].setText(Integer.toString(waiting_time_long));
                                break;
                        }
                        guest_time[index] = Long.parseLong(timer_h[index].getText().toString()) * SEC;

                        // 손님의 인내심 정보로 정한 시간을 바탕으로 타이머가 작동하게 됨
                        cDT_Guest[index] = new CountDownTimer(guest_time[index], SEC) {
                            @Override
                            public void onTick(long l) {
                                if(guest[index].getVisibility() == View.INVISIBLE)
                                {
                                    this.cancel();
                                }

                                guest_time[index] = l;
                                temp_time[index] = l;
                                updateTimer(index);
                            }

                            @Override
                            public void onFinish() {
                                // 타이머가 종료되면 손님이 나가고 다음 손님이 들어오게 됨
                                is_time_over[index] = true;
                                guest[index].setVisibility(View.INVISIBLE);

                                order_list_h[index].setText("");
                                order_list_k[index].setText("");

                                cDT_Table[index] = null;
                                cDT_Guest[index] = null;

                                exitGuest(index, 0);
                                comeGuest(index);
                            }
                        }.start();
                    }
                }.start();
            }

        }
        is_time_over[index] = false;
    }

    // 손님이 주문한 음식과 서빙할 음식이 맞는지 틀린지
    private boolean CheckOrder(int index, int table)
    {
        // '=='를 쓰면 안됨
        if(items.get(index).name.equals(order_list_h[table].getText().toString()))
            return true;
        else
            return false;
    }

    // 정해진 시간이 1초마다 감소되고 텍스트로 나타냄
    private void updateTimer(int index)
    {
        int seconds = (int) (temp_time[index] / SEC);
        String Left_time = "";

        // 10초 이하가 되면 글씨 붉어짐
        if(seconds < 10)
        {
            Left_time += "0";
            timer_h[index].setTextColor(Color.RED);
            timer_k[index].setTextColor(Color.RED);
        }
        else
        {
            // 기본 텍스트 컬러
            timer_h[index].setTextColor(order_list_k[index].getTextColors());
            timer_k[index].setTextColor(order_list_k[index].getTextColors());
        }
        Left_time += seconds;

        timer_h[index].setText(Left_time);
        timer_k[index].setText(Left_time);
    }

    // 손님의 주문
    private void Order(int index)
    {
        Random random = new Random();
        int select = random.nextInt(toast_menu.length);

        order_list_h[index].setText(toast_menu[select]);
        order_list_k[index].setText(toast_menu[select]);
    }

    // 손님 나갈 때 평가 후 점수, 별점에 반영
    private void exitGuest(int index, int food_quality)
    {
        float cur_rating = GameInstance.getInstance().getRating();
        int cur_score = GameInstance.getInstance().getScore();

        // 타임 오버가 돼서 나간 경우는 점수 못 얻고 별점 -0.5
        if(is_time_over[index])
        {
            cur_rating -= 0.5f;

            GameInstance.getInstance().setRating(cur_rating);
            ratingBar.setRating(GameInstance.getInstance().getRating());

            return;
        }

        // 음식 퀄리티를 기준으로 손님의 만족도를 상,중,하로 나눠
        //  0 ~ 20 : 하 / 30 ~ 40 : 중 / 50~60 : 상
        // 별점을 올릴지 유지할지 내릴지 정한다
        // 점수는 음식 퀄리티가 그대로 더해짐

        // 음식 상
        if(food_quality >= 50)
        {
            // 현재 별점이 5점이면 별점이 올라가지 않는다
            if(cur_rating < 5.0f)
                cur_rating += 0.5f;

            cur_score += food_quality;

            GameInstance.getInstance().setScore(cur_score);
            GameInstance.getInstance().setRating(cur_rating);

            ratingBar.setRating(GameInstance.getInstance().getRating());
            tv_Score.setText(Integer.toString(GameInstance.getInstance().getScore()));
        }
        // 음식 중
        else if(food_quality >= 30)
        {
            cur_score += food_quality;

            GameInstance.getInstance().setScore(cur_score);
            tv_Score.setText(Integer.toString(GameInstance.getInstance().getScore()));
        }
        // 음식 하
        else
        {
            cur_rating -= 0.5f;
            cur_score += food_quality;

            GameInstance.getInstance().setScore(cur_score);
            GameInstance.getInstance().setRating(cur_rating);

            ratingBar.setRating(GameInstance.getInstance().getRating());
            tv_Score.setText(Integer.toString(GameInstance.getInstance().getScore()));
        }

    }

    //레시피 버튼을 눌렀을 때
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mInflater = getMenuInflater();
        if(v==btnRecipe){
            menu.setHeaderTitle("음식 레시피");
            mInflater.inflate(R.menu.recipe1, menu);
        }
    }

    //레시피 버튼의 세부항목을 눌렀을 때 대화상자 나오게 하기
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);   //대화상자를 위한것?
        choiceMenu = item.getTitle().toString();
        dlg.setTitle(choiceMenu + "의 레시피입니다");
        //대화상자의 '확인'버튼을 눌렀을 때
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tv_Ingredient.setText(choiceMenu+'\n');
                progFood.setProgress(0);
                Toast.makeText(getApplicationContext(), choiceMenu+"을 선택하였습니다!", Toast.LENGTH_SHORT).show();
            }
        });
        dlg.setNegativeButton("취소", null);
        switch(item.getItemId()){
            case R.id.menu1:
                dlg.setMessage("빵/계란/햄/양상추/치즈/피클");
                dlg.show();
                return true;
            case R.id.menu2:
                dlg.setMessage("빵/계란/베이컨/양상추/치즈/피클");
                dlg.show();
                return true;
            case R.id.menu3:
                dlg.setMessage("빵/계란/해시브라운/치즈/머스타드/치즈소스");
                dlg.show();
                return true;
            case R.id.menu4:
                dlg.setMessage("빵/계란/소세지/양상추/치즈/머스타드");
                dlg.show();
                return true;
            case R.id.menu5:
                dlg.setMessage("빵/계란/피클/새우/양상추/치즈");
                dlg.show();
                return true;
            case R.id.menu6:
                dlg.setMessage("빵/계란/피클/불고기/양상추/치즈");
                dlg.show();
                return true;
            case R.id.menu7:
                dlg.setMessage("베이글/계란/베이컨/치즈/머스타드/치즈소스");
                dlg.show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    //점수를 계산하는 메서드
    public Integer scoring(String menu, int[] ingredientList)
    {
        int score=0;
        int index;
        int[] recipe = recipeBook.get(menu);
        for(int i=0; i<ingredientList.length; i++){
            index = i;
            if(recipe[index]==0){
                if(recipe[index]<ingredientList[index]) score-=10;
            }
            else{
                if(recipe[index]<=ingredientList[index]) score+=10;
            }
        }
        return score;
    }

}

// 손님의 정보를 담는 Human 클래스
class Human
{
    int patience; // 사람 인내심 -> 상/중/하로 구분
    int type; // 사람 이미지

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
    int quality; // 토스트의 음식 완성도

    Food(String name, int quality)
    {
        this.name = name;
        this.quality = quality;
    }
}