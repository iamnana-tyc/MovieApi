package com.iamnana.movieApi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iamnana.movieApi.dto.MovieDto;
import com.iamnana.movieApi.dto.MoviePageResponse;
import com.iamnana.movieApi.exception.EmptyFileException;
import com.iamnana.movieApi.service.file.FileService;
import com.iamnana.movieApi.service.movie.MovieService;
import com.iamnana.movieApi.utils.AppConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movie")
public class MovieController {
    private final MovieService movieService;
    private final FileService fileService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/save-movie")
    public ResponseEntity<MovieDto> saveMovie(@RequestPart MultipartFile file,
                                              @RequestPart String movieDto) throws IOException, EmptyFileException {
        if(file.isEmpty()){
            throw new EmptyFileException("File is empty, add a file.");
        }
        MovieDto dto = convertMovieDto(movieDto);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    @GetMapping("/all-movies")
    public ResponseEntity<List<MovieDto>> getAllMovies(){
        return new ResponseEntity<>(movieService.getAllMovies(),HttpStatus.OK);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Integer movieId){
        return new ResponseEntity<>(movieService.getMovieById(movieId),HttpStatus.OK);

    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Integer movieId,
                                                @RequestPart MultipartFile file,
                                                @RequestPart String movieDtoObject) throws IOException {
        if(file.isEmpty())
            file = null;
        MovieDto movieDto = convertMovieDto(movieDtoObject);
        return new ResponseEntity<>(movieService.updateMovie(movieDto,file,movieId), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovie(@PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse> getAllMoviesByPagination(
            @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize
    )
    {
        return  ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber, pageSize));
    }

    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getAllMoviesByPaginationAndSorting(
            @RequestParam(defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstant.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstant.SORT_DIRECTION, required = false) String dir
    )
    {
        return  ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber, pageSize,sortBy,dir));
    }

    // This is to convert the movieDto into a string
    private MovieDto convertMovieDto(String movieDtoObject) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObject, MovieDto.class);
    }


}
