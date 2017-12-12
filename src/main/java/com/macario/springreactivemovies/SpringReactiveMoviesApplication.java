package com.macario.springreactivemovies;

import lombok.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@SpringBootApplication
public class SpringReactiveMoviesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringReactiveMoviesApplication.class, args);
	}

}

@RestController
@RequestMapping("/movies")
class MovieController {

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    private final MovieService movieService;

    @GetMapping
    Flux<Movie> all() {
        return movieService.findAll();
    }

}

@Service
class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Flux<Movie> findAll() {
        return movieRepository.findAll();
    }
}

@Component
class CLRMovie implements CommandLineRunner {

    private MovieRepository movieRepository;

    public CLRMovie(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        this.movieRepository.deleteAll().thenMany(
            Flux.just("Silencio dos inocentes", "Morte das pombas", "Elefante do Mal", "Net é um lixo")
                .flatMap( title -> this.movieRepository.save(new Movie(title))))
            .subscribe( null,
                    null,
                    () -> this.movieRepository.findAll().subscribe(System.out::println)
            );
    }
}

interface MovieRepository extends ReactiveMongoRepository < Movie , String > {}

@Document
@Data
@NoArgsConstructor
@RequiredArgsConstructor
class Movie {

    @Id
    private String id;
    @NonNull
    private String nome;

}