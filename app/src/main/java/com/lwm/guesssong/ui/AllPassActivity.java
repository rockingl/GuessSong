package com.lwm.guesssong.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.lwm.guesssong.R;

public class AllPassActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //显示通关页面
        setContentView(R.layout.all_pass_view);
        //隐藏title的金币按钮
        FrameLayout view=(FrameLayout)findViewById(R.id.layout_bar_coin);
        view.setVisibility(View.INVISIBLE);
    }
}
