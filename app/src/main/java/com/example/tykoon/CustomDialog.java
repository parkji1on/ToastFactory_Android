package com.example.tykoon;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog implements View.OnClickListener {
    private CustomDialogInterface customDialogInterface;
    private Context context;
    private Button btn[] = new Button[6];
    private Button btn_trash;
    TextView tv_name,tv_quality;

    // MainActivity로 전달할 변수
    private int result;

    // 태그
    final static int TABLE_1 = 0;
    final static int TABLE_2 = 1;
    final static int TABLE_3 = 2;
    final static int TABLE_4 = 3;
    final static int TABLE_5 = 4;
    final static int TABLE_6 = 5;
    final static int TRASH = 6;

    int btn_id[] = {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6};

    interface CustomDialogInterface{
        void BtnClicked(int TAG);
    }

    public void setDialogListener(CustomDialogInterface customDialogInterface)
    {
        this.customDialogInterface = customDialogInterface;
    }

    public CustomDialog(Context context)
    {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serve_dialog);

        for(int i = 0; i < btn_id.length; i++)
        {
            final int index = i;

            btn[index] = findViewById(btn_id[index]);
            btn[index].setOnClickListener(this);
        }
        btn_trash = findViewById(R.id.btn_trash);
        btn_trash.setOnClickListener(this);

        tv_name = findViewById(R.id.toast_name);
        tv_quality = findViewById(R.id.toast_quality);

        tv_name.setText(MainActivity.toast_name);
        tv_quality.setText(MainActivity.toast_quality);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btn1:
                result = TABLE_1;
                break;
            case R.id.btn2:
                result = TABLE_2;
                break;
            case R.id.btn3:
                result = TABLE_3;
                break;
            case R.id.btn4:
                result = TABLE_4;
                break;
            case R.id.btn5:
                result = TABLE_5;
                break;
            case R.id.btn6:
                result = TABLE_6;
                break;
            case R.id.btn_trash:
                result = TRASH;
                break;
        }
        customDialogInterface.BtnClicked(result);
        dismiss();
    }
}
