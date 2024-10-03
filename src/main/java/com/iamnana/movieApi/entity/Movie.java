package com.iamnana.movieApi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide movie title")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Please provide the director of the movie.")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Please provide the movie's studio")
    private String studio;

    @Column(nullable = false)
    private Integer releaseYear;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @Column(nullable = false)
    @NotBlank(message = "Please provide the picture of the movie.")
    private String image;

}
