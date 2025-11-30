package com.codingchallenge.minidoodlev1.mapper;

import com.codingchallenge.minidoodlev1.data.dto.AvailabilityCreationRequest;
import com.codingchallenge.minidoodlev1.data.entity.Availability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityEntityMapper extends EntityMapper<Availability, AvailabilityCreationRequest> {

}
