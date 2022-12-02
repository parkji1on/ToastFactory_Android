package com.example.tykoon.rank;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tykoon.R;
import com.example.tykoon.retrofit.Retrofit_interface;
import com.example.tykoon.retrofit.model.BaseResponse;
import com.example.tykoon.retrofit.model.UserRank;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RankActivity extends AppCompatActivity {

    private Button refreshBtn, backBtn;
    private RecyclerView recyclerView;
    private List<UserRank> userRanks;

    private Retrofit mRetrofit;
    private Retrofit_interface retrofit_interface;

    private Call<BaseResponse<String>> rankDataRes;
    private Call<BaseResponse<List<UserRank>>> rankListRes;

    private int offset = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_view_xml);

        backBtn = findViewById(R.id.rank_back_btn);
        refreshBtn = findViewById(R.id.rank_refresh_btn);
        recyclerView = findViewById(R.id.recyclerview);

        // retrofit logic
        userRanks = new ArrayList<>();
        setRetrofitInit();
        getRankList(0);

        // refreshBtn logic
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // 버튼 호출시 api 재호출
                userRanks.clear();
                getRankList(offset);
            }
        });

    }

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
                System.out.println("call = " + call);
                return;
            }
            // 새로운 요소가 없을 경우 확인
            if (response.body().getResult().isEmpty()) {
                return;
            }
            
            for (UserRank userRank : response.body().getResult()) {
                System.out.println("userRank.getRank() = " + userRank.getRank());
                System.out.println("userRank.getName() = " + userRank.getName());
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
        rankDataRes = retrofit_interface.getUserRank(name);
        rankDataRes.enqueue(mRetrofitCallback);
    }

    private Callback<BaseResponse<String>> mRetrofitCallback = new Callback<BaseResponse<String>>() {
        @Override
        public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
            if (!response.isSuccessful()){
                return;
            }
            String result = response.body().getResult();
        }

        @Override
        public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
            t.printStackTrace();
        }
    };

}
