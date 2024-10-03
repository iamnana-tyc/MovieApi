package com.iamnana.movieApi.dto;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

public record MoviePageResponse(List<MovieDto> movieDtos,
                                Integer pageNumber,
                                Integer pageSize,
                                Long totalElements,
                                Integer totalPages,
                                boolean isLast) {
}
