package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.transaction.Transactional;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;
	@Autowired
	private MovieRepository movieRepository;

	@Transactional
	public Artist saveArtist(Artist artist) {
		return this.artistRepository.save(artist);
	}

	@Transactional
	public List<Artist> findArtistByNameAndSurname(String name, String surname) {
		return this.artistRepository.findByNameAndSurname(name, surname);
	}

	@Transactional
	public Artist findArtistById(Long id) {
		return this.artistRepository.findById(id).orElse(null);
	}

	@Transactional
	public Iterable<Artist> findAllArtists() {
		return this.artistRepository.findAll();
	}

	@Transactional
	public void deleteArtist(Long id) {
		
		Artist artist = this.findArtistById(id);
		
		List<Movie> moviesDirected = this.movieRepository.findByDirector(artist);
		
		for(Movie m : moviesDirected) {
			m.setDirector(null);
			this.movieRepository.save(m);
		}
		
		List<Movie> movieRecited = this.movieRepository.findByActorsContaining(artist);
		
		for(Movie m : movieRecited) {
			m.getActors().remove(artist);
			this.movieRepository.save(m);
		}
		
		this.artistRepository.deleteById(id);
	}

	@Transactional
	public void modifyArtist(Artist artist, String name, String surname, LocalDate birth, LocalDate death) {
		artist.setName(name);
		artist.setSurname(surname);
		artist.setBirth(birth);
		artist.setDeath(death);
	}
	
	@Transactional
	public List<Artist> findAllArtistNotInMovie(Movie movie){
		return this.artistRepository.findArtistNotInMovie(movie);
	}

	public List<Artist> findArtistByNameOrSurname(String name, String name2) {
		return this.artistRepository.findByNameOrSurname(name, name2);
	}

	public void inizializeArtist(Artist artist, String fileName) {
		artist.setPhoto(fileName);
		artist.setActorOfMovies(new ArrayList<>());
		artist.setDirectorOfMovies(new ArrayList<>());
	}

	public Artist findArtistDirectorOfMovie(Movie movie) {
		return this.artistRepository.findByDirectorOfMoviesContaining(movie);
	}

	public List<Artist> findArtistsActorsOfMovie(Movie movie) {
		return this.artistRepository.findByActorOfMoviesContaining(movie);
	}
}
