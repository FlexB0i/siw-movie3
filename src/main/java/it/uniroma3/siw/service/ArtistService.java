package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import jakarta.transaction.Transactional;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;

	@Transactional
	public Artist saveArtist(Artist artist) {
		return this.artistRepository.save(artist);
	}

	@Transactional
	public List<Artist> findaArtistByNameAndSurname(String name, String surname) {
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
		this.artistRepository.deleteById(id);
	}

	@Transactional
	public void modifyArtist(Artist artist, String name, String surname, LocalDate birth, LocalDate death) {
		artist.setName(name);
		artist.setSurname(surname);
		artist.setBirth(birth);
		artist.setDeath(death);
	}
}
