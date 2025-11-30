package com.codingchallenge.minidoodlev1.repository;

import com.codingchallenge.minidoodlev1.data.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long>, JpaSpecificationExecutor<Availability> {

    boolean existsByOwnerIdAndStartDateTimeBeforeAndEndDateTimeAfter(Long ownerId, Instant startDateTimeBefore, Instant endDateTimeAfter);

    boolean existsByIdAndOwnerId(Long availabilityId, Long ownerId);

    Optional<Availability> findByIdAndOwnerId(Long availabilityId, Long ownerId);
}
