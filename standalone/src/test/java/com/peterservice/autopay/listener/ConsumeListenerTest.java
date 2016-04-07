package com.peterservice.autopay.listener;

import com.peterservice.autopay.AbstractTest;
import com.peterservice.autopay.dto.MessageDto;
import com.peterservice.autopay.enums.MessStatus;
import com.peterservice.autopay.enums.MessType;
import com.peterservice.autopay.service.IBaseService;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.Test;


public class ConsumeListenerTest extends AbstractTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConsumeListenerTest.class);
  private final static String USER = "test_user";

  @Autowired
  RabbitTemplate rabbitTemplate;

  @Autowired
  RabbitTemplate amqpTemplate;

  @Autowired
  IBaseService baseService;

  @Autowired
  ConsumerListener consumerListener;

  @Autowired
  ConnectionFactory connectionFactory;

  /**
   * Попытка снятия со счета больше, чем есть
   * Ожидаемый результат MessStatus == MessStatus.A_LITTLE_MONEY
   */
  @Test
  public void notEnoughMoneyTest() throws Exception {
    //Очистка записей тестового юзера
    baseService.deleteMessages(USER);
    //Зачисление через базу тестовому юзеру 30
    baseService.save(credited(50));
    //Отправили запрос через rabbitmq на снятие 50
    Message mess = rabbitTemplate.sendAndReceive("client." + writtenOff(51).getMessType().getKey(), getMessage(writtenOff(51)));
    //Проверка фактического и ожидаемого MessStatus
    assert (mess != null);
    assert (getMessageDto(mess).getMessStatus() == MessStatus.A_LITTLE_MONEY);
  }

  /**
   * Зачисления большей суммы и (сразу же) снятие большой суммы
   */
  @Test
  public void manyMessagesTest() throws Exception {
    //Очистка записей тестового юзера
    baseService.deleteMessages(USER);
    //Создание сообщений
    MessageDto credited = credited(100);
    MessageDto writtenOff = writtenOff(70);
    MessageDto credited2 = credited(70);
    MessageDto writtenOff2 = writtenOff(100);
    //Отправка запроса через rabbitmq на зачисление 100
    Message mess1 = rabbitTemplate.sendAndReceive("client." + credited.getMessType().getKey(), getMessage(credited));
    //Проверка ожидаемого статуса сообщения
    assert (mess1 != null);
    assert (getMessageDto(mess1).getMessStatus() == MessStatus.SUCCESS);
    //Снятие 70
    Message mess2 = rabbitTemplate.sendAndReceive("client." + writtenOff.getMessType().getKey(), getMessage(writtenOff));
    //Проверка статус сообщения
    assert (mess2 != null);
    assert (getMessageDto(mess2).getMessStatus() == MessStatus.SUCCESS);
    //Поменялись местами, сняли 70
    Message mess3 = rabbitTemplate.sendAndReceive("client." + writtenOff2.getMessType().getKey(), getMessage(writtenOff2));
    //Проверка статус сообщения
    assert (mess3 != null);
    assert (getMessageDto(mess3).getMessStatus() == MessStatus.A_LITTLE_MONEY);
    //Зачисление 100
    Message mess4 = rabbitTemplate.sendAndReceive("client." + credited2.getMessType().getKey(), getMessage(credited2));
    //Проверка статуса сообщения
    assert (mess4 != null);
    assert (getMessageDto(mess4).getMessStatus() == MessStatus.SUCCESS);
    //Проверка баланса
    assert (Float.compare(getMessageDto(mess4).getBank(), 130f) == 0);
  }

  /**
   * Проверка поведения когда недоступна БД
   * Ожидаемый результат MessStatus == MessageStatus.BASE_ERROR
   * @throws Exception
   */
  @Test()
  public void dbNotAvailableTest() throws Exception {
    //Формирование сообщения
    MessageDto credited = credited(60);
    Message sendMessage = getMessage(credited);
    //Создание spy объекта для baseService
    IBaseService spyBaseService = Mockito.spy(baseService);
    ReflectionTestUtils.setField(consumerListener, "baseService", spyBaseService);
    //Заглушка baseService.save()
    Mockito.doThrow(new RuntimeException("TEST: Connection refused: connect"))
            .when(spyBaseService).save(credited);
    //Отправка запроса через rabbitmq
    Message message = rabbitTemplate.sendAndReceive(getKey(credited), sendMessage);
    //Сравнение фактического и ожидаемого MessStatus
    assert (message != null);
    assert (getMessageDto(message).getMessStatus() == MessStatus.BASE_ERROR);
  }

  /**
   * Проверка соединения с RabbitMQ
   * @throws Exception
   */
  @Test()
  public void rabbitMQConnectionTest() throws Exception{
    Connection connection = connectionFactory.createConnection();
    assert(connection != null);
  }

  private String getKey(MessageDto messageDto) {
      return "client." + messageDto.getMessType().getKey();
  }

  private MessageDto writtenOff(float money) {
    MessageDto messageDto = new MessageDto();
    messageDto.setUser(USER);
    messageDto.setMessType(MessType.WRITTEN_OFF);
    messageDto.setMoney(money);
    messageDto.setDescription("списание");
    return messageDto;
  }

  private MessageDto credited(float money) {
    MessageDto messageDto = new MessageDto();
    messageDto.setUser(USER);
    messageDto.setMessType(MessType.CREDITED);
    messageDto.setMoney(money);
    messageDto.setDescription("зачисление");
    return messageDto;
  }
}
