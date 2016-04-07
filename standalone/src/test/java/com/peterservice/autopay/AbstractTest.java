package com.peterservice.autopay;

import com.peterservice.autopay.dto.MessageDto;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.util.Date;
import java.util.UUID;


@ContextConfiguration(locations = {"classpath:spring/root-context.xml","classpath:spring/sender.xml"})
public class AbstractTest extends AbstractTestNGSpringContextTests {
  @Autowired
  MappingJackson2HttpMessageConverter jsonMessageConverter;

  protected Message getMessage(MessageDto messageDto) throws Exception {
    org.springframework.amqp.core.MessageProperties properties = new org.springframework.amqp.core.MessageProperties();
    properties.setContentType("application/json");
    properties.setContentEncoding("UTF-8");
    properties.setHeader("version", "1.0");
    properties.setHeader("operationDate", new Date());
    properties.setCorrelationId(UUID.randomUUID().toString().getBytes());
    String json = jsonMessageConverter.getObjectMapper().writeValueAsString(messageDto);
    return new Message(json.getBytes("UTF-8"), properties);
  }

  protected MessageDto getMessageDto(Message message) throws Exception {
    return jsonMessageConverter.getObjectMapper().readValue(new String(message.getBody(), "UTF-8"), MessageDto.class);
  }
}
