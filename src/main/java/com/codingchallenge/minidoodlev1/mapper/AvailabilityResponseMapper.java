package com.codingchallenge.minidoodlev1.mapper;

import com.codingchallenge.minidoodlev1.data.dto.AvailabilityResponse;
import com.codingchallenge.minidoodlev1.data.entity.Availability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityResponseMapper extends ResponseMapper<AvailabilityResponse, Availability> {

}
