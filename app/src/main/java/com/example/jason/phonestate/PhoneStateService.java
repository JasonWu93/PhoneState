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

public class PhoneStateService extends Service {

    private static final String TAG = "PhoneStateService";
    //    protected static final String ACTION = "com.android.broadcast.RECEIVER_ACTION";
    private TelephonyManager telephonyManager;
    private MyPhoneStateListener phoneStateListener;
    private OutCallReceiver outCallReceiver;

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
                    Log.i(TAG,"State IDLE 挂断");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG,"State RINGING 来电 ：" + phoneNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG,"State OFFHOOK 接听");
                    break;
            }
        }
    }
}
