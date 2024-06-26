package com.psybrainy.CallFlowManager.call.application.port.in;

import com.psybrainy.CallFlowManager.call.domain.Call;

public interface Dispatcher<T> {
    T dispatchCall(Call call);
}
