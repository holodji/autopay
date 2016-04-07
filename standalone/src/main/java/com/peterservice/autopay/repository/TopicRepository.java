package com.peterservice.autopay.repository;

import com.peterservice.autopay.enums.MessStatus;
import com.peterservice.autopay.enums.MessType;
import com.peterservice.autopay.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by bishop on 23.11.2015.
 */
public interface TopicRepository extends JpaRepository<Topic, Integer>, JpaSpecificationExecutor<Topic> {

    @Query(value = "select sum(t.money) from Topic t where t.user = ?1 and t.messStatus = ?2 and t.messType =?3")
    Float getManyByUser(String user, MessStatus messStatus, MessType messType);

    @Transactional
    @Modifying
    @Query("delete from Topic t where t.user = ?1")
    void deleteByUser(String user);

}
