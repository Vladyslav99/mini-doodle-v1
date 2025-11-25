package com.codingchallenge.minidoodlev1.mapper;

public interface DataMapper<D, E> {

    D toDto(E entity);

    E toEntity(D dto);
}
