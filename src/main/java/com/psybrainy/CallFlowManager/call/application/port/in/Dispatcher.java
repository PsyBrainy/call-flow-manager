package com.psybrainy.CallFlowManager.call.application.port.in;

import com.psybrainy.CallFlowManager.call.domain.Call;

import java.util.concurrent.CompletableFuture;

public interface Dispatcher {
    CompletableFuture<String> dispatchCall(Call call);
}
