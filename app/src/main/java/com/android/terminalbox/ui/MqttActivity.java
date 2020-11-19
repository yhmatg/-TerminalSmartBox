package com.android.terminalbox.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.terminalbox.R;
import com.android.terminalbox.core.bean.box.IotDevice;
import com.android.terminalbox.core.bean.user.OrderBody;
import com.android.terminalbox.mqtt.MqttServer;
import com.android.terminalbox.mqtt.RylaiMqttCallback;
import com.android.terminalbox.mqtt.own.Props;
import com.android.terminalbox.mqtt.own.ResultProp;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.UUID;

public class MqttActivity extends AppCompatActivity {
    String TAG = "MqttActivity";
    private IotDevice iotDevice;
    private String relevanceId  = "11111111111";
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
            Log.e(TAG, "topic===" + topic);
            Log.e(TAG, "message===" + message);
            if ("app/devices/15aa68f3183311ebb7260242ac120004_uniqueCode002/insts".equals(topic)) {
                OrderBody orderBody = new Gson().fromJson(new String(message.getPayload()), OrderBody.class);
                relevanceId = orderBody.getInstData().getRelevanceId();
            }
            contentTv.setText(new String(message.getPayload()));
        }

    };
    private TextView contentTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);
        Button openPublish = findViewById(R.id.open_pulish);
        openPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testOpenReport();
            }
        });
        Button closePublish = findViewById(R.id.close_pulish);
        closePublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testCloseReport();
            }
        });
        Button invPublish = findViewById(R.id.inv_pulish);
        invPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testInvReport();
            }
        });
        contentTv = findViewById(R.id.arrived_content);
        MqttConnect mqttConnect = new MqttConnect();
        mqttConnect.start();
    }

    class MqttConnect extends Thread {
        @Override
        public void run() {
            super.run();
            if (iotDevice == null) {
                iotDevice = new IotDevice();
                iotDevice.setIotHost("117.34.118.157");
                iotDevice.setMqttPort("20008");
                iotDevice.setPordId("15aa68f3183311ebb7260242ac120004");
                iotDevice.setDevVerify("uniqueCode002");
                iotDevice.setDevId(iotDevice.getPordId()+"_"+iotDevice.getDevVerify());
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

    public void testOpenReport() {
        Props props = new Props();
        ArrayList<ResultProp> resultProps = new ArrayList<>();
        ResultProp resultProp = new ResultProp();
        resultProp.setCap_id("id1");
        relevanceId = "open"+ UUID.randomUUID().toString() ;
        resultProp.setRelevance_id(relevanceId);
        resultProp.setData_event_time(System.currentTimeMillis());
        ResultProp.Prop prop = new ResultProp.Prop();
        prop.setOpenEkey(true);
        prop.setOpenType("remote");
        resultProp.setProp(prop);
        resultProps.add(resultProp);
        props.setProps(resultProps);
        MqttServer.getInstance().reportProperties(new Gson().toJson(props));
    }

    public void testCloseReport() {
        Props props = new Props();
        ArrayList<ResultProp> resultProps = new ArrayList<>();
        ResultProp resultProp = new ResultProp();
        resultProp.setCap_id("id2");
        //relevanceId = "close" + UUID.randomUUID().toString() ;
        resultProp.setRelevance_id(relevanceId);
        resultProp.setData_event_time(System.currentTimeMillis());
        ResultProp.Prop prop = new ResultProp.Prop();
        prop.setCloseEkey(true);
        resultProp.setProp(prop);
        resultProps.add(resultProp);
        props.setProps(resultProps);
        MqttServer.getInstance().reportProperties(new Gson().toJson(props));
    }

    public void testInvReport() {
        Props props = new Props();
        ArrayList<ResultProp> resultProps = new ArrayList<>();
        ResultProp resultProp = new ResultProp();
        resultProp.setCap_id("id3");
        //relevanceId = "inv" + UUID.randomUUID().toString() ;
        resultProp.setRelevance_id(relevanceId);
        resultProp.setData_event_time(System.currentTimeMillis());
        ResultProp.Prop prop = new ResultProp.Prop();
        ArrayList<String> inList = new ArrayList<>();
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        inList.add("epc000000000000000000001");
        ArrayList<String> outList = new ArrayList<>();
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        outList.add("epc000000000000000000002");
        prop.setRfid_in(inList);
        prop.setRfid_out(outList);
        resultProp.setProp(prop);
        resultProps.add(resultProp);
        props.setProps(resultProps);
        MqttServer.getInstance().reportProperties(new Gson().toJson(props));
    }

}
