package com.lwm.guesssong.model;

import android.widget.Button;

/**
 * @author：lwm on 2016/7/23 10:12
 * @updateDate：2016/7/23
 * @version：1.0
 * @email：846988094@qq.com
 */
public class WordButton {
    //索引，可见与否，文字，按钮
    //设置公众属性，供外界访问
    public int mIndex;
    public boolean mIsVisiable;
    public String mCurrentWord;
    public Button mCurrentButton;
    //构造函数初始化
    public WordButton(){
        mIsVisiable=true;
        mCurrentWord="";
    }
}
