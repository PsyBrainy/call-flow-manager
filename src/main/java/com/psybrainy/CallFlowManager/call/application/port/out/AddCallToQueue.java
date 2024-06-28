package com.psybrainy.CallFlowManager.call.application.port.out;

import com.psybrainy.CallFlowManager.call.domain.Call;

public interface AddCallToQueue {
    void send(Call call);
}
