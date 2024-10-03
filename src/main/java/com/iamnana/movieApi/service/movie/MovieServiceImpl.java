package com.iamnana.movieApi.service.movie;

import com.iamnana.movieApi.dto.MovieDto;
import com.iamnana.movieApi.dto.MoviePageResponse;
import com.iamnana.movieApi.entity.Movie;
import com.iamnana.movieApi.exception.FileExistException;
import com.iamnana.movieApi.exception.MovieNotFoundException;
import com.iamnana.movieApi.repository.MovieRepository;
import com.iamnana.movieApi.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class MovieServiceImpl  implements MovieService {
    private final MovieRepository movieRepository;
    private final FileService fileService;

    // File path from app settings
    @Value("${project.image}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        // we have to check if file already exist to avoid duplicating
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))){
            throw new FileExistException("File already exist, please change the file name");
        }
        // We need to upload the file
        String uploadedFileName = fileService.uploadFile(path, file);

        // we need to set the value of the field as fileName
        movieDto.setImage(uploadedFileName);

        // Map dto to movie object
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getReleaseYear(),
                movieDto.getMovieCast(),
                movieDto.getImage()
        );

        // we need to save to
        Movie savedMovie = movieRepository.save(movie);

        // we need to generate an image url
        String imageUrl = baseUrl + "/file/" + uploadedFileName;

        // map movie to movieDto object and return it
        MovieDto response = new MovieDto(
                savedMovie.getId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getReleaseYear(),
                savedMovie.getMovieCast(),
                savedMovie.getImage(),
                imageUrl
        );
        return response;
    }

    @Override
    public MovieDto getMovieById(Integer id) {
        Movie movie =  movieRepository.findById(id)
                .orElseThrow(()-> new MovieNotFoundException("Movie with id " + id + " not found"));

        String imageUrl = baseUrl + "/file/" + movie.getImage();
        MovieDto response = new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getReleaseYear(),
                movie.getMovieCast(),
                movie.getImage(),
                imageUrl
        );
        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        // first we get all movies from db
        List<Movie> movies =  movieRepository.findAll();

        // create an empty array to store the movies
        List<MovieDto> movieDtoList = new ArrayList<>();

        // then we iterate through the movies to get each image
        for(Movie movie : movies){
            String imageUrl = baseUrl + "/file/" + movie.getImage();
            MovieDto movieDto = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getReleaseYear(),
                    movie.getMovieCast(),
                    movie.getImage(),
                    imageUrl
            );
            // we add to the array
            movieDtoList.add(movieDto);
        }
        return movieDtoList;
    }

    @Override
    public String deleteMovie(Integer id) throws IOException {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(()-> new MovieNotFoundException("Movie with " + id + " can't be deleted"));

        Files.deleteIfExists(Paths.get(path + File.separator + movie.getImage()));
        movieRepository.delete(movie);

        return "Movie successfully deleted";
    }

    @Override
    public MovieDto updateMovie(MovieDto movieDto, MultipartFile file, Integer id) throws IOException {
        Movie getMovie = movieRepository.findById(id)
                .orElseThrow(()-> new MovieNotFoundException("Movie " + id + " does not exist"));

        String fileName = getMovie.getImage();
        // we need to check if the file is null, do nothing
        // however if file isn't null, then delete exiting file associated with the record, and upload new file
        if(file != null){
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path,file);
        }
        // set the image value
        movieDto.setImage(fileName);

        // map the movie to the object
        Movie movie = new Movie(
                getMovie.getId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getReleaseYear(),
                movieDto.getMovieCast(),
                movieDto.getImage()
        );
        // save the movie in db
        Movie updatedMovie = movieRepository.save(movie);

        // get the image url
        String imageUrl = baseUrl + "/file/" + fileName;

        MovieDto response = new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getReleaseYear(),
                movie.getMovieCast(),
                movie.getImage(),
                imageUrl
        );
        return response;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> allMoviePages = movieRepository.findAll(pageable);
        List<Movie> movies = allMoviePages.getContent();

        List<MovieDto> movieDtoList = new ArrayList<>();

        // then we iterate through the movies to get each image
        for(Movie movie : movies){
            String imageUrl = baseUrl + "/file/" + movie.getImage();
            MovieDto movieDto = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getReleaseYear(),
                    movie.getMovieCast(),
                    movie.getImage(),
                    imageUrl
            );
            // we add to the array
            movieDtoList.add(movieDto);
        }
        return new MoviePageResponse(movieDtoList,pageNumber,pageSize,
                                     allMoviePages.getTotalElements(),
                                     allMoviePages.getTotalPages(),
                                     allMoviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                                  String sortBy, String sortDirection)
    {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Movie> allMoviePages = movieRepository.findAll(pageable);
        List<Movie> movies = allMoviePages.getContent();

        List<MovieDto> movieDtoList = new ArrayList<>();

        // then we iterate through the movies to get each image
        for(Movie movie : movies){
            String imageUrl = baseUrl + "/file/" + movie.getImage();
            MovieDto movieDto = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getReleaseYear(),
                    movie.getMovieCast(),
                    movie.getImage(),
                    imageUrl
            );
            // we add to the array
            movieDtoList.add(movieDto);
        }
        return new MoviePageResponse(movieDtoList,pageNumber,pageSize,
                allMoviePages.getTotalElements(),
                allMoviePages.getTotalPages(),
                allMoviePages.isLast());
    }
}
