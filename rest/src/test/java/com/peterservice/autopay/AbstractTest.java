package com.peterservice.autopay;

import com.peterservice.autopay.controllers.RestAutoPayController;
import com.peterservice.autopay.dto.MessageDto;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;

import java.security.Principal;
import java.util.Date;
import java.util.UUID;


@ContextConfiguration(locations = {"classpath:root-context.xml","classpath:mvc-config.xml"})
@WebAppConfiguration
public class AbstractTest extends AbstractTestNGSpringContextTests {
  @Autowired
  WebApplicationContext wac;

  @Autowired
  FilterChainProxy springSecurityFilterChain;

  @Autowired
  MappingJackson2HttpMessageConverter jsonMessageConverter;

  protected MockMvc mockMvc;

  @BeforeMethod
  public void setup() {

    this.mockMvc = MockMvcBuilders.standaloneSetup(new RestAutoPayController() {
      @Override
      public ResponseEntity<MessageDto> writtenof(@PathVariable("money") Float money, Principal principal) {
        return new ResponseEntity<>(new MessageDto(), HttpStatus.OK);
      }

      @Override
      public ResponseEntity<MessageDto> credited(@PathVariable("money") Float money, Principal principal) {
        return new ResponseEntity<>(new MessageDto(), HttpStatus.OK);
      }
    })
      .addFilters(this.springSecurityFilterChain)
      .build();
  }

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
