package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialRepository;
import jakarta.transaction.Transactional;

@Service
public class CredentialsService {

	@Autowired
	CredentialRepository credentialRepository;
	@Autowired
    protected PasswordEncoder passwordEncoder;

	@Transactional
	public Credentials getCredentials(String username) {
		return credentialRepository.findByUsername(username).get();
	}

	@Transactional
	public Credentials getCredentials(Long id) {
		return credentialRepository.findById(id).get();
	}

	@Transactional
    public Credentials saveCredentials(Credentials credentials) {
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return this.credentialRepository.save(credentials);
    }

}
