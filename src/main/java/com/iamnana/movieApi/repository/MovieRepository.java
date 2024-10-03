package com.iamnana.movieApi.repository;

import com.iamnana.movieApi.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie,Integer> {
}
