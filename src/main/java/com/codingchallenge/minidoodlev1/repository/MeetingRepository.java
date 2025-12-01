package com.codingchallenge.minidoodlev1.repository;

import com.codingchallenge.minidoodlev1.data.entity.Availability;
import com.codingchallenge.minidoodlev1.data.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long>, JpaSpecificationExecutor<Availability> {
}
