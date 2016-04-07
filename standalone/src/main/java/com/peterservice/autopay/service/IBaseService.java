package com.peterservice.autopay.service;

import com.peterservice.autopay.dto.MessageDto;

public interface IBaseService {

    MessageDto save(MessageDto messageDto);

    MessageDto saveCredited(MessageDto messageDto);

    MessageDto saveWrittenOff(MessageDto messageDto);

    void deleteMessages(String user);

    float getCurrentBank(String user) throws Exception;
}
