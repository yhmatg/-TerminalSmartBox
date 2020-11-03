package com.android.terminalbox.core.bean.box;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class Oprecord {
    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "userName")
    private String userName;

    @ColumnInfo(name = "op_type")
    private String opType;

    @ColumnInfo(name="op_date")
    private Date op_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public Date getOp_date() {
        return op_date;
    }

    public void setOp_date(Date op_date) {
        this.op_date = op_date;
    }

    @Override
    public String toString() {
        return "Oprecord{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", opType='" + opType + '\'' +
                ", op_date=" + op_date +
                '}';
    }
}
