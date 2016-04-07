package com.peterservice.autopay.service;

import com.peterservice.autopay.dto.MessageDto;
import com.peterservice.autopay.enums.MessStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.*;


import java.util.Date;
import java.util.UUID;

@Service
public class RabbitService implements IRabbitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitService.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MappingJackson2HttpMessageConverter jsonMessageConverter;


    public Message getMessage(MessageDto messageDto) throws Exception {
        org.springframework.amqp.core.MessageProperties properties = new org.springframework.amqp.core.MessageProperties();
        properties.setContentType("application/json");
        properties.setContentEncoding("UTF-8");
        properties.setHeader("version", "1.0");
        properties.setHeader("operationDate", new Date());
        properties.setCorrelationId(UUID.randomUUID().toString().getBytes());
        String json = jsonMessageConverter.getObjectMapper().writeValueAsString(messageDto);
        return new Message(json.getBytes("UTF-8"), properties);
    }

    public MessageDto send(MessageDto messageDto) throws Exception {
        Message mess = null;
        try {
            mess = rabbitTemplate.sendAndReceive("client." + messageDto.getMessType().getKey(), getMessage(messageDto));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            messageDto.setMessStatus(MessStatus.NOT_AVAILABLE);
            return messageDto;
        }

        if(mess == null)
            return null;

        LOGGER.info(mess.toString());

        return jsonMessageConverter.getObjectMapper().readValue(new String(mess.getBody(), "UTF-8"), MessageDto.class);
    }

}
