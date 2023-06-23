package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.transaction.Transactional;

@Service
public class MovieService {

	@Autowired
	private MovieRepository movieRepository;
	@Autowired
	private ArtistRepository artistRepository;

	@Transactional
	public Movie saveMovie(Movie movie) {
		return this.movieRepository.save(movie);
	}

	@Transactional
	public Movie findMovieById(Long id) {
		return this.movieRepository.findById(id).orElse(null);
	}

	@Transactional
	public Iterable<Movie> findAllMovies() {
		return this.movieRepository.findAll();
	}

	@Transactional
	public List<Movie> findMovieByYear(Integer year) {
		return this.movieRepository.findByYear(year);
	}

	@Transactional
	public Movie setDirectorToMovie(Long movieId, Long directorId) {
		Movie movie = this.findMovieById(movieId);
		Artist director = this.artistRepository.findById(directorId).get();
		if (movie!=null && director!=null) {
			movie.setDirector(director);
			this.saveMovie(movie);
		}
		return movie;
	}
	
	@Transactional
	public void deleteMovie(Long id) {
		this.movieRepository.deleteById(id);
	}
	
	@Transactional
	public void modifyMovie (Movie movie, String title, Integer year) {
		movie.setTitle(title);
		movie.setYear(year);
			
	}
}
