package it.uniroma3.siw.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Credentials {

	public static final String ADMIN_ROLE = "admin";

	public static final String DEFAULT_ROLE = "deafault";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@NotNull
	String username;
	@NotNull
	String password;
	String role;
	@OneToOne(cascade = CascadeType.ALL)
	User userReferenciated;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public User getUser() {
		return userReferenciated;
	}
	public void setUser(User user) {
		this.userReferenciated = user;
	}
	public User getUserReferenciated() {
		return userReferenciated;
	}
	public void setUserReferenciated(User userReferenciated) {
		this.userReferenciated = userReferenciated;
	}

	

}
