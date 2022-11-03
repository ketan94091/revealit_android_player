package com.Revealit.ModelClasses;

import java.util.List;

public class JsonDataTransfer {

    String voter;
    String proxy;
    List producers;

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public List getProducers() {
        return producers;
    }

    public void setProducers(List producers) {
        this.producers = producers;
    }
}
