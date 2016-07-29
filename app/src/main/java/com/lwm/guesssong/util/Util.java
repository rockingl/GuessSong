package com.lwm.guesssong.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lwm.guesssong.R;
import com.lwm.guesssong.data.Const;
import com.lwm.guesssong.oberserIf.IAlertDialogButtonListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * @author：Lwm on 2016/7/23 11:09
 * @updateDate：2016/7/23
 * @version：1.0
 * @email：846988094@qq.com
 */
public class Util {

    public static AlertDialog mAlertDialog;

    //实例化一个布局文件
    public static View getView(Context context, int layoutId) {
        //加载
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //创建资源对象
        View view = inflater.inflate(layoutId, null);
        return view;

    }

    //随机生成一个汉字
    public static char genChineseWord() {
        String str = null;
        Random random = new Random();
        int highPos, lowPos;
        highPos = 176 + Math.abs(random.nextInt(39));
        lowPos = 161 + Math.abs(random.nextInt(93));
        byte[] b = new byte[2];
        b[0] = (new Integer(highPos)).byteValue();
        b[1] = (new Integer(lowPos)).byteValue();
        try {
            str = new String(b, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.charAt(0);
    }

    //激活一个activity
    public static void aliveActivity(Context context, Class targetClass) {
        Intent intent = new Intent();
        intent.setClass(context, targetClass);
        context.startActivity(intent);
        //关闭当前的activity
        ((Activity) context).finish();
    }

    //显示自定义对话框
    public static void showDialog(final Context context,
                                  String message, final IAlertDialogButtonListener listener) {
        View dialogView = null;
        //得到对话框的builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_Transparent);
        //得到自定义的View
        dialogView = getView(context, R.layout.dialog_view);
        //获取信息组件
        TextView txtMessageView = (TextView) dialogView.findViewById(R.id.text_dialog_message);
        txtMessageView.setText(message);
        //设定ok按钮点击事件
        ImageButton btn_ok = (ImageButton) dialogView.findViewById(R.id.btn_dialog_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭对话框
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }
                //回调事件
                if (listener != null) {
                    listener.onclick();
                }
                //显示音效（进入）
                MyPlayer.playTone(context, MyPlayer.INDEX_ENTER_TONE);
            }
        });
        //设定cancel点击事件
        ImageButton btn_cancel = (ImageButton) dialogView.findViewById(R.id.btn_dialog_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭对话框
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }
                //显示音效（删除）
                MyPlayer.playTone(context, MyPlayer.INDEX_CANCEL_TONE);
            }
        });
        //为AlertDialog设置View
        builder.setView(dialogView);
        mAlertDialog = builder.create();
        //显示对话框
        mAlertDialog.show();
    }

    //实现保存数据（当前关数和金币shu）
    //格式化代码键。ctrl+shift+F
    public static void saveData(Context context, int mCurrentPassIndex, int mCurrentCoins) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(Const.SAVE_DATA_FILE_NAME, Context.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeInt(mCurrentPassIndex);
            dos.writeInt(mCurrentCoins);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭输出流
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //显示读取游戏数据
    public static int[] readData(Context context){
        //存读取的数组(初始的和关数金币数)
        int[] data={-1,Const.INIT_COINS};
        FileInputStream fis=null;
        try {
            fis=context.openFileInput(Const.SAVE_DATA_FILE_NAME);
            DataInputStream dis=new DataInputStream(fis);
            data[Const.INDEX_CURRENT_PASS]=dis.readInt();
            data[Const.INDEX_CURRENT_COINS]=dis.readInt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //返回数据
        return data;
    }
}
