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
    private String AstCode;
    private String boxCode;

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

    public String getAstCode() {
        return AstCode;
    }

    public void setAstCode(String astCode) {
        AstCode = astCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EpcFile)) return false;
        EpcFile epcFile = (EpcFile) o;
        return getEpcCode().equals(epcFile.getEpcCode()) &&
                getAstCode().equals(epcFile.getAstCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEpcCode(), getAstCode());
    }
}
