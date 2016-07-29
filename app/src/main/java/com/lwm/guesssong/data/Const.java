package com.lwm.guesssong.data;

/**
 * @author：lwm on 2016/7/25 10:42
 * @updateDate：2016/7/25
 * @version：1.0
 * @email：846988094@qq.com
 */
public class Const {
    //初始化金币
    public static final int INIT_COINS=100;
    public static final int INDEX_FILENAME=0;
    public static final int INDEX_SONGNAME=1;
    public static final String SONG_RES[][]={
            //歌曲文件名，歌曲名称
            {"__00000.m4a", "征服"},
            {"__00001.m4a", "童话"},
            {"__00002.m4a", "同桌的你"},
            {"__00003.m4a", "七里香"},
            {"__00004.m4a", "传奇"},
            {"__00005.m4a", "大海"},
            {"__00006.m4a", "后来"},
            {"__00007.m4a", "你的背包"},
            {"__00008.m4a", "再见"},
            {"__00009.m4a", "老男孩"},
            {"__00010.m4a", "龙的传人"},
    };
    //定义文件名字
    public static final String SAVE_DATA_FILE_NAME="gamedata.bat";
    //定义当前的关数索引
    public static final int INDEX_CURRENT_PASS=0;
    //定义当前的金币数
    public static final int INDEX_CURRENT_COINS=1;

}
