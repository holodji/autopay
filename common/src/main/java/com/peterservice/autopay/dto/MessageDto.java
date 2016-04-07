package com.peterservice.autopay.dto;

import com.peterservice.autopay.enums.MessStatus;
import com.peterservice.autopay.enums.MessType;

public class MessageDto {
    String user;
    MessType messType;
    MessStatus messStatus;
    String description;
    Float money;
    Float bank;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public MessType getMessType() {
        return messType;
    }

    public void setMessType(MessType messType) {
        this.messType = messType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MessStatus getMessStatus() {
        return messStatus;
    }

    public void setMessStatus(MessStatus messStatus) {
        this.messStatus = messStatus;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Float getBank() {
        return bank;
    }

    public void setBank(Float bank) {
        this.bank = bank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageDto that = (MessageDto) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (messType != that.messType) return false;
        if (messStatus != that.messStatus) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (money != null ? !money.equals(that.money) : that.money != null) return false;
        return !(bank != null ? !bank.equals(that.bank) : that.bank != null);

    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (messType != null ? messType.hashCode() : 0);
        result = 31 * result + (messStatus != null ? messStatus.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (money != null ? money.hashCode() : 0);
        result = 31 * result + (bank != null ? bank.hashCode() : 0);
        return result;
    }
}
