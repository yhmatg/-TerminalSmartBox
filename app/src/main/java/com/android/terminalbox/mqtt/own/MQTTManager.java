package com.android.terminalbox.mqtt.own;

import android.content.Context;
import android.util.Log;
import com.android.terminalbox.core.bean.box.IotDevice;
import com.android.terminalbox.utils.box.ConnectUtils;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MQTTManager {
    
    String TAG = "MQTTManager";
    private Context mContext;
    private MqttAndroidClient mqttAndroidClient;
    private String clientId;//自定义

    private MqttConnectOptions mqttConnectOptions;

    private ScheduledExecutorService reconnectPool;//重连线程池
    private IotDevice mIotDevice;

    public MQTTManager(Context mContext) {
        this.mContext = mContext;
    }

    public void buildClient() {
        mIotDevice = new IotDevice();
        mIotDevice.setIotHost("172.16.61.101");
        mIotDevice.setMqttPort("1883");
        mIotDevice.setDevId("smartbox");
        mIotDevice.setDevPassword("smartbox");
        closeMQTT();//先关闭上一个连接
        buildMQTTClient();
    }

    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG,"connect-"+"onSuccess");
            closeReconnectTask();
            subscribeToTopic();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            //connect-onFailure-MqttException (0) - java.net.UnknownHostException
            Log.i(TAG,"connect-"+ "onFailure-"+exception);
            startReconnectTask();
        }
    };

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            //close-connectionLost-等待来自服务器的响应时超时 (32000)
            //close-connectionLost-已断开连接 (32109)
            Log.i(TAG,"close-"+"connectionLost-"+cause);
            if (cause != null) {//null表示被关闭
                startReconnectTask();
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String body = new String(message.getPayload());
            Log.i(TAG,"messageArrived-"+message.getId()+"-"+body);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            try {
                Log.i(TAG,"deliveryComplete-"+token.getMessage().toString());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };

    private void buildMQTTClient(){
        //add 1102 start
        String serverUrl = "tcp://" + mIotDevice.getIotHost()+ ":"+mIotDevice.getMqttPort();
        String currentDate = ConnectUtils.getCurrentDate();
        String clientId = mIotDevice.getDevId()+ "_0_0_" + currentDate;
        String password = ConnectUtils.sha256_HMAC(mIotDevice.getDevPassword(), currentDate);
        //add 1102 end
        mqttAndroidClient = new MqttAndroidClient(mContext, serverUrl, clientId);
        mqttAndroidClient.setCallback(mqttCallback);

        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setConnectionTimeout(10);
        mqttConnectOptions.setKeepAliveInterval(20);
        mqttConnectOptions.setCleanSession(true);
        try {
            mqttConnectOptions.setUserName(mIotDevice.getDevId());
            mqttConnectOptions.setPassword(password.toCharArray());
        } catch (Exception e) {
        }
        doClientConnection();
    }

    private synchronized void startReconnectTask(){
        if (reconnectPool != null)return;
        reconnectPool = Executors.newScheduledThreadPool(1);
        reconnectPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                doClientConnection();
            }
        } , 0 , 5*1000 , TimeUnit.MILLISECONDS);
    }

    private synchronized void closeReconnectTask(){
        if (reconnectPool != null) {
            reconnectPool.shutdownNow();
            reconnectPool = null;
        }
    }

    /**
     * 连接MQTT服务器
     */
    private synchronized void doClientConnection() {
        if (!mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.connect(mqttConnectOptions, null, iMqttActionListener);
                Log.i(TAG,"mqttAndroidClient-connecting-"+mqttAndroidClient.getClientId());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void subscribeToTopic() {//订阅之前会取消订阅，避免重连导致重复订阅
        try {
           /* String registerTopic = "";//自定义
            String controlTopic = "";//自定义
            String[] topicFilter=new String[]{registerTopic , controlTopic };
            int[] qos={0,0};*/
            String registerTopic = "app/devices/15aa68f3183311ebb7260242ac120004_uniqueCode002/insts";//自定义
            String[] topicFilter=new String[]{registerTopic };
            int[] qos={0};
            mqttAndroidClient.unsubscribe(topicFilter, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG,"unsubscribe-"+"success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG,"unsubscribe-"+"failed-"+exception);
                }
            });
            mqttAndroidClient.subscribe(topicFilter, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {//订阅成功
                    Log.i(TAG,"subscribe-"+"success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    startReconnectTask();
                    Log.i(TAG,"subscribe-"+"failed-"+exception);
                }
            });

        } catch (MqttException ex) {
        }
    }

    public void sendMQTT(String topicSep, String msg) {
        try {
            if (mqttAndroidClient == null)return;
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes());
            String topic = "";//自定义
            mqttAndroidClient.publish(topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
//                    Log.i(TAG,"sendMQTT-"+"success:" + msg);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    startReconnectTask();
                    Log.i(TAG,"sendMQTT-"+"failed:" + msg);
                }
            });
        } catch (MqttException e) {
        }
    }

    public void closeMQTT(){
        closeReconnectTask();
        if (mqttAndroidClient != null){
            try {
                mqttAndroidClient.unregisterResources();
                mqttAndroidClient.disconnect();
                Log.i(TAG,"closeMQTT-"+mqttAndroidClient.getClientId());
                mqttAndroidClient = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

}