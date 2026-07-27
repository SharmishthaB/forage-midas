package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionConsumer.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    public TransactionConsumer(UserRepository userRepository,
                               TransactionRecordRepository transactionRecordRepository,
                               RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void receive(Transaction transaction) {
        // Log the incoming transaction so we can inspect it easily in execution
        LOG.info("Received transaction {}", transaction);

        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {
            // Call Incentive REST API
            Incentive incentiveResponse = restTemplate.postForObject(
                    "http://localhost:8080/incentive",  // 1. Where to send the request
                    transaction,                           // 2. What object to send (converted to JSON)
                    Incentive.class                        // 3. What class to parse the JSON response into
            );

            float incentiveAmount = (incentiveResponse != null) ? incentiveResponse.getAmount() : 0.0f;

            // Update balances: Sender pays transaction amount; Recipient gets transaction amount + incentive
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

            //save updated user records
            userRepository.save(sender);
            userRepository.save(recipient);

            //save transaction record with incentive
            TransactionRecord record = new TransactionRecord(sender,recipient,transaction.getAmount(),incentiveAmount);
            transactionRecordRepository.save(record);

            LOG.info("Processed transaction: amount={}, incentive={}", transaction.getAmount(), incentiveAmount);
        }
        else {
            LOG.warn("Invalid transaction discarded: {}", transaction);
        }
    }
}
