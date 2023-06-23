package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Artist;

public interface ArtistRepository extends CrudRepository<Artist, Long>{

	public boolean existsByNameAndSurname (String name, String Surname);
	public List<Artist> findByNameAndSurname(String name, String Surname);
//	@Query()
//	public List<Artist> findArtistNotInMovie(Long movieId);
}
