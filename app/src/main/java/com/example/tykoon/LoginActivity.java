package com.example.tykoon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tykoon.retrofit.Retrofit_interface;
import com.example.tykoon.retrofit.model.BaseResponse;
import com.example.tykoon.retrofit.model.PostMemberReq;
import com.example.tykoon.retrofit.model.UserRank;

import java.io.InterruptedIOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    ImageButton btnLogin, btnJoin;
    EditText dlgEdtName, dlgEdtEmail;
    View dialogView;
    String name, password;

    private Retrofit mRetrofit;
    private Retrofit_interface retrofit_interface;

    private Call<BaseResponse<String>> memberRes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        setTitle("로그인 및 회원가입");

        btnLogin = findViewById(R.id.btnLogin);
        btnJoin = findViewById(R.id.btnJoin);

        setRetrofitInit();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView = (View) View.inflate(LoginActivity.this, R.layout.dialog1, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(LoginActivity.this);
                dlg.setTitle("로그인");
                dlg.setView(dialogView);
                dlg.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dlgEdtName = (EditText) dialogView.findViewById(R.id.edtID);
                        dlgEdtEmail = (EditText) dialogView.findViewById(R.id.edtPW);

                        name = dlgEdtName.getText().toString();
                        password = dlgEdtEmail.getText().toString();
                        postMemberLogin();  // login retrofit
                    }
                });
                dlg.setNegativeButton("취소", null);

                AlertDialog alertDialog = dlg.create();
                alertDialog.show();
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView = (View) View.inflate(LoginActivity.this, R.layout.dialog1, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(LoginActivity.this);
                dlg.setTitle("회원가입");
                dlg.setView(dialogView);
                dlg.setPositiveButton("회원가입", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dlgEdtName = (EditText) dialogView.findViewById(R.id.edtID);
                        dlgEdtEmail = (EditText) dialogView.findViewById(R.id.edtPW);

                        name = dlgEdtName.getText().toString();
                        password = dlgEdtEmail.getText().toString();
                        postMember();   // 회원가입 retrofit
                    }
                });
                dlg.setNegativeButton("취소", null);

                AlertDialog alertDialog = dlg.create();
                alertDialog.show();
            }
        });

    }


    /**
     * Retrofit 기본 URL 설정 메서드
     * baseUrl을 설정해준 해당 Retrofit 객체를 retrofit_interface 객체로 사용
     * */
    private void setRetrofitInit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://toastfactory.shop/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit_interface = mRetrofit.create(Retrofit_interface.class);
    }

    /** @POST("member") */
    private void postMember() {
        memberRes = retrofit_interface.postMember(new PostMemberReq(name, password));
        memberRes.enqueue(mRetrofitJoinCallback);
    }

    private Callback<BaseResponse<String>> mRetrofitJoinCallback = new Callback<BaseResponse<String>>() {
        @Override
        public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
            // 성공 여부 확인
            if (!response.isSuccessful()){
                Log.getStackTraceString(new InterruptedIOException());
                return;
            }

            if (response.body().getCode() == 1000) {
                Toast.makeText(LoginActivity.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
            } else if (response.body().getCode() == 2001) {
                Toast.makeText(LoginActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
            t.printStackTrace();
        }
    };

    /** @POST("member/login") */
    private void postMemberLogin() {
        memberRes = retrofit_interface.postMemberLogin(new PostMemberReq(name, password));
        memberRes.enqueue(mRetrofitLoginCallback);
    }

    private Callback<BaseResponse<String>> mRetrofitLoginCallback = new Callback<BaseResponse<String>>() {
        @Override
        public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
            // 성공 여부 확인
            if (!response.isSuccessful()){
                Log.getStackTraceString(new InterruptedIOException());
                return;
            }

            System.out.println("response = " + response.body().getCode());
            if (response.body().getCode() == 1000) {
                Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                // 게임 인스턴스에 ID 넣는부분 추가
                GameInstance.getInstance().setPlayerID(name);
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            } else if (response.body().getCode() == 2002) {
                Toast.makeText(LoginActivity.this, "이름 정보를 확인하세요", Toast.LENGTH_SHORT).show();
            } else if (response.body().getCode() == 2004) {
                Toast.makeText(LoginActivity.this, "비밀번호 정보를 확인하세요", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
            t.printStackTrace();
        }
    };

}
