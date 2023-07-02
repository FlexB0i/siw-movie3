package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;

public interface MovieRepository extends CrudRepository<Movie, Long>{

	public List<Movie> findByYear(Integer year);

	public boolean existsByTitleAndYear(String title, Integer year);

	public List<Movie> findByDirector(Artist director);
	
	public List<Movie> findByActorsContaining(Artist actor);
	
	public List<Movie> findAllByOrderByVoteDesc();
}
