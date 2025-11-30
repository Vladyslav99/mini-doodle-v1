package com.codingchallenge.minidoodlev1.mapper;

public interface ResponseMapper<R, E> {
    R toResponse(E entity);
}
