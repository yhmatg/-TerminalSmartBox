package com.android.terminalbox.mqtt;

public class TagService {
    private String service_id;
    private TagProp properties;
    private String event_time;
    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public TagProp getProperties() {
        return properties;
    }

    public void setProperties(TagProp properties) {
        this.properties = properties;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }
}
