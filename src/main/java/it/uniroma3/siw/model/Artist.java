package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Artist {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String name;
	@NotBlank
	private String surname;
	@NotNull
	private LocalDate birth;
	
	private LocalDate death;
	
	@Column(length = 64)
    private String photo;

	@OneToMany(mappedBy = "director")
	private List<Movie> directorOfMovies;
	
	@ManyToMany(mappedBy = "actors")
	private List<Movie> actorOfMovies;

	
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	
	public LocalDate getBirth() {
		return birth;
	}
	public void setBirth(LocalDate birth) {
		this.birth = birth;
	}
	public LocalDate getDeath() {
		return death;
	}
	public void setDeath(LocalDate death) {
		this.death = death;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Movie> getDirectorOfMovies() {
		return directorOfMovies;
	}
	public void setDirectorOfMovies(List<Movie> directorOfMovies) {
		this.directorOfMovies = directorOfMovies;
	}
	public List<Movie> getActorOfMovies() {
		return actorOfMovies;
	}
	public void setActorOfMovies(List<Movie> actorOfMovies) {
		this.actorOfMovies = actorOfMovies;
	}
	
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	@Transient
	public String getPhotoPath() {
		if(this.photo == null || this.id == null) return null;
		
		return "/artist-photo/" + this.id + "/" + this.photo;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(name, other.name) ;
	}

}