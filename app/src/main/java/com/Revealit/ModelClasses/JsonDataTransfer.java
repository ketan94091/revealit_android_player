package com.Revealit.ModelClasses;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

public class JsonDataTransfer implements Serializable {

    @SerializedName("voter")
    @NotNull
    private String voter;

    @SerializedName("proxy")
    @NotNull
    private String proxy;

    @SerializedName("producers")
    @NotNull
    private List<String> producers;

    public JsonDataTransfer(@NotNull String voter, @NotNull String proxy ,@NotNull List<String> mListProducer) {
        this.voter = voter;
        this.proxy = proxy;
        this.producers =mListProducer;
    }

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

    public List<String> getProducers() {
        return producers;
    }

    public void setProducers(List<String> producers) {
        this.producers = producers;
    }
}



