package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionConsumer.class);

    @KafkaListener(topics = "${general.kafka-topic}")
    public void receive(Transaction transaction) {
        // Log the incoming transaction so we can inspect it easily in execution
        LOG.info("Received transaction {}", transaction);
    }
}
