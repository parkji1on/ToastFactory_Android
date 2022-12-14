package com.example.tykoon;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ItemCustomDialog extends Dialog {
    private TextView txt_item0;
    private ImageButton btn_item0;
    private Integer item0_rest;

    public ItemCustomDialog(@NonNull Context context, Integer item) {
        super(context);
        setContentView(R.layout.item_box);

        item0_rest = item;
        txt_item0 = findViewById(R.id.txt_item0);
        txt_item0.setText(item0_rest.toString());
        btn_item0 = findViewById(R.id.btn_item0);
        btn_item0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Integer item0_rest = Integer.parseInt(txt_item0.getText().toString());
                if(item0_rest > 0){
                    item0_rest = item0_rest-1;
                    GameInstance.getInstance().setItem_rest(item0_rest);
                    GameInstance.getInstance().setRating(GameInstance.getInstance().getRating()+0.5f);
                    txt_item0.setText(item0_rest.toString());
                }
                //else Toast.makeText(getApplicationContext(), "아이템의 수가 부족합니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
