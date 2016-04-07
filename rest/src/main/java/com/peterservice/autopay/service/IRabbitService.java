package com.peterservice.autopay.service;

import com.peterservice.autopay.dto.MessageDto;
import org.springframework.amqp.core.Message;


public interface IRabbitService {
    MessageDto send(MessageDto messageDto) throws Exception;

    Message getMessage(MessageDto messageDto) throws Exception;
}
