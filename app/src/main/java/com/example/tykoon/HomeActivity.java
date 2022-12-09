package com.example.tykoon;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.tykoon.rank.RankActivity;

public class HomeActivity extends AppCompatActivity {

    ImageButton btnGameStart, btnRanking;
    EditText dlgEdtName, dlgEdtEmail;
    View dialogView;
    String id, password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);
        setTitle("사용자 정보 입력");

        btnGameStart = (ImageButton) findViewById(R.id.btnGameStart);
        btnRanking = (ImageButton) findViewById(R.id.btnRanking);

        btnGameStart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialogView = (View) View.inflate(HomeActivity.this, R.layout.dialog1, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(HomeActivity.this);
                dlg.setTitle("사용자 정보 입력");
                dlg.setView(dialogView);
                dlg.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dlgEdtName = (EditText) dialogView.findViewById(R.id.edtID);
                        dlgEdtEmail = (EditText) dialogView.findViewById(R.id.edtPW);

                        id = dlgEdtName.getText().toString();
                        password = dlgEdtEmail.getText().toString();

                        //inflate를 통해서 정보 날리기

                        //id와 password를 비교해서 로그인할지 안할지 결정
                        //로그인 될때에는 Toast.makeText(MainActivity.this, id+"님 환영합니다", Toast.LENGTH_LONG).show(); 을 출력
                        //다음 activity로 넘김
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        intent.putExtra("ID name", id);
                        startActivity(intent);

                        //로그인 되지 않을 때에는 아이디에 대한 비밀번호가 있는게 그것이 틀렸을 때
                        //Toast.makeText(MainActivity.this, id+"님 비밀번호가 틀렸습니다!", Toast.LENGTH_LONG).show(); 을 출력

                    }
                });
                dlg.setNegativeButton("취소", null);

            }
        });

        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //랭킹 쪽으로 뷰를 돌려주기
                Intent intent = new Intent(HomeActivity.this, RankActivity.class);
                startActivity(intent);
            }
        });

    }
}

