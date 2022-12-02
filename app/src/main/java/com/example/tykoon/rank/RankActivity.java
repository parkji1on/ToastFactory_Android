package com.example.tykoon.rank;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

    Button refreshBtn;
    RecyclerView recyclerView;
    private List<UserRank> userRanks;

    private Retrofit mRetrofit;
    private Retrofit_interface retrofit_interface;

    Call<BaseResponse<String>> rankDataRes;
    Call<BaseResponse<List<UserRank>>> rankListRes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_view_xml);

        refreshBtn = findViewById(R.id.rank_refresh_btn);
        recyclerView = findViewById(R.id.recyclerview);

        // retrofit logic
        setRetrofitInit();
        getRankList(0);

        // refreshBtn logic
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // 버튼 호출시 api 재호출
                getRankList(0);
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
            if (!response.isSuccessful()){
                System.out.println("call = " + call);
                return;
            }

            userRanks = response.body().getResult();
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
                System.out.println("call = " + call);
                return;
            }
            String result = response.body().getResult();
            System.out.println("result = " + result);
        }

        @Override
        public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
            t.printStackTrace();
        }
    };

}
