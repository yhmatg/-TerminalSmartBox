package com.android.terminalbox.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.terminalbox.R;
import com.android.terminalbox.core.bean.box.IotDevice;
import com.android.terminalbox.core.bean.box.Oprecord;
import com.android.terminalbox.core.dao.OprecordDao;
import com.android.terminalbox.mqtt.MqttServer;
import com.android.terminalbox.mqtt.RylaiMqttCallback;
import com.android.terminalbox.mqtt.TagMessage;
import com.android.terminalbox.mqtt.own.Props;
import com.android.terminalbox.mqtt.own.ResultProp;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttConnect;

import java.util.ArrayList;
import java.util.List;

public class MqttActivity extends AppCompatActivity {
    String TAG = "MqttActivity";
    private IotDevice iotDevice;
    private RylaiMqttCallback rylaiMqttCallback= new RylaiMqttCallback() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.e(TAG, "Mqtt Message onSuccess");
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.e(TAG, "Mqtt Message onFailure");
            Log.d(TAG, "Mqtt Message Action==============失败");
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            Log.e(TAG, "Mqtt Message connectComplete");
        }

        @Override
        public void connectionLost(Throwable cause) {
            Log.e(TAG, "Mqtt Message connectionLost");
        }


        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.e(TAG, "Mqtt Message deliveryComplete");
            Log.d(TAG, "Mqtt Message==============消息上传完成");

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.e(TAG, "Mqtt Message messageArrived");
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);
        Button publishBt = findViewById(R.id.bt_pulish);
        publishBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testReport();
            }
        });
        MqttConnect mqttConnect = new MqttConnect();
        mqttConnect.start();
    }

    public void testReport() {
        Props props = new Props();
        ArrayList<ResultProp> resultProps = new ArrayList<>();
        ResultProp resultProp = new ResultProp();
        resultProp.setCap_id("id1");
        resultProp.setRelevance_id("自定义Id推荐uuid0001");
        resultProp.setData_event_time(System.currentTimeMillis());
        ResultProp.Prop prop = new ResultProp.Prop();
        prop.setOpenEkey(true);
        prop.setOpenType("remote");
        resultProp.setProp(prop);
        resultProps.add(resultProp);
        props.setProps(resultProps);
        MqttServer.getInstance().reportProperties(new Gson().toJson(props));
    }

    class MqttConnect extends Thread {
        @Override
        public void run() {
            super.run();
            if (iotDevice == null) {
                iotDevice = new IotDevice();
                iotDevice.setIotHost("172.16.61.101");
                iotDevice.setMqttPort("1883");
                iotDevice.setDevId("smartbox");
                iotDevice.setDevPassword("smartbox");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MqttServer.getInstance().init(MqttActivity.this, iotDevice, rylaiMqttCallback);
        }
    }

}
