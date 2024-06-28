package com.psybrainy.CallFlowManager.call.adapter.out.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psybrainy.CallFlowManager.call.application.port.out.AddCallToQueue;
import com.psybrainy.CallFlowManager.call.domain.Call;
import com.psybrainy.CallFlowManager.share.AbstractLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AddCallToQueueKafkaAdapter
        extends AbstractLogger
        implements AddCallToQueue {

    private final KafkaTemplate<String, String> kafkaProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AddCallToQueueKafkaAdapter(KafkaTemplate<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void send(Call call)  {
        try {

            log.info("Retry call queue: {}", call);
            Thread.sleep(1000);

            String message = objectMapper.writeValueAsString(call);
            kafkaProducer.send("call-topic", message);
        } catch (JsonProcessingException e) {
            log.error("Error while sending call", e);
            return;
        } catch (InterruptedException e) {
            return;
        }
    }
}
