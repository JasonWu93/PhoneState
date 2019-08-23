package com.example.jason.phonestate;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

public class PhoneStateService extends Service {

    private static final String TAG = "PhoneStateService";
    private boolean isIDLE = false;
    private TelephonyManager telephonyManager;
    private MyPhoneStateListener phoneStateListener;
    private OutCallReceiver outCallReceiver;

    private PermissionListener mPermissionListener;
    private ViewStateListener mViewStateListener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new MyPhoneStateListener();
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        // 动态注册广播接收器监听去电信息
        outCallReceiver = new OutCallReceiver();
        // 手机拨打电话时会发送：android.intent.action.NEW_OUTGOING_CALL的广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(outCallReceiver, intentFilter);
        Touher();
        Log.i(TAG,"服务打开");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消来电的电话状态监听服务
        if (telephonyManager != null && phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        // 取消去电的广播监听
        if (outCallReceiver != null) {
            unregisterReceiver(outCallReceiver);
        }
    }

    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String outPhone = getResultData();
            Log.i(TAG,"outPhone :" + outPhone);
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (isIDLE) {
                        Log.i(TAG,"State IDLE");
                        mViewStateListener.onHide();
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG,"State RINGING :" + phoneNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    isIDLE = true;
                    Log.i(TAG,"State OFFHOOK");
                    mViewStateListener.onShow();
                    break;
            }
        }
    }

    public void Touher() {
        View view = (View) LayoutInflater.from(getApplicationContext()).inflate(R.layout.floatwindow, null);
        TextView textView = view.findViewById(R.id.ftextview);
        textView.setText("运营商");
        textView.setBackgroundResource(R.drawable.ic_launcher_background);

        FloatWindow
                .with(getApplicationContext())
                .setView(view)
                .setWidth(Screen.width, 0.2f) //设置悬浮控件宽高
                .setHeight(Screen.width, 0.1f)
                .setX(Screen.width, 0.8f)
                .setY(Screen.height, 0.3f)
//                .setMoveType(MoveType.slide,100,-100)
                .setMoveStyle(500, new BounceInterpolator())
//                .setFilter(true, MainActivity.class)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .build();

        mPermissionListener = new PermissionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess");
            }

            @Override
            public void onFail() {
                Log.d(TAG, "onFail");
            }
        };

        mViewStateListener = new ViewStateListener() {
            @Override
            public void onPositionUpdate(int x, int y) {
                Log.d(TAG, "onPositionUpdate: x=" + x + " y=" + y);
            }

            @Override
            public void onShow() {
                FloatWindow.get().show();
                Log.d(TAG, "onShow");
            }

            @Override
            public void onHide() {
                Log.d(TAG, "onHide");
                FloatWindow.get().hide();
            }

            @Override
            public void onDismiss() {
                Log.d(TAG, "onDismiss");
            }

            @Override
            public void onMoveAnimStart() {
                Log.d(TAG, "onMoveAnimStart");
            }

            @Override
            public void onMoveAnimEnd() {
                Log.d(TAG, "onMoveAnimEnd");
            }

            @Override
            public void onBackToDesktop() {
                Log.d(TAG, "onBackToDesktop");
            }
        };

    }


}
