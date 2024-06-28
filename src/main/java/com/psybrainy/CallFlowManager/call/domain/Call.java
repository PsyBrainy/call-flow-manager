package com.psybrainy.CallFlowManager.call.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.SecureRandom;

public class Call {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final int duration;

    public Call() {
        this.duration = 5 + RANDOM.nextInt(6);
    }

    @JsonCreator
    public Call(@JsonProperty("duration") int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Call{" +
                "duration=" + duration +
                '}';
    }
}
