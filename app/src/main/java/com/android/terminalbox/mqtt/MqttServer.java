package com.android.terminalbox.mqtt;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.terminalbox.core.bean.box.IotDevice;
import com.android.terminalbox.utils.box.ConnectUtils;
import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.SecureRandom;

public class MqttServer {
    private static final String TAG="MqttServer";
    private static MqttServer mqttServer = null;
    private RylaiMqttCallback mRylaiMqttCallback=null;
    private MqttAndroidClient mqttAndroidClient;
//    private String iotHost,prodId,devVerifyCode,devId,devPassword;

    private IotDevice mIotDevice=null;
    int qos = 2;
    private long minBackoff = 1000;
    private long maxBackoff = 30 * 1000;
    private long defaultBackoff = 1000;
    private static int retryTimes = 0;
    private SecureRandom random = new SecureRandom();
    private Gson gson = new Gson();

    public static MqttServer getInstance() {
        if (mqttServer == null) {
            synchronized (MqttServer.class) {
                if (mqttServer == null) {
                    mqttServer = new MqttServer();
                }
            }
        }
        return mqttServer;
    }
    /**
     * 初始化
     *
     * @param context 上下文对象
     * @return 是否初始化成功
     */
    public boolean init(Context context, IotDevice iotDevice,RylaiMqttCallback rylaiMqttCallback) {

        synchronized (this) {
            mIotDevice=iotDevice;
            this.mRylaiMqttCallback=rylaiMqttCallback;
            String serverUrl = "tcp://" + iotDevice.getIotHost()+ ":"+iotDevice.getMqttPort();
            String currentDate = ConnectUtils.getCurrentDate();
            String clientId = mIotDevice.getDevId()+ "_0_0_" + currentDate;
            MqttConnectOptions mqttConnectOptions = intitMqttConnectOptions(currentDate);
            mqttAndroidClient = new MqttAndroidClient(context, serverUrl, clientId);
            mqttAndroidClient.setCallback(new MqttCallBack4IoTHub());
            try {
                mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        exception.printStackTrace();
                        //退避重连
                        int lowBound = (int) (defaultBackoff * 0.8);
                        int highBound = (int) (defaultBackoff * 1.2);
                        long randomBackOff = random.nextInt(highBound - lowBound);
                        long backOffWithJitter = (int) (Math.pow(2.0, (double) retryTimes)) * (randomBackOff + lowBound);
                        long waitTImeUntilNextRetry = (int) (minBackoff + backOffWithJitter) > maxBackoff ? maxBackoff : (minBackoff + backOffWithJitter);
                        try {
                            Thread.sleep(waitTImeUntilNextRetry);
                        } catch (InterruptedException e) {
                            System.out.println("sleep failed, the reason is" + e.getMessage().toString());
                        }
                        retryTimes++;
                        init(context,iotDevice,mRylaiMqttCallback);
                    }
                });
                return true;
            } catch (MqttException e) {
            }
            return false;
        }
    }

    private MqttConnectOptions intitMqttConnectOptions(String currentDate) {
        String password = ConnectUtils.sha256_HMAC(mIotDevice.getDevPassword(), currentDate);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setKeepAliveInterval(120);
        mqttConnectOptions.setConnectionTimeout(30);
        mqttConnectOptions.setUserName(mIotDevice.getDevId());
        mqttConnectOptions.setPassword(password.toCharArray());
        return mqttConnectOptions;
    }

    /**
     * Mqtt 消息回调函数
     */
    private final class MqttCallBack4IoTHub implements MqttCallbackExtended {

        @Override
        public void connectionLost(Throwable cause) {
            mRylaiMqttCallback.connectionLost(cause);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            mRylaiMqttCallback.messageArrived(topic,message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            try {
                mRylaiMqttCallback.deliveryComplete(token);
            }catch (Exception ex){

            }
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            try {
                //mqttAndroidClient.subscribe("app/devices/15aa68f3183311ebb7260242ac120004_uniqueCode002/insts", 0);
                mqttAndroidClient.subscribe("app/userface/update", 0);
            } catch (MqttException e) {
                e.printStackTrace();
            }
            mRylaiMqttCallback.connectComplete(reconnect,serverURI);
        }
    }
    /**
     * 发布属性上报topic
     *
     * @return
     */
    private String getPublishTopic() {
//        String mqtt_report_topic_json = String.format("$oc/devices/%s/sys/properties/report", editText_mqtt_device_connect_deviceId.getText().toString());
       /* String mqtt_report_topic_json = String.format(mIotDevice.getTopicPropUpload(),mIotDevice.getDevId());
        return mqtt_report_topic_json;*/
        return  "app/devices/15aa68f3183311ebb7260242ac120004_uniqueCode002/properties/report";
    }

    /**
     * 属性上报
     */
    public void reportProperties(TagMessage tagMessage) {
        String message = gson.toJson(tagMessage);
        reportProperties(message);
    }
    /**
     * 属性上报
     */
    public void reportProperties(String message) {
        Log.d(TAG, "reportProperties: "+message);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        try {
            mqttAndroidClient.publish(getPublishTopic(),message.getBytes(),qos,false,null,new IMqttActionListener(){
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mRylaiMqttCallback.onSuccess(asyncActionToken);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mRylaiMqttCallback.onFailure(asyncActionToken,exception);
                }
            } );
        } catch (MqttException e) {
        }
    }
}
