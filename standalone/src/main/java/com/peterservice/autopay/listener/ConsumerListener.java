package com.peterservice.autopay.listener;

import com.peterservice.autopay.dto.MessageDto;
import com.peterservice.autopay.enums.MessStatus;
import com.peterservice.autopay.service.IBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;


import java.util.Date;

@Component
public class ConsumerListener implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerListener.class);

    @Autowired
    RabbitTemplate amqpTemplate;

    @Autowired
    MappingJackson2HttpMessageConverter jsonMessageConverter;

    @Autowired
    IBaseService baseService;


    @Override
    public void onMessage(Message message) {
        MessageDto messageDto = null;

        try {
            messageDto = getMessageDto(message);
            messageDto = baseService.save(messageDto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if(messageDto != null) {
                messageDto.setMessStatus(MessStatus.BASE_ERROR);
                try {
                    amqpTemplate.send("server." + messageDto.getMessType().getKey(),
                            new Message(jsonMessageConverter.getObjectMapper().writeValueAsString(messageDto).getBytes("UTF-8"), message.getMessageProperties()));
                } catch (Exception e1) {
                    LOGGER.error(e.getMessage());
                }
            }
            return;
        }
        try {
            amqpTemplate.send("server." + messageDto.getMessType().getKey(),
                    new Message(jsonMessageConverter.getObjectMapper().writeValueAsString(messageDto).getBytes("UTF-8"), message.getMessageProperties()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info(message.toString());
    }

    public MessageDto getMessageDto(Message message) throws Exception {
        org.springframework.amqp.core.MessageProperties properties = message.getMessageProperties();
        properties.setHeader("operationDate", new Date());
        return jsonMessageConverter.getObjectMapper().readValue(new String(message.getBody(), "UTF-8"), MessageDto.class);
    }


}
