package com.android.terminalbox.core.bean.box;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

//@Table(name = "tbiotdevice",onCreated = "INSERT INTO tbiotdevice (iot_host,mqtt_port) values ('iot-mqtts.cn-north-4.myhuaweicloud.com','1883');")
@Entity(indices = {@Index("name"), @Index(value = {"tbiotdevice"})})
public class IotDevice {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "iot_host")
    private String iotHost;
    @ColumnInfo(name = "mqtt_port")
    private String mqttPort;
    @ColumnInfo(name = "prod_id")
    private String pordId;
    @ColumnInfo(name = "dev_verify")
    private String devVerify;

    private String devId;
    @Column(name = "dev_password")
    private String devPassword;
    @Column(name = "topic_prop_upload")
    private String topicPropUpload;

    public IotDevice() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIotHost() {
        return iotHost;
    }

    public void setIotHost(String iotHost) {
        this.iotHost = iotHost;
    }

    public String getPordId() {
        return pordId;
    }

    public void setPordId(String pordId) {
        this.pordId = pordId;
    }

    public String getDevVerify() {
        return devVerify;
    }

    public void setDevVerify(String devVerify) {
        this.devVerify = devVerify;
    }

    public String getDevPassword() {
        return devPassword;
    }

    public void setDevPassword(String devPassword) {
        this.devPassword = devPassword;
    }

    public String getMqttPort() {
        return mqttPort;
    }

    public void setMqttPort(String mqttPort) {
        this.mqttPort = mqttPort;
    }

    public String getTopicPropUpload() {
        return topicPropUpload;
    }

    public void setTopicPropUpload(String topicPropUpload) {
        this.topicPropUpload = topicPropUpload;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }
}