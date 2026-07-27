package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionConsumer.class);

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionConsumer(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void receive(Transaction transaction) {
        // Log the incoming transaction so we can inspect it easily in execution
        LOG.info("Received transaction {}", transaction);

        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {
            //updates balances
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());

            //save updated user records
            userRepository.save(sender);
            userRepository.save(recipient);

            //save transaction audit record
            TransactionRecord record = new TransactionRecord(sender,recipient,transaction.getAmount());
            transactionRecordRepository.save(record);

            LOG.info("Transaction processed successfully for amount: {}", transaction.getAmount());
        }
        else {
            LOG.warn("Invalid transaction discarded: {}", transaction);
        }
    }
}
