package com.codingchallenge.minidoodlev1.specification;

import com.codingchallenge.minidoodlev1.data.entity.Availability;
import com.codingchallenge.minidoodlev1.data.enums.AvailabilityStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class AvailabilitySpecification {

    public static Specification<Availability> availabilityId(Long availabilityId) {
        return (root, query, cb) -> cb.equal(root.get("id"), availabilityId);
    }

    public static Specification<Availability> ownerId(Long ownerId) {
        return (root, query, cb) -> cb.equal(root.get("ownerId"), ownerId);
    }

    public static Specification<Availability> availabilityStatus(AvailabilityStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("availabilityStatus"), status);
    }

    public static Specification<Availability> overlapping(Instant startDateTime, Instant endDateTime) {
        return (root, query, cb) -> {
            if (startDateTime == null || endDateTime == null) {
                return null;
            }

            return cb.and(
                    cb.greaterThan(root.get("endDateTime"), startDateTime),
                    cb.lessThan(root.get("startDateTime"), endDateTime)
            );
        };
    }

    public static Specification<Availability> orderByStartDateTimeAsc = (root, query, cb) -> {
        query.orderBy(cb.asc(root.get("startDateTime")));
        return null;
    };

}
