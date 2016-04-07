package com.peterservice.autopay.model;

import com.peterservice.autopay.enums.MessStatus;
import com.peterservice.autopay.enums.MessType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by bishop on 23.11.2015.
 */

@Entity
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "user")
    String user;

    @Column(name = "description")
    String description;

    @Column(name = "key")
    String key;

    @Column(name = "exchange_name")
    String exchangeName;

    @Column(name = "queue_name")
    String queueName;

    @Column(name = "money")
    Float money;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    Date date;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    MessType messType;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    MessStatus messStatus;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MessType getMessType() {
        return messType;
    }

    public void setMessType(MessType messType) {
        this.messType = messType;
    }

    public MessStatus getMessStatus() {
        return messStatus;
    }

    public void setMessStatus(MessStatus messStatus) {
        this.messStatus = messStatus;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Topic topic = (Topic) o;

        if (id != null ? !id.equals(topic.id) : topic.id != null) return false;
        if (user != null ? !user.equals(topic.user) : topic.user != null) return false;
        if (description != null ? !description.equals(topic.description) : topic.description != null) return false;
        if (key != null ? !key.equals(topic.key) : topic.key != null) return false;
        if (exchangeName != null ? !exchangeName.equals(topic.exchangeName) : topic.exchangeName != null) return false;
        if (queueName != null ? !queueName.equals(topic.queueName) : topic.queueName != null) return false;
        if (money != null ? !money.equals(topic.money) : topic.money != null) return false;
        if (date != null ? !date.equals(topic.date) : topic.date != null) return false;
        if (messType != topic.messType) return false;
        return messStatus == topic.messStatus;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (exchangeName != null ? exchangeName.hashCode() : 0);
        result = 31 * result + (queueName != null ? queueName.hashCode() : 0);
        result = 31 * result + (money != null ? money.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (messType != null ? messType.hashCode() : 0);
        result = 31 * result + (messStatus != null ? messStatus.hashCode() : 0);
        return result;
    }
}
