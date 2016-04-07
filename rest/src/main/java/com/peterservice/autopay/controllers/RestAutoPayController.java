package com.peterservice.autopay.controllers;


import com.peterservice.autopay.dto.MessageDto;
import com.peterservice.autopay.enums.MessStatus;
import com.peterservice.autopay.enums.MessType;
import com.peterservice.autopay.service.IRabbitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;



@RestController
@RequestMapping("/rest")
public class RestAutoPayController {
    Logger logger = LoggerFactory.getLogger(RestAutoPayController.class);

    @Autowired
    IRabbitService rabbitService;

    @RequestMapping(value = "/credited/{money}", method = RequestMethod.GET)
    public ResponseEntity<MessageDto> credited(@PathVariable("money") Float money, Principal principal) {
        MessageDto messageDto = new MessageDto();
        MessageDto responseDto;

        try {
            messageDto.setUser(principal.getName());
            messageDto.setMessType(MessType.CREDITED);
            messageDto.setMoney(money);
            messageDto.setDescription("зачисление");
            responseDto = rabbitService.send(messageDto);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            messageDto.setMessStatus(MessStatus.FAILURE);
            return new ResponseEntity<>(messageDto, HttpStatus.OK);
        }

        if(responseDto == null){
            messageDto.setMessStatus(MessStatus.FAILURE);
            return new ResponseEntity<>(messageDto, HttpStatus.OK);
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/writtenoff/{money}", method = RequestMethod.GET)
    public ResponseEntity<MessageDto> writtenof(@PathVariable("money") Float money, Principal principal) {
        MessageDto messageDto = new MessageDto();
        MessageDto responseDto;

        try {
            messageDto.setUser(principal.getName());
            messageDto.setMessType(MessType.WRITTEN_OFF);
            messageDto.setMoney(money);
            messageDto.setDescription("списание");
            responseDto = rabbitService.send(messageDto);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            messageDto.setMessStatus(MessStatus.FAILURE);
            return new ResponseEntity<>(messageDto, HttpStatus.OK);
        }

        if(responseDto == null){
            messageDto.setMessStatus(MessStatus.FAILURE);
            return new ResponseEntity<>(messageDto, HttpStatus.OK);
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<MessageDto> status(Principal principal) {
        MessageDto messageDto = new MessageDto();
        MessageDto responseDto;

        try {
            messageDto.setUser(principal.getName());
            messageDto.setMessType(MessType.STATUS);
            messageDto.setDescription("статус");
            responseDto = rabbitService.send(messageDto);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            messageDto.setMessStatus(MessStatus.FAILURE);
            return new ResponseEntity<>(messageDto, HttpStatus.OK);
        }

        if(responseDto == null){
            messageDto.setMessStatus(MessStatus.FAILURE);
            return new ResponseEntity<>(messageDto, HttpStatus.OK);
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}
