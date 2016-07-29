package com.lwm.guesssong.customUi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.lwm.guesssong.R;
import com.lwm.guesssong.model.WordButton;
import com.lwm.guesssong.oberserIf.IWordButtonOb;
import com.lwm.guesssong.util.Util;

import java.util.ArrayList;

/**
 * @author：lwm on 2016/7/23 10:27
 * @updateDate：2016/7/23
 * @version：1.0
 * @email：846988094@qq.com
 */
public class MyGridView  extends GridView{
    //定义文字选择的大小
    public final static int WORD_COUNTS=24;
    //定义存放文字按钮的容器
    private ArrayList<WordButton> mWBs=new ArrayList<WordButton>();
    //定义适配器
    private GridViewAdapter mAdapter;
    //定义上下文
    private Context mContext;
    //定义文字按钮动画
    private Animation mWBAm;
    //定义观察者接口
    private IWordButtonOb mIWordButtonOb;
    //生成的构造方法
    public MyGridView(Context context, AttributeSet abs) {
        super(context, abs);
        //传递上下文，创建
        mContext=context;
        //为本类初始化适配器
        mAdapter=new GridViewAdapter();
        this.setAdapter(mAdapter);
    }

    //更新数据
    public void update(ArrayList<WordButton> list){
        mWBs=list;
        //设置数据源
        setAdapter(mAdapter);
    }

    //为这个类配置一个适配器（内部类）
    class GridViewAdapter extends BaseAdapter{
        //得到大小
        public int getCount(){
            return mWBs.size();
        }

        @Override
        public Object getItem(int pos) {
            return mWBs.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int pos, View view, ViewGroup viewGroup) {
            final WordButton mWB;
            //等于空时重新创建
            if(view==null){
                view=Util.getView(mContext, R.layout.word_button);
                mWB=mWBs.get(pos);
                mWB.mIndex=pos;
                mWBAm= AnimationUtils.loadAnimation(mContext,R.anim.scale);
                //定义每个按钮开始的时间
                mWBAm.setStartOffset(pos*100);
                mWB.mCurrentButton=(Button)view.findViewById(R.id.item_btn);
                //为按钮注册点击事件
                mWB.mCurrentButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mIWordButtonOb.onClickWordButton(mWB);
                    }
                });
                view.setTag(mWB);
            }else {
                mWB=(WordButton)view.getTag();
            }
            //设置按钮上的文字,开始动画
            mWB.mCurrentButton.setText(mWB.mCurrentWord);
            view.startAnimation(mWBAm);
            return view;
        }

    }
    //注册监听接口
    public void registerIWordButtonOb(IWordButtonOb iWordButtonOb){
        mIWordButtonOb=iWordButtonOb;
    }

}
