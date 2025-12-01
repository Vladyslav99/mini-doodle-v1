package com.codingchallenge.minidoodlev1.mapper;

import com.codingchallenge.minidoodlev1.data.dto.UserResponse;
import com.codingchallenge.minidoodlev1.data.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserResponseMapper extends ResponseMapper<UserResponse, User> {
}
