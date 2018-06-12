package org.fh.gae.query.index.memory.loader.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.fh.gae.query.index.memory.loader.IndexLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "gae.index.kafka", name = "enable", matchIfMissing = false, havingValue = "true")
@Slf4j
public class KafkaIndexListener {
    @Autowired
    private IndexLoader indexLoader;

    @KafkaListener(topics = {"${gae.index.kafka.topic}"})
    public void listenLog(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());

        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("incremental index: {}", message);

            consumeMessage(message);
        }
    }

    private void consumeMessage(Object message) {
        String msg = (String) message;

        indexLoader.processLine(msg);
    }
}
