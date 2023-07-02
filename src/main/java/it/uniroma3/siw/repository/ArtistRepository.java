package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;

public interface ArtistRepository extends CrudRepository<Artist, Long>{

	public boolean existsByNameAndSurname (String name, String Surname);
	public List<Artist> findByNameAndSurname(String name, String Surname);
	
	@Query("SELECT a FROM Artist a WHERE NOT EXISTS " +
	           "(SELECT f FROM Movie f JOIN f.actors fa WHERE fa = a AND f = :movie)")
	public List<Artist> findArtistNotInMovie(Movie movie);
	
	public Artist findByDirectorOfMoviesContaining(Movie movie);
	
	public List<Artist> findByNameOrSurname(String name, String surname);
	
	public List<Artist> findByActorOfMoviesContaining(Movie movie);
}
