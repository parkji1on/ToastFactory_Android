package com.example.tykoon;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {

    Button refreshBtn;
    RecyclerView recyclerView;
    private List<RankVO> userRanks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_view_xml);

        refreshBtn = findViewById(R.id.rank_refresh_btn);
        recyclerView = findViewById(R.id.recyclerview);

        /* initiate adapter */
        MyRecyclerAdapter mRecyclerAdapter = new MyRecyclerAdapter();

        /* initiate recyclerview */
        recyclerView.setAdapter(mRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        /* adapt data */
        userRanks = new ArrayList<>();
        for(Long i = 1L; i <= 10; i++){
            if(i%2==0)
                userRanks.add(new RankVO(i,i+"번째 사람",i*100));
            else
                userRanks.add(new RankVO(i,i+"번째 사람",i*100));

        }
        mRecyclerAdapter.setUserRanks(userRanks);
    }

}
