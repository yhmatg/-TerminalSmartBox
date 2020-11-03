package com.android.terminalbox.core.bean.box;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Tag {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "tag_epc")
    private String tagEpc;

    @ColumnInfo(name="op_id")
    private int opId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagEpc() {
        return tagEpc;
    }

    public void setTagEpc(String tagEpc) {
        this.tagEpc = tagEpc;
    }

    public int getOpId() {
        return opId;
    }

    public void setOpId(int opId) {
        this.opId = opId;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", tagEpc='" + tagEpc + '\'' +
                ", opId=" + opId +
                '}';
    }
}
