package com.android.terminalbox.mqtt;

import java.util.List;

public class TagMessage {
    public List<TagService> services;

    public TagMessage() {
    }

    public TagMessage(List<TagService> services) {
        this.services = services;
    }

    public List<TagService> getServices() {
        return services;
    }

    public void setServices(List<TagService> services) {
        this.services = services;
    }
}
