package com.codingchallenge.minidoodlev1.mapper;

import com.codingchallenge.minidoodlev1.data.dto.MeetingResponse;
import com.codingchallenge.minidoodlev1.data.entity.Meeting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserResponseMapper.class)
public interface MeetingResponseMapper extends ResponseMapper<MeetingResponse, Meeting> {

}
