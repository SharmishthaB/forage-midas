package com.jpmc.midascore.entity;

import jakarta.persistence.*;


@Entity     //registers this class as a SQL database table (transaction_record).
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //Auto-increments primary keys in SQL.
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id",nullable = false)
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float incentive;

    protected TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount,float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

    public Long getId() {
        return id;
    }

    public UserRecord getSender() {
        return sender;
    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public float getAmount() {
        return amount;
    }

    public float getIncentive() {
        return incentive;
    }
}
