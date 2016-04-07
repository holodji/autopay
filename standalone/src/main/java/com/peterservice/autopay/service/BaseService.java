package com.peterservice.autopay.service;

import com.peterservice.autopay.dto.MessageDto;
import com.peterservice.autopay.enums.MessStatus;
import com.peterservice.autopay.enums.MessType;
import com.peterservice.autopay.model.Topic;
import com.peterservice.autopay.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class BaseService implements IBaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

    @Autowired
    TopicRepository topicRepository;

    @Transactional
    public MessageDto save(MessageDto messageDto) {
        if(messageDto.getMessType() == MessType.CREDITED)
            return saveCredited(messageDto);
        else if(messageDto.getMessType() == MessType.WRITTEN_OFF)
            return saveWrittenOff(messageDto);
        else if(messageDto.getMessType() == MessType.STATUS)
            return getStatus(messageDto);
        else
            return null;
    }

    @Transactional
    public void deleteMessages(String user) {
        topicRepository.deleteByUser(user);
    }

    public MessageDto getStatus(MessageDto messageDto) {
        try {
            messageDto.setBank(getBank(messageDto));
            messageDto.setMessStatus(MessStatus.SUCCESS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            messageDto.setMessStatus(MessStatus.FAILURE);
        }
        return messageDto;
    }

    public MessageDto saveCredited(MessageDto messageDto) {
        try {
            Topic topic = new Topic();
            topic.setUser(messageDto.getUser());
            topic.setDescription(messageDto.getDescription());
            topic.setMoney(messageDto.getMoney());
            topic.setDate(new Date());
            topic.setMessStatus(MessStatus.SUCCESS);
            topic.setMessType(MessType.CREDITED);
            topic.setKey(MessType.CREDITED.getKey());
            topicRepository.save(topic);
            messageDto.setBank(getBank(messageDto));
            messageDto.setMessStatus(MessStatus.SUCCESS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            messageDto.setMessStatus(MessStatus.FAILURE);
        }
        return messageDto;
    }

    public MessageDto saveWrittenOff(MessageDto messageDto) {
        try {
            float bank = getBank(messageDto);

            if(bank  < messageDto.getMoney()) {
                messageDto.setMessStatus(MessStatus.A_LITTLE_MONEY);
                messageDto.setBank(bank);
                return messageDto;
            }

            Topic topic = new Topic();
            topic.setUser(messageDto.getUser());
            topic.setDescription(messageDto.getDescription());
            topic.setMoney(messageDto.getMoney());
            topic.setDate(new Date());
            topic.setMessStatus(MessStatus.SUCCESS);
            topic.setMessType(MessType.WRITTEN_OFF);
            topic.setKey(MessType.WRITTEN_OFF.getKey());
            topicRepository.save(topic);
            messageDto.setMessStatus(MessStatus.SUCCESS);
            messageDto.setBank(getBank(messageDto));
            return messageDto;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            messageDto.setMessStatus(MessStatus.FAILURE);
        }
        return messageDto;
    }

    private float getBank(MessageDto messageDto) throws Exception {
        return getCurrentBank(messageDto.getUser());
    }

    public float getCurrentBank(String user) throws Exception {
        Float creditedMoney = topicRepository.getManyByUser(user, MessStatus.SUCCESS, MessType.CREDITED);
        Float writtenOfMoney = topicRepository.getManyByUser(user, MessStatus.SUCCESS, MessType.WRITTEN_OFF);
        if(creditedMoney != null && writtenOfMoney != null)
            return creditedMoney - writtenOfMoney;
        else if(creditedMoney != null)
            return creditedMoney;
        else if(writtenOfMoney != null)
            return -1 * writtenOfMoney;
        else
            return 0f;
    }
}
