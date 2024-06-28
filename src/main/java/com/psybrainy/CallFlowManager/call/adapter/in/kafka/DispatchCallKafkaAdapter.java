package com.psybrainy.CallFlowManager.call.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psybrainy.CallFlowManager.call.application.port.in.Dispatcher;
import com.psybrainy.CallFlowManager.call.domain.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class DispatchCallKafkaAdapter {

    private final Dispatcher dispatcher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public DispatchCallKafkaAdapter(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @KafkaListener(topics = "call-topic", groupId = "call")
    public void listen(String message) {
        try {
            System.out.println("Message received: " + message);
            Call call = objectMapper.readValue(message, Call.class);
            System.out.println("Call received: " + call);
            dispatcher.dispatchCall(call);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
