package com.lwm.guesssong.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lwm.guesssong.R;
import com.lwm.guesssong.customUi.MyGridView;
import com.lwm.guesssong.data.Const;
import com.lwm.guesssong.model.Song;
import com.lwm.guesssong.model.WordButton;
import com.lwm.guesssong.oberserIf.IAlertDialogButtonListener;
import com.lwm.guesssong.oberserIf.IWordButtonOb;
import com.lwm.guesssong.util.MyPlayer;
import com.lwm.guesssong.util.SLog;
import com.lwm.guesssong.util.Util;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements IWordButtonOb{
    //定义当前类的标记
    public static final String TAG="MainActivity";

    //定义当前答案的状态
    public static final int ANSWER_RIGHT=1;
    public static final int ANSWER_WRONG=2;
    public static final int ANSWER_LACK=3;

    //定义显示对话框的id
    public static final int ID_DELETE_WORD=1;
    public static final int ID_TIP_ANSWER=2;
    public static final int ID_COIN_LACK=3;

    //定义动画
    //转盘旋转
    private Animation mPanDiscAm;
    private LinearInterpolator mPanDiscAmLin;
    //拨杆进
    private Animation mLeverInAm;
    private LinearInterpolator mLeverInAmLin;
    //拨杆出
    private Animation mLeverOutAm;
    private LinearInterpolator mLeverOutAmLin;

    //定义界面组件
    //播放按钮
    private ImageButton mPlayButton;
    //转盘
    private ImageView mPanDisc;
    //拨杆
    private ImageView mLever;
    //判断用户是否在播放
    private boolean mIsRunning=false;

    //文字框
    //数据源（已选，待选）
    private ArrayList<WordButton> mSelectedWords;
    private ArrayList<WordButton> mAllWords;
    //组件
    private MyGridView mGridView;
    private LinearLayout mSelectedLl;

    //定义当前歌曲，当前歌曲的索引
    private Song mCurrentSong;
    private int mSongIndex=-1;

    //过关显示组件
    private View mPassGame;

    //定义闪烁标志
    private boolean mSpark=false;

    //定义当前的金币数
    private int mCurrentCoins=Const.INIT_COINS;

    //定义显示金币的控件
    private TextView mViewCoins;

    //定义删除 提示组件
    private ImageButton mViewDeleteWord;
    private ImageButton mViewTipAnswer;

    //定义当前关及其索引（过关弹出页）
    private int mCurrentPassIndex;
    private TextView mCurrentPass;

    //定义当前关的歌曲名称
    private TextView mCurrentPassSongName;

    //定义下一题按钮
    private ImageButton mNextPassButton;

    //定义当前关（主页面）
    private TextView mCurrentMainPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //初始化文字框组件
        mGridView=(MyGridView)findViewById(R.id.gridview);
        mSelectedLl=(LinearLayout)findViewById(R.id.word_select_container);
        //GridView注册此接口
        mGridView.registerIWordButtonOb(this);

        //读取数据
        int[] data=Util.readData(this);
        mSongIndex=data[Const.INDEX_CURRENT_PASS];
        mCurrentCoins=data[Const.INDEX_CURRENT_COINS];

        //初始化金币控件,添加初始金币
        mViewCoins=(TextView)findViewById(R.id.txt_bar_coins);
        mViewCoins.setText(mCurrentCoins + "");

        //初始化当前关(有问题)
        mCurrentMainPass=(TextView)findViewById(R.id.current_level);
        mCurrentMainPass.setText((mSongIndex+2) + "");

        //初始化按钮
        mPlayButton=(ImageButton)findViewById(R.id.btn_play_start);
        mPanDisc=(ImageView)findViewById(R.id.imageView1);
        mLever=(ImageView)findViewById(R.id.imageView2);
        //为按钮添加点击事件
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //播放动画
                hanlerPlay();
            }
        });
        //初始化动画,定义动画监听
        mPanDiscAm= AnimationUtils.loadAnimation(this, R.anim.rotate);
        mPanDiscAmLin=new LinearInterpolator();
        mPanDiscAm.setInterpolator(mPanDiscAmLin);
        mPanDiscAm.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //转盘结束后 拨杆返回
              mLever.startAnimation(mLeverOutAm);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLeverInAm=AnimationUtils.loadAnimation(this,R.anim.rotate_45);
        mLeverInAmLin=new LinearInterpolator();
        mLeverInAm.setInterpolator(mLeverInAmLin);
        //完成后停止在此位置
        mLeverInAm.setFillAfter(true);
        mLeverInAm.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //唱片开始
                mPanDisc.startAnimation(mPanDiscAm);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLeverOutAm=AnimationUtils.loadAnimation(this,R.anim.rotate_d_45);
        mLeverOutAmLin=new LinearInterpolator();
        mLeverOutAm.setInterpolator(mLeverOutAmLin);
        mLeverOutAm.setFillAfter(true);
        mLeverOutAm.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //结束后设置可见.结束标志置为false
                mIsRunning=false;
                mPlayButton.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //更新数据
        initGameData();
        //控制删除按钮
        handleDeleteButton();
        //控制提示按钮
        handleTipButton();
    }
    //点击事件触发后，开启动画
    private void hanlerPlay(){
        //拨杆不为空时候
        if(mLever!=null){
            if(!mIsRunning) {
                //动画开启后，默认正在运行
                mIsRunning=true;
                //拨杆旋转，唱片准备旋转
                mLever.startAnimation(mLeverInAm);
                //设置按钮不可见
                mPlayButton.setVisibility(View.INVISIBLE);
                //开始播放音乐
                MyPlayer.playSong(MainActivity.this, mCurrentSong.getFileName());
            }
        }
    }
    //实现接口方法
    @Override
    public void onClickWordButton(WordButton wordButton) {
        onClickAllWords(wordButton);
        int status=checkAnswer();
        if(status==ANSWER_RIGHT){
            //显示过关逻辑（如果通关了，就显示通关页面）
            if(isPassThisGame()){
                //最后一关时候，自动重置数据
                Util.saveData(MainActivity.this,-1,Const.INIT_COINS);
                //激活另一个通关Activity,
                Util.aliveActivity(MainActivity.this,AllPassActivity.class);
            }else{
                //显示下一关
                passView();
            }
        }else if(status==ANSWER_WRONG){
            //错误答案闪烁
            sparkWrongWords();
        }else{
            //答案不完整时候，显示答案为白色
            for(int i=0;i<mSelectedWords.size();i++){
                mSelectedWords.get(i).mCurrentButton.setTextColor(Color.WHITE);
            }
        }
    }
    //过关时候执行逻辑
    private void passView(){
        //过关显示过关界面可见
        mPassGame=(View)findViewById(R.id.pass_view);
        mPassGame.setVisibility(View.VISIBLE);
        //停止未完成的动画(转盘不在转动)
        mPanDisc.clearAnimation();

        //停止歌曲
        MyPlayer.stopSong(MainActivity.this);

        //显示过关音效(金币掉落)
        MyPlayer.playTone(MainActivity.this,MyPlayer.INDEX_COIN_TONE);

        //显示当前关的索引
        mCurrentPassIndex=mSongIndex;
        mCurrentPass=(TextView)findViewById(R.id.txt_current_stage_pass);
        mCurrentPass.setText((mCurrentPassIndex+1)+"");

        //显示歌曲名称
        mCurrentPassSongName=(TextView)findViewById(R.id.txt_current_song_name_pass);
        String[] tmp=Const.SONG_RES[mSongIndex];
        mCurrentPassSongName.setText(tmp[Const.INDEX_SONGNAME] + "");

        //点击下一题按钮
        mNextPassButton=(ImageButton)findViewById(R.id.btn_next);
        mNextPassButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //关闭此页面,重新初始化页面
                mPassGame.setVisibility(View.INVISIBLE);
                //重新定义关数
                mCurrentMainPass.setText((mCurrentPassIndex+2)+"");
                initGameData();
            }
        });

    }
    //判断是否通关
    private boolean isPassThisGame(){
        return mCurrentPassIndex==Const.SONG_RES.length-2;
    }
    //检查答案
    private int checkAnswer(){
        //如果有一个为空，则答案不完整
        for(int i=0;i<mSelectedWords.size();i++){
            if(mSelectedWords.get(i).mCurrentWord.equals("")){
                return ANSWER_LACK;
            }
        }
        //判断所选答案是否与正确答案相同
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<mSelectedWords.size();i++){
            sb.append(mSelectedWords.get(i).mCurrentWord);
        }
        if(String.valueOf(sb).equals(mCurrentSong.getSongName())){
            return ANSWER_RIGHT;

        }else{
            return ANSWER_WRONG;
        }
    }
    //答案错误时闪烁（确保运行在ui线程中）
    private void sparkWrongWords(){
        TimerTask task=new TimerTask(){
           // boolean mSpark=true;
            int mSparkTimes=-1;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSparkTimes++;
                        if(mSparkTimes>6){
                            return;
                        }
                        if(!mSpark){
                            for(int i=0;i<mSelectedWords.size();i++){
                                mSelectedWords.get(i).mCurrentButton.setTextColor(Color.RED);
                                mSpark=true;
                            }
                        }else {
                            for(int i=0;i<mSelectedWords.size();i++){
                                mSelectedWords.get(i).mCurrentButton.setTextColor(Color.WHITE);
                                mSpark=false;
                            }
                        }
                    }
                });
            }
        };
        Timer timer=new Timer();
        timer.schedule(task,1,300);

    }
    //点击待选按钮效果
    private void onClickAllWords(WordButton wordButton){
        for(int i=0;i<mSelectedWords.size();i++){
            if(mSelectedWords.get(i).mCurrentWord.equals("")){
                //得到选中按钮的值,并记住当前值的索引
                mSelectedWords.get(i).mCurrentButton.setText(wordButton.mCurrentWord);
                mSelectedWords.get(i).mIsVisiable=true;
                mSelectedWords.get(i).mCurrentWord=wordButton.mCurrentWord;
                mSelectedWords.get(i).mIndex=wordButton.mIndex;
                mSelectedWords.get(i).mCurrentButton.setEnabled(true);

                //记录日志信息(当前点击按钮的索引)
                SLog.d(TAG,wordButton.mIndex+"");

                //点击按钮的值不可见,并设置按钮不可点击
                wordButton.mIsVisiable=false;
                wordButton.mCurrentButton.setVisibility(View.INVISIBLE);
                //可见性
                SLog.d(TAG, wordButton.mIsVisiable + "");
                break;
            }

        }
    }
    @Override
    protected void onPause() {
        //暂停时候动画暂停,歌曲也暂停
        mPanDisc.clearAnimation();
        MyPlayer.stopSong(MainActivity.this);
        //保存数据（如果是最后一关，不执行此逻辑）
        if(!isPassThisGame()){
        Util.saveData(MainActivity.this,mSongIndex-1,mCurrentCoins);
        }
        super.onPause();
    }
    //点击已选择框的效果
    private void clearSelected(WordButton wordButton){
        //设置按钮可见
        mAllWords.get(wordButton.mIndex).mIsVisiable=true;
        mAllWords.get(wordButton.mIndex).mCurrentButton.setVisibility(View.VISIBLE);

        //可见性
        SLog.d(TAG, wordButton.mIsVisiable + "");
        //当前点击的按钮设置文本值为空
        wordButton.mCurrentButton.setText("");
        wordButton.mCurrentWord="";
        wordButton.mIsVisiable=false;
        wordButton.mCurrentButton.setEnabled(false);
    }
    //初始化游戏数据
    private void initGameData(){
        //初始化已选择框
        mSelectedWords=initSelectedWords();
        LayoutParams params=new LayoutParams(60,60);
        //移除原先存在的View
        mSelectedLl.removeAllViews();
        for(int i=0;i<mSelectedWords.size();i++){
            mSelectedLl.addView(mSelectedWords
                    .get(i).mCurrentButton,params);
        }
        //获取数据，更新数据
        mAllWords=initAllWords();
        mGridView.update(mAllWords);
        //初始化就要播放歌曲
        hanlerPlay();
    }
    //初始化文字框数据
    private ArrayList<WordButton> initAllWords(){
        char words[]=getAllWords();
        String[] strWords=new String[MyGridView.WORD_COUNTS];
        for(int i=0;i<words.length;i++){
            strWords[i]=String.valueOf(words[i]);
        }
        ArrayList<WordButton> data=new ArrayList<WordButton>();
        for(int i=0;i< MyGridView.WORD_COUNTS;i++){
            WordButton wB=new WordButton();
            wB.mCurrentWord=strWords[i];
            data.add(wB);
        }
        return data;
    }
    //初始化已选框数据
    private ArrayList<WordButton> initSelectedWords(){
         //初始化当前歌曲
        mCurrentSong=getCurrentSong(++mSongIndex);
        ArrayList<WordButton> data=new ArrayList<WordButton>();
        for(int i=0;i<mCurrentSong.getSongNameSize();i++){
            View view= Util.getView(MainActivity.this,R.layout.word_button);
            final WordButton wB=new WordButton();
            wB.mCurrentButton=(Button)view.findViewById(R.id.item_btn);
            wB.mCurrentButton.setTextColor(Color.WHITE);
            wB.mCurrentButton.setText("");
            wB.mIsVisiable=false;
            wB.mCurrentButton.setBackgroundResource(R.mipmap.game_wordblank);
            wB.mCurrentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearSelected(wB);
                }
            });
            data.add(wB);
        }
        return data;
    }
    //创建当前歌曲对象
    private Song getCurrentSong(int songIndex){
        //判断选中哪一个歌曲
        Song song=new Song();
        String mCurrentSongInfo[]=Const.SONG_RES[songIndex];
        song.setFileName(mCurrentSongInfo[Const.INDEX_FILENAME]);
        song.setSongName(mCurrentSongInfo[Const.INDEX_SONGNAME]);
        return song;
    }
    //生成初始化的24个汉字
    private char[] getAllWords(){
        Random random=new Random();
        char allWords[]=new char[MyGridView.WORD_COUNTS];
        char songNameCharArray[]=mCurrentSong.getSongNameCharArray(mCurrentSong.getSongName());
        //生成歌曲名称
        for(int i=0;i<mCurrentSong.getSongNameSize();i++){
            allWords[i]=songNameCharArray[i];
        }
        //生成混淆的其他汉字
        for(int i=songNameCharArray.length;
            i<MyGridView.WORD_COUNTS;i++){
           allWords[i]=Util.genChineseWord();
        }
        //打乱24个汉字数组的分布
        for (int i = MyGridView.WORD_COUNTS - 1; i >= 0; i--) {
            int index = random.nextInt(i + 1);
            char buf = allWords[index];
            allWords[index] = allWords[i];
            allWords[i] = buf;
        }
        return allWords;
    }
    //删除一个单词
    private void handleDeleteButton() {
        mViewDeleteWord=(ImageButton)findViewById(R.id.btn_delete_word);
        mViewDeleteWord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               showDialogBaseId(ID_DELETE_WORD);
            }
        });
    }
    //提示一个单词
    private void handleTipButton(){
        mViewTipAnswer=(ImageButton)findViewById(R.id.btn_tip_word);
        mViewTipAnswer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDialogBaseId(ID_TIP_ANSWER);
            }
        });

    }
    //判断金币是否可删除
    private boolean isDeleteCoin(int count){
        boolean flag;
        if (0 > (mCurrentCoins -count )) {
            flag=false;
            return flag;
        } else {
            mCurrentCoins = mCurrentCoins - count;
            flag=true;
        }
        return flag;
    }
    //删除一个单词
    private void deleteWord(){
        boolean flag=isDeleteCoin(30);
        if(flag) {
            mViewCoins.setText(mCurrentCoins+"");
            WordButton wB=findDeleteWord();
            wB.mIsVisiable=false;
            wB.mCurrentButton.setVisibility(View.INVISIBLE);
        }else{
            //金币余额不足时候提示
            showDialogBaseId(ID_COIN_LACK);
        }
    }
    //判断是否可增加一个提示
    private void addTipAnswer() {
        boolean mIsVoidSelect=isVoidSelect();
        ALL:
        if(mIsVoidSelect) {
            for (int i = 0; i < mSelectedWords.size(); i++) {
                if (mSelectedWords.get(i).mCurrentWord.length() == 0) {
                    //判断金币数量是否足够
                    boolean flag = isDeleteCoin(90);
                    //金币余额不足时候提示
                    if(!flag){
                        showDialogBaseId(ID_COIN_LACK);
                        break ALL;
                    }
                    if (flag) {
                        WordButton wB = findAnswerWord(i);
                        //原文字框消失不可见
                        wB.mIsVisiable = false;
                        wB.mCurrentButton.setVisibility(View.INVISIBLE);
                        //显示待选框数据
                        mSelectedWords.get(i).mCurrentButton.setText(wB.mCurrentWord);
                        mSelectedWords.get(i).mIndex = wB.mIndex;
                        mSelectedWords.get(i).mIsVisiable = true;
                        mSelectedWords.get(i).mCurrentWord = wB.mCurrentWord;
                        //减少金币
                        mViewCoins.setText(mCurrentCoins+"");
                        //如果待选框数据已满，判断答案是否正确
                        if(!isVoidSelect()){
                           onTipAnswerComplete();
                        }
                        break ALL;
                    }
                }
            }
        }
    }
    //判断当前选择框是否有空位
    private boolean isVoidSelect(){
        boolean flag=false;
        for(int i=0;i<mSelectedWords.size();i++){
            if(mSelectedWords.get(i).mCurrentWord.length()==0){
                flag=true;
            }
        }
        return flag;
    }
    //找出一个要删除的单词
    private WordButton findDeleteWord(){
        Random random = new Random();
        while(true){
           int randomNum = random.nextInt(MyGridView.WORD_COUNTS);
           if(mAllWords.get(randomNum)
                   .mIsVisiable&&(findIsAnswerWord(randomNum)==null)){
               return mAllWords.get(randomNum);
           }
        }
    }
    //判断当前的词是否是答案
    private WordButton findIsAnswerWord(int index){
        WordButton buf;
        for(int i=0;i<mCurrentSong.getSongName().length();i++) {
            if (mAllWords.get(index).mCurrentWord
                    .equals(mCurrentSong.getSongNameCharArray(mCurrentSong.getSongName())[i] + "")) {
                buf = mAllWords.get(i);
                return buf;
            }
        }
        return null;
    }
    //根据待选框的位置在AllWords中找到一个答案
    private WordButton findAnswerWord(int index){
        String mCurrentWord=mCurrentSong.getSongNameCharArray(mCurrentSong.getSongName())[index]+"";
        WordButton mFindWord=null;
        for(int i=0;i<MyGridView.WORD_COUNTS;i++){
            if(mAllWords.get(i).mIsVisiable&&mAllWords
                    .get(i).mCurrentWord.equals(mCurrentWord)){
                mFindWord=mAllWords.get(i);
            }
        }
        return mFindWord;
    }
    //提示单词填充待选框时候，判断正确与否
    private void onTipAnswerComplete(){
        int status=checkAnswer();
        if(status==ANSWER_RIGHT){
            //显示过关逻辑
            //过关显示过关界面可见
            mPassGame=(View)findViewById(R.id.pass_view);
            mPassGame.setVisibility(View.VISIBLE);
        }else if(status==ANSWER_WRONG){
            //错误答案闪烁
            sparkWrongWords();
        }
    }
    //删除单词对话框
    private IAlertDialogButtonListener mOnClickDeleteWordButton=new IAlertDialogButtonListener() {
        @Override
        public void onclick() {
        //确定删除单词
        deleteWord();
        }
    };
    //提示答案对话框
    private IAlertDialogButtonListener mOnClickTipAnswerButton=new IAlertDialogButtonListener() {
        @Override
        public void onclick() {
        //确定增加提示
        addTipAnswer();
        }
    };
    //金币不足对话框
    private IAlertDialogButtonListener mCoinsIsLack=new IAlertDialogButtonListener() {
        @Override
        public void onclick() {
            //
        }
    };
    //根据id显示对话框
    private void showDialogBaseId(int id){
        switch (id){
            case ID_DELETE_WORD:
            //点击删除按钮
            Util.showDialog(MainActivity.this,
                    "确定花掉30个金币去掉一个错误答案？",mOnClickDeleteWordButton);
            break;
            case ID_TIP_ANSWER:
            //点击提示按钮
            Util.showDialog(MainActivity.this,
                        "确定花掉90个金币获得一个文字提示？",mOnClickTipAnswerButton);
            break;
            case ID_COIN_LACK:
            //金币不足时
            Util.showDialog(MainActivity.this,
                        "金币不足，去商店购买？",mCoinsIsLack);
            break;
        }

    }
}
