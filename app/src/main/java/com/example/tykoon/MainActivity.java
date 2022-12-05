package com.example.tykoon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Rating;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // 현재 뷰가 홀인지, 주방인지
    boolean IsInHall = true;

    // 시간 상수
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

    // 토스트 메뉴들
    final String toast_menu[] = {
            "햄치즈스페셜","베이컨베스트","햄치즈포테이토","더블소세지","새우","그릴드불고기","베이컨치즈베이글"};

    // 각 테이블별로 시간이 0초 아래가 됐는지 -> 필요할까? 일단 나둠
    boolean is_time_over[] = {false, false, false, false, false, false};

    CountDownTimer countDownTimer0, countDownTimer1, countDownTimer2;
    TextView timer_h[] = new TextView[6]; // Guest 시간 -> 홀에서 보이게
    TextView timer_k[] = new TextView[6]; // Guest 시간 -> 주방에서 보이게
    TextView txtGredient, txtMenu, txtScore, txtDay; // 재료, 메뉴, 점수, Day
    TextView tv_world_time; // 게임 세계 시간
    TextView order_list_h[] = new TextView[6]; // 테이블 별 주문 리스트뷰 -> 홀 텍스트
    TextView order_list_k[] = new TextView[6]; // 테이블 별 주문 리스트뷰 -> 홀 텍스트

    ImageButton guest[] = new ImageButton[6]; // Guest 이미지버튼 -> 버튼 기능 필요없을 시 뷰로 변경
    ImageButton table[] = new ImageButton[6]; // Table 이미지버튼 -> 버튼 기능 필요없을 시 뷰로 변경
    ImageButton ingreButtons[] = new ImageButton[16];
    ImageButton item_btn, pause_btn, btnRecipe, btnStart, btnRestart;;
    Button btn_changeView;

    ListView listView; // 완성된 음식 들어가는 리스트뷰
    ViewFlipper vFlipper;

    RatingBar ratingBar;
    ProgressBar progFood;

    String ingre = "\n\n\n";   //TextView에 보여줄 텍스트 생성
    String choiceMenu; //선택한 메뉴의 이름

    int[] ingredientList = new int[16]; //선택한 재료가 들어 있는지 여부
    //메뉴의 리스트 (menu) - (choicemenu와 ingredientList를 저장함) - 홀에서 필요한 정보
    Menu menu;  //Menu 객체를 위한것
    //레시피의 확인 절차를 위한 recipebook
    HashMap<String,int[]> recipebook = new HashMap<String,int[]>(){{
        put("햄치즈스페셜", new int[]{1,0,1,1,1,1,0,0,0,0,0,0,1,0,0,0});
        put("베이컨베스트", new int[]{1,0,1,0,1,1,1,0,0,0,0,0,1,0,0,0});
        put("햄치즈포테이토", new int[]{1,0,1,0,1,0,0,1,0,0,0,0,0,0,0,1});
        put("더블소세지", new int[]{1,0,1,0,1,1,0,0,1,0,0,0,0,0,0,0});
        put("새우", new int[]{1,0,1,0,1,1,0,0,0,0,1,0,1,0,0,0});
        put("그릴드불고기", new int[]{1,0,1,0,1,1,0,0,0,0,0,1,1,0,0,0});
        put("베이컨치즈베이글", new int[]{0,1,1,0,1,0,1,0,0,0,0,0,0,0,0,0});
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

    long guest_time = 0; // 손님 별 시간
    long temp_time = 0; // 시간 계산 위해
    long world_time = 9 * HOUR; // 게임 세계 시간, 09:00 부터 시작

    ArrayList<Food> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // xml, JAVA 연결해주는 부분
        listView        = (ListView)    findViewById(R.id.listView);
        tv_world_time   = (TextView)    findViewById(R.id.world_time);
        item_btn        = (ImageButton) findViewById(R.id.itembtn);
        btn_changeView  = (Button)      findViewById(R.id.btn_changeview);
        vFlipper        = (ViewFlipper) findViewById(R.id.viewFlipper);
        btnRecipe       = (ImageButton) findViewById(R.id.btnRecipe);
        btnRestart      = (ImageButton) findViewById(R.id.btnRestart);
        btnStart        = (ImageButton) findViewById(R.id.btnStart);
        txtGredient     = (TextView)    findViewById(R.id.txtGredient);
        txtMenu         = (TextView)    findViewById(R.id.txtMenu);
        progFood        = (ProgressBar) findViewById(R.id.progFood);
        txtDay          = (TextView)    findViewById(R.id.day);
        txtScore        = (TextView)    findViewById(R.id.score);
        ratingBar       = (RatingBar)   findViewById(R.id.life);

        items = new ArrayList<>();
        final GameAdapter adapter = new GameAdapter(this, items);
        listView.setAdapter(adapter); // 리스트뷰에 어댑터 설정

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
        }

        // menu등록
        registerForContextMenu(btnRecipe);

        // 게임 인스턴스 통한 설정
        txtDay.setText(Short.toString(GameInstance.getInstance().getStage()));
        ratingBar.setRating(GameInstance.getInstance().getRating());
        txtScore.setText(Integer.toString(GameInstance.getInstance().getScore()));

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

                // 1초마다
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
                                    items.remove(i);
                                    adapter.notifyDataSetChanged();

                                    order_list_h[Tag].setText(""); // 주문 목록 제거
                                    order_list_k[Tag].setText("");
                                    guest[Tag].setVisibility(View.INVISIBLE); // 게스트 안보이게
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

                menu = new Menu(choiceMenu, 0/*scoring(choiceMenu, ingredientList)*/);
                Food food = new Food(menu.menuName, menu.score);
                //Menu 객체를 생성 Menu(choiceMenu, scoreing());
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
                txtGredient.setText("");
                txtMenu.setText("\n\n\n");
            }
        });

        //초기화 버튼을 눌렀을 경우
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //메뉴 객체를 어떻게 할지 정해야 함 //굳이 어떻게 할 필요 없나? 새로운 객체를 생성하면 되서
                txtGredient.setText("");
                txtMenu.setText("\n\n\n");
                progFood.setProgress(0);
                choiceMenu = "";//굳이 필요없음
                ingredientList = new int[16];
            }
        });

        /* 임시로 완성된 음식 목록에 올리려고 한 코드 -> 나중에 주방이랑 연계
        item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();

                String str = toast_menu[rand.nextInt(toast_menu.length)];

                Food food = new Food(str, 50.0f);
                items.add(food);

                adapter.notifyDataSetChanged();
            }
        });
*/

        // 주방 -> 홀 / 홀 -> 주방으로 이동할 수 있음
        btn_changeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsInHall)
                {
                    btn_changeView.setText("주방");
                }
                else
                {
                    btn_changeView.setText("홀");
                }
                IsInHall = !IsInHall;
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
                        case 0: ingre = "빵"; break;
                        case 1: ingre = "베이글"; break;
                        case 2: ingre = "계란"; break;
                        case 3: ingre = "햄"; break;
                        case 4: ingre = "치즈"; break;
                        case 5: ingre = "양상추"; break;
                        case 6: ingre = "베이컨"; break;
                        case 7: ingre = "해시브라운"; break;
                        case 8: ingre = "소세지"; break;
                        case 9: ingre = "마카로니"; break;
                        case 10: ingre = "새우"; break;
                        case 11: ingre = "불고기"; break;
                        case 12: ingre = "피클"; break;
                        case 13: ingre = "케챱"; break;
                        case 14: ingre = "머스타드"; break;
                        case 15: ingre = "치즈소스"; break;
                    }
                    //ingre = txtGredient.getText().toString() + "  " +ingreButtons[index].getText().toString();
                    ingre = txtGredient.getText().toString() + "  " + ingre;
                    txtGredient.setText(ingre);
                    //i를 쓰면 안되고 index를 써야함 왜인지는 잘 모르겠다...
                    ingredientList[index]++;    //해당 재료의 갯수 1 증가(여러번 눌러도 되게 만들었음)
                }
            });
        }

    }

    // 테이블이 비고 손님이 들어오기 까지의 시간이 지나면 손님이 들어오게 되고 주문을 한다.
    private void comeGuest(int index)
    {
        if(guest[index].getVisibility() == View.VISIBLE)
        {
            return;
        }

        is_time_over[index] = false;

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
                        timer_h[index].setText(Integer.toString(50));
                        timer_k[index].setText(Integer.toString(50));
                        break;
                    case 1:
                        timer_h[index].setText(Integer.toString(60));
                        timer_k[index].setText(Integer.toString(60));
                        break;
                    case 2:
                        timer_h[index].setText(Integer.toString(70));
                        timer_k[index].setText(Integer.toString(70));
                        break;
                }
                guest_time = Long.parseLong(timer_h[index].getText().toString()) * SEC;

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
                        is_time_over[index] = true;
                        guest[index].setVisibility(View.INVISIBLE);
                        order_list_h[index].setText("");
                        exitGuest(index, 0);
                        comeGuest(index);
                    }


                }.start();
            }
        }.start();
    }

    // 손님이 주문한 음식과 서빙할 음식이 맞는지 틀린지
    private boolean CheckOrder(int index, int table)
    {
        if(items.get(index).name == order_list_h[table].getText().toString())
            return true;
        else
            return false;
    }

    // 정해진 시간이 1초마다 감소되고 텍스트로 나타냄
    private void updateTimer(int index)
    {
        int seconds = (int) temp_time / SEC;
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

        // 음식 상
        if(food_quality >= 70)
        {
            cur_rating += 0.5f;
            cur_score += food_quality;

            GameInstance.getInstance().setScore(cur_score);
            GameInstance.getInstance().setRating(cur_rating);

            ratingBar.setRating(GameInstance.getInstance().getRating());
            txtScore.setText(Integer.toString(GameInstance.getInstance().getScore()));
        }
        // 음식 중
        else if(food_quality >= 30)
        {
            cur_score += food_quality;

            GameInstance.getInstance().setScore(cur_score);
            txtScore.setText(Integer.toString(GameInstance.getInstance().getScore()));
        }
        // 음식 하
        else
        {
            cur_rating -= 0.5f;
            cur_score += food_quality;

            GameInstance.getInstance().setScore(cur_score);
            GameInstance.getInstance().setRating(cur_rating);

            ratingBar.setRating(GameInstance.getInstance().getRating());
            txtScore.setText(Integer.toString(GameInstance.getInstance().getScore()));
        }

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
            // 리스트뷰에 들어갈 아이템의 xml
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
                txtMenu.setText(choiceMenu+'\n');
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
                dlg.setMessage("빵/계란/해시브라운/치즈/치즈소스");
                dlg.show();
                return true;
            case R.id.menu4:
                dlg.setMessage("빵/계란/소세지/양상추/치즈");
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
                dlg.setMessage("베이글/계란/베이컨/치즈");
                dlg.show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    //Menu 객체
    public class Menu
    {
        public String menuName;
        public int score;

        public Menu() {}

        public Menu(String name)
        {
            this.menuName = name;
        }

        public Menu(String name, int score)
        {
            this.menuName = name;
            this.score = score;
        }

        public boolean IsEqualMenu(String menu)
        {
            return menu==this.menuName;
        }
    }

    //점수를 계산하는 메서드
    public Integer scoring(String menu, int[] ingredientList)
    {
        int score=0;
        int[] recipe = recipebook.get(menu);
        for(int i=0; i<ingredientList.length; i++){
            if(recipe[i]<ingredientList[i]) score+=10;
        }
        return score;
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