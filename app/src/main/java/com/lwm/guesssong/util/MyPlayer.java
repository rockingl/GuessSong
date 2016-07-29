package com.lwm.guesssong.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * @author：lwm on 2016/7/28 20:46
 * @updateDate：2016/7/28
 * @version：1.0
 * @email：846988094@qq.com
 */
public class MyPlayer {
    //定义音效索引，便于直观了解
    public static final int INDEX_ENTER_TONE=0;
    public static final int INDEX_COIN_TONE=1;
    public static final int INDEX_CANCEL_TONE=2;
    //定义音效名称数组
    public static final String[] TONE_NAMES={"enter.mp3","coin.mp3","cancel.mp3"};
    //定义音效数组
    public static MediaPlayer[] mToneMediaPlayer=new MediaPlayer[TONE_NAMES.length];

    //播放音效
    public static void playTone(Context context,int index){
        //等于空时创建
        if(mToneMediaPlayer[index]==null){
            mToneMediaPlayer[index]=new MediaPlayer();
            //加载声音
            AssetManager assetManager=context.getAssets();
            try {
                AssetFileDescriptor fileDescriptor=assetManager.openFd(TONE_NAMES[index]);
                mToneMediaPlayer[index].setDataSource(fileDescriptor.getFileDescriptor()
                        , fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                //准备就绪
                mToneMediaPlayer[index].prepare();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        //开始播放
        mToneMediaPlayer[index].start();

    }

    //歌曲播放的控件
    public static MediaPlayer mMusicMediaPlayer;

    //播放歌曲
    public static void playSong(Context context,String fileName){
        //等于空时创建
        if(mMusicMediaPlayer==null){
            mMusicMediaPlayer=new MediaPlayer();
        }
        //强制重置
        mMusicMediaPlayer.reset();
        //加载声音
        AssetManager assetManager=context.getAssets();
        try {
            AssetFileDescriptor fileDescriptor=assetManager.openFd(fileName);
            mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor()
                    ,fileDescriptor.getStartOffset(),fileDescriptor.getLength());
            //准备就绪，开始播放
            mMusicMediaPlayer.prepare();
            mMusicMediaPlayer.start();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    //停止歌曲
    public static void stopSong(Context context){
        if(mMusicMediaPlayer!=null){
            mMusicMediaPlayer.stop();
        }
    }
}
