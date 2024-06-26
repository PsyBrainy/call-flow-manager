package com.psybrainy.CallFlowManager.call.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psybrainy.CallFlowManager.call.application.port.in.Dispatcher;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.share.AbstractLogger;
import com.psybrainy.CallFlowManager.share.exception.KafkaMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
public class DispatchCallKafkaAdapter extends AbstractLogger {

    private final Dispatcher dispatcher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public DispatchCallKafkaAdapter(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @KafkaListener(topics = "call-topic", groupId = "call")
    public void listen(String message) {
        try {
            log.info("Message received: {}", message);
            Call call = objectMapper.readValue(message, Call.class);
            log.info("Call received: {}", call);
            dispatcher.dispatchCall(call);
        } catch (IOException e) {
            log.error("Error parsing message: {}", message, e);
            throw new KafkaMessageException("Error parsing message", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred: ", e);
            throw new KafkaMessageException("Unexpected error occurred", e);
        }
    }
}
