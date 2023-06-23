package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class CredentialsService {

	@Autowired
	CredentialRepository credentialRepository;

	@Transactional
	public Credentials getCredentials(String username) {
		return credentialRepository.findByUsername(username).get();
	}

	@Transactional
	public Credentials getCredentials(Long id) {
		return credentialRepository.findById(id).get();
	}

	@Transactional
	public void saveCredentials(@Valid Credentials credentials) {
		credentialRepository.save(credentials);
	}

}
