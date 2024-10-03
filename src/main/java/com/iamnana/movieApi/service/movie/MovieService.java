package com.iamnana.movieApi.service.movie;

import com.iamnana.movieApi.dto.MovieDto;
import com.iamnana.movieApi.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;
    MovieDto getMovieById(Integer id);
    List<MovieDto> getAllMovies();
    String deleteMovie(Integer id) throws IOException;
    MovieDto updateMovie(MovieDto movieDto, MultipartFile file, Integer id) throws IOException;
    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);
    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                           String sortBy, String sortDirection);
}
