package com.android.terminalbox.core.bean.user;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Objects;

@Entity
public class EpcFile {
    private String name;
    @PrimaryKey
    @NonNull
    private String epcCode;

    public EpcFile(String name, String epcCode) {
        this.name = name;
        this.epcCode = epcCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEpcCode() {
        return epcCode;
    }

    public void setEpcCode(String epcCode) {
        this.epcCode = epcCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EpcFile)) return false;
        EpcFile epcFile = (EpcFile) o;
        return getEpcCode().equals(epcFile.getEpcCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEpcCode());
    }
}
