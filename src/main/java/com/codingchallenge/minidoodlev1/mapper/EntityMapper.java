package com.codingchallenge.minidoodlev1.mapper;

public interface EntityMapper<E, R> {
    E toEntity(R request);
}
