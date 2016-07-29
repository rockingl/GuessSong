package com.lwm.guesssong.model;

/**
 * @author：lwm on 2016/7/25 10:49
 * @updateDate：2016/7/25
 * @version：1.0
 * @email：846988094@qq.com
 */
public class Song {
    //定义歌曲名称，对应的文件的名称，歌曲名字的尺寸
    private String mSongName;
    private String mFileName;
    private int mSongNameSize;
    //拆分为字符数组形式
    public char[] getSongNameCharArray(String songName){
        return songName.toCharArray();
    }
    public String getFileName() {
        return mFileName;
    }
    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public int getSongNameSize() {
        return mSongNameSize;
    }
    public String getSongName() {
        return mSongName;
    }
    //根据歌曲名称可以得到歌曲的尺寸
    public void setSongName(String songName) {
        mSongName = songName;
        mSongNameSize=songName.length();
    }
}
