package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class MovieService {

	@Autowired
	private MovieRepository movieRepository;
	@Autowired
	private ArtistService artistService;

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
		Artist director = this.artistService.findArtistById(directorId);
		if (movie!=null && director!=null) {
			movie.setDirector(director);
			this.saveMovie(movie);
		}
		return movie;
	}
	
	@Transactional
	public void deleteMovie(Long id) {
		
		Movie movie = this.findMovieById(id);
		
		Artist directorOfMovie = this.artistService.findArtistDirectorOfMovie(movie);
		
		if(directorOfMovie!=null) {
			directorOfMovie.getDirectorOfMovies().remove(movie);
			this.artistService.saveArtist(directorOfMovie);
		}
		
		List<Artist> actorsOfMovie = this.artistService.findArtistsActorsOfMovie(movie);
		for(Artist a : actorsOfMovie) {
			a.getActorOfMovies().remove(movie);
			this.artistService.saveArtist(a);
		}
		
		this.movieRepository.deleteById(id);
	}
	
	@Transactional
	public void modifyMovie (Movie movie, String title, Integer year, Artist director) {
		movie.setTitle(title);
		movie.setYear(year);
		movie.setDirector(director);
	}

	@Transactional
	public void inizializeMovie(@Valid Movie movie, String fileName, Artist director) {
		movie.setImage(fileName);
		movie.setNumOfVote(0);
		movie.setVote(0);	
		movie.setDirector(director);
		List<Artist> actors = new ArrayList<>();
		movie.setActors(actors);
		List<Float> allVotes = new ArrayList<>();
		movie.setAllVotes(allVotes);
	}

	@Transactional
	public void setVote(Movie movie, float currentVote) {
		List<Float> allVotes = movie.getAllVotes();
		float oldSumOfVote = 0;
		for(float v : allVotes) {
			oldSumOfVote = oldSumOfVote + v;
		}
		
		Integer oldNumOfVote = movie.getNumOfVote();
		Integer newNumOfVote = oldNumOfVote+1;
		
		float newSumOfVote = oldSumOfVote + currentVote;
		float newVote = newSumOfVote/newNumOfVote;
		
		movie.setVote(Math.round(newVote*100)/100f);
		movie.setNumOfVote(newNumOfVote);
		movie.getAllVotes().add(currentVote);
	}

	@Transactional
	public void deleteOldVote(float vote, Movie movie) {
		System.out.print("tutti i voti \n");
		System.out.print(movie.getAllVotes() + "\n");
		float oldVote = movie.getVote();
		System.out.print("prendo il vecchio voto" + oldVote + "\n");
		float oldNumOfVote = movie.getNumOfVote();
		System.out.print("prendo il vecchio numero di voti" + oldNumOfVote + "\n");
		float newNumOfVote = oldNumOfVote-1;
		System.out.print("prendo il nuovo numero di voti" + newNumOfVote + "\n");
		movie.getAllVotes().remove(vote);
		
		float newVote;
		if(newNumOfVote == 0) {
			newVote = 0;
		}
		else {
			float divisore = vote/oldNumOfVote;
			float firstOperator = oldVote - divisore;
			float secondOperator = oldNumOfVote/newNumOfVote;
			newVote = (firstOperator) * (secondOperator);
			System.out.print("il nuovo voto " + newVote + "\n");
		}
		
		movie.setVote(Math.round(newVote*100)/100f);
		movie.setNumOfVote((int) newNumOfVote);
	}
	
	public List<Movie> findAllMoviesOrderedByVote(){
		return this.movieRepository.findAllByOrderByVoteDesc();
	}

	public List<Movie> findAllMoviesByDirector(Artist director) {
		return this.movieRepository.findByDirector(director);
	}

	public List<Movie> findAllMoviesByActor(Artist artist) {
		return this.movieRepository.findByActorsContaining(artist);
	}
}
