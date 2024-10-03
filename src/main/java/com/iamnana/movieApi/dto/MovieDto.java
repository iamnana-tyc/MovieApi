package com.iamnana.movieApi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto{
        Integer id;
        @NotBlank(message = "Please provide movie title")
        String title;
        @NotBlank(message = "Please provide the director of the movie.")
        String director;
        @NotBlank(message = "Please provide the movie's studio")
        String studio;
        Integer releaseYear;
        Set<String> movieCast;
        @NotBlank(message = "Please provide the picture of the movie.")
        String image;
        @NotBlank(message = "Please provide the url.")
        String imageUrl;
}