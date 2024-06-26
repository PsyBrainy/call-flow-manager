package com.psybrainy.CallFlowManager.call.domain;

import java.security.SecureRandom;

public class Call {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final int duration;

    public Call() {
        this.duration = 5 + RANDOM.nextInt(6);
    }

    public int getDuration() {
        return duration;
    }
}
