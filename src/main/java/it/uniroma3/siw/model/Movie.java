package it.uniroma3.siw.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String title;

	@NotNull
	@Min(1900)
	@Max(2023)
    private Integer year;
	
	@Column(length = 64)
    private String image;

    @ManyToOne
    private Artist director;
    
    @ManyToMany
    private List<Artist> actors;
    
    @OneToMany (mappedBy = "movieReviewed")
    private List<Review> reviews;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String urlImage) {
		this.image = urlImage;
	}
	public Artist getDirector() {
		return director;
	}
	public void setDirector(Artist director) {
		this.director = director;
	}
	public List<Artist> getActors() {
		return actors;
	}
	public void setActors(List<Artist> actor) {
		this.actors = actor;
	}
	public List<Review> getReviews() {
		return reviews;
	}
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	@Transient
	public String getImagePath() {
		if(this.image == null || id == null) return null;
		
		return "/movie-image/" + this.id + "/" + this.image;
	}
	
	
}
