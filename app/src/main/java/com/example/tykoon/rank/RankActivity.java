package com.example.tykoon.rank;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tykoon.HomeActivity;
import com.example.tykoon.MainActivity;
import com.example.tykoon.R;
import com.example.tykoon.retrofit.Retrofit_interface;
import com.example.tykoon.retrofit.model.BaseResponse;
import com.example.tykoon.retrofit.model.UserRank;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RankActivity extends AppCompatActivity {

    private Button refreshBtn, backBtn;
    private TextView myRankTextView;
    private TextView myScoreTextView;
    private TextView myNameTextView;
    private View myRankLayout;
    private RecyclerView recyclerView;
    private List<UserRank> userRanks;

    private Retrofit mRetrofit;
    private Retrofit_interface retrofit_interface;

    private Call<BaseResponse<UserRank>> rankMyDataRes;
    private Call<BaseResponse<List<UserRank>>> rankListRes;

    private int offset = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_view_xml);

        backBtn = findViewById(R.id.rank_back_btn);
        refreshBtn = findViewById(R.id.rank_refresh_btn);
        recyclerView = findViewById(R.id.recyclerview);

        myNameTextView = findViewById(R.id.rank_my_name);
        myRankTextView = findViewById(R.id.rank_my_rank);
        myScoreTextView = findViewById(R.id.rank_my_score);
        myRankLayout = findViewById(R.id.my_rank_layout);

        // retrofit logic
        userRanks = new ArrayList<>();
        setRetrofitInit();
        getRankList(0);

        // TODO: name 변수 받아오기
        getUserRank("상민");

        // refreshBtn logic
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // 버튼 호출시 api 재호출
                userRanks.clear();
                getRankList(offset);
                getUserRank("상민");
            }
        });

        // BackBtn logic
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // 버튼 호출시 Home 화면으로 이동
                Intent intent = new Intent(RankActivity.this, HomeActivity.class);
                startActivity(intent);
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

    /** @GET("rank/") */
    private void getRankList(int offset) {
        rankListRes = retrofit_interface.getRankList(offset);
        rankListRes.enqueue(mRetrofitListCallback);
    }

    private Callback<BaseResponse<List<UserRank>>> mRetrofitListCallback = new Callback<BaseResponse<List<UserRank>>>() {
        @Override
        public void onResponse(Call<BaseResponse<List<UserRank>>> call, Response<BaseResponse<List<UserRank>>> response) {
            // 성공 여부 확인
            if (!response.isSuccessful()){
                Log.getStackTraceString(new InterruptedIOException());
                return;
            }
            // 새로운 요소가 없을 경우 확인
            if (response.body().getResult().isEmpty()) {
                return;
            }
            
            userRanks.addAll(response.body().getResult());
            createRecycler();
        }

        @Override
        public void onFailure(Call<BaseResponse<List<UserRank>>> call, Throwable t) {
            t.printStackTrace();
        }
    };

    private void createRecycler() {
        /* initiate adapter */
        MyRecyclerAdapter mRecyclerAdapter = new MyRecyclerAdapter();

        /* initiate recyclerview */
        recyclerView.setAdapter(mRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /* adapt data */
        mRecyclerAdapter.setUserRanks(userRanks);
    }

    /**  @GET("rank/my") */
    private void getUserRank(String name) {
        rankMyDataRes = retrofit_interface.getUserRank(name);
        rankMyDataRes.enqueue(mRetrofitCallback);
    }

    private Callback<BaseResponse<UserRank>> mRetrofitCallback = new Callback<BaseResponse<UserRank>>() {
        @Override
        public void onResponse(Call<BaseResponse<UserRank>> call, Response<BaseResponse<UserRank>> response) {
            if (!response.isSuccessful()){
                Log.getStackTraceString(new InterruptedIOException());
                return;
            }
            UserRank userRank = response.body().getResult();
            myRankLayout.setVisibility(View.VISIBLE);
            myNameTextView.setText(userRank.getName());
            myRankTextView.setText(String.valueOf(userRank.getRank()));
            myScoreTextView.setText(String.valueOf(userRank.getScore()));
        }

        @Override
        public void onFailure(Call<BaseResponse<UserRank>> call, Throwable t) {
            t.printStackTrace();
        }
    };

}
