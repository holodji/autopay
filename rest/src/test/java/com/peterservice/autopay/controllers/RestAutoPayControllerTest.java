package com.peterservice.autopay.controllers;

import com.peterservice.autopay.AbstractTest;
import com.peterservice.autopay.dto.MessageDto;
import com.peterservice.autopay.enums.MessStatus;
import com.peterservice.autopay.enums.MessType;
import com.peterservice.autopay.service.IRabbitService;
import com.peterservice.autopay.service.RabbitService;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.Test;
import sun.misc.BASE64Encoder;

import java.net.ConnectException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class RestAutoPayControllerTest extends AbstractTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitService.class);

  @Autowired
  RabbitTemplate rabbitTemplate;

  @Autowired
  IRabbitService rabbitService;


  /**
   * Проверка авторизации с существующим юзером user
   * Ожидаемый результат HttpStatus == HttpStatus.OK (200)
   * @throws Exception
   */
  @Test
  public void securitySuccessTest() throws Exception {
      String authString = new BASE64Encoder().encode(("user:pass").getBytes("UTF-8"));
      mockMvc.perform(get("/rest/writtenoff/20").characterEncoding("UTF-8")
              .header("authorization", "Basic " + authString))
              .andExpect(status().isOk());
  }

 /**
 * Проверка авторизации с несуществующим юзером user1
 * Ожидаемый результат HttpStatus == HttpStatus.UNAUTHORIZED (401)
 * @throws Exception
 */
  @Test
  public void securityFailureTest() throws Exception {
      String authString = new BASE64Encoder().encode(("user:pass1").getBytes("UTF-8"));
      mockMvc.perform(get("/rest/credited/20").characterEncoding("UTF-8")
              .header("authorization", "Basic " + authString))
              .andExpect(status().isUnauthorized());
  }

  /**
   * Проверка поведения когда недоступен RabbitMQ
   * Ожидаемый результат MessStatus == MessStatus.NOT_AVAILABLE
   * @throws Exception
     */
  @Test
  public void rabbitMQNotAvailable() throws Exception {
    //Создание сообщения
    MessageDto credited = credited(60);
    Message sendMessage = getMessage(credited);
    //Создание spy объектов
    RabbitTemplate spyRabbitTemplate = Mockito.spy(rabbitTemplate);
    IRabbitService spyRabbitService = Mockito.spy(rabbitService);

    ReflectionTestUtils.setField(spyRabbitService, "rabbitTemplate", spyRabbitTemplate);
    //Заглушка на RabbitService.sendAndReceive()
    Mockito.doThrow(new AmqpConnectException(new ConnectException("Connection refused: connect")))
      .when(spyRabbitTemplate).sendAndReceive(getKey(credited), sendMessage);
    //Заглушка на RabbitService.getMessage()
    Mockito.doReturn(sendMessage)
      .when(spyRabbitService).getMessage(credited);

    MessageDto messageDto = spyRabbitService.send(credited);
    //Проверка фактического и ожидаемого MessStatus
    assert (messageDto != null);
    assert (messageDto.getMessStatus() == MessStatus.NOT_AVAILABLE);

  }

  private MessageDto credited(float money) {
    MessageDto messageDto = new MessageDto();
    messageDto.setUser("user");
    messageDto.setMessType(MessType.CREDITED);
    messageDto.setMoney(money);
    messageDto.setDescription("зачисление");
    return messageDto;
  }


  private String getKey(MessageDto messageDto) {
    return "client." + messageDto.getMessType().getKey();
  }




}
