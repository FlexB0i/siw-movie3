package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.codeJava.FileUploadUtil;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.validator.MovieValidator;
import jakarta.validation.Valid;

@Controller
public class MovieController {
  @Autowired MovieService movieService;
  @Autowired ArtistService artistService ;
  @Autowired MovieValidator movieValidator;
  @Autowired CredentialsService credentialsService;
  @Autowired GlobalController globalController;
  @Autowired ReviewService reviewService;
  
  // ********************************************** //
  // CONTROLLER PER RICHIESTE DI UN UTENTE GENERICO
  //********************************************** //

  @GetMapping("/indexMovie")
	public String indexMovie() {
		return "indexMovie.html";
	}

  @GetMapping("/formSearchMovies")
  public String formSearchMovies() {
    return "formSearchMovies.html";
  }

  @PostMapping("/searchMovies")
  public String searchMovies(Model model, @RequestParam Integer year) {
    model.addAttribute("movies", this.movieService.findMovieByYear(year));
    return "foundMovies.html";
  }
  
  @GetMapping("/movies")
  public String movies(Model model) {
	  model.addAttribute("movies", this.movieService.findAllMovies());
	  return "movies.html";
  }
  
  @GetMapping("/movie/{id}")
  public String movie(Model model,@PathVariable("id") Long id) {
	  Movie movie = this.movieService.findMovieById(id);
	  User user = this.credentialsService.getCredentials(globalController.getUser().getUsername()).getUser();
	  Review writtenReview = reviewService.findWrittenReview(movie, user);
	  
	  if (user != null) {		
		model.addAttribute("userReview", writtenReview);
	} else
		  model.addAttribute("userReview", null);
	  
	  List<Review> movieReviews = movie.getReviews();
	  movieReviews.remove(writtenReview);
	  
	  model.addAttribute("movie", movie);	  
	  model.addAttribute("reviews", movieReviews);
	  return "movie.html";
  }

  //************************************* //
  // CONTROLLER PER RICHIESTE DI UN ADMIN
  //************************************* //

  @GetMapping("/admin/indexMovieAdmin")
  public String getIndexMovieAdmin() {
	  return "admin/indexMovieAdmin.html";
  }

  @GetMapping("/admin/formNewMovie")
  public String formNewMovie(Model model) {
  model.addAttribute("movie", new Movie());
  return "admin/formNewMovie.html";
  }

  @PostMapping("/admin/newMovie")
  public String newMovie(@Valid @ModelAttribute("movie") Movie movie, @RequestParam("image") MultipartFile multipartFile, 
		  BindingResult bindingResult, Model model) throws IOException {
	  
	  this.movieValidator.validate(movie, bindingResult);
	  if (!bindingResult.hasErrors()) {
		  
		  String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		  movie.setImage(fileName);
		  
		  Movie savedMovie = this.movieService.saveMovie(movie);
		  
		  String uploadDir = "movie-image/" + savedMovie.getId();
		  FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		  
		  
		  return "redirect:/movie/" + movie.getId();
	  }
	  else {
		  return "admin/formNewMovie.html";
    }
  }
//
//  @GetMapping("/admin/manageMovies/{id}")
//  public String getMovie(@PathVariable("id") Long id, Model model) {
//    Movie movie = this.movieService.findMovieById(id);
//    if(movie!=null) {
//    	model.addAttribute("movie", movie);
//    	return "movie.html";
//    }
//    else
//    	return "movieError.html";
//  }

  @GetMapping("/admin/manageMovies")
  public String showMovies(Model model) {
    model.addAttribute("movies", this.movieService.findAllMovies());
    return "admin/manageMovies.html";
  }

  @GetMapping("/admin/formUpdateMovie/{id}")
  public String getUpadateMovie(@PathVariable("id") Long id, Model model ) {
	  Movie movie = this.movieService.findMovieById(id);
	  if(movie != null) {
		  model.addAttribute("movie", movie);
		  model.addAttribute("artists", movie.getActors());
		  return "admin/formUpdateMovie.html";
	  }
	  else
		  return "movieError.html";
  }

  @GetMapping("/admin/directorsToAdd/{id}")
  public String directorPerFilm (@PathVariable("id") Long id, Model model ) {
	  model.addAttribute("artists", this.artistService.findAllArtists());
	  model.addAttribute("movie", this.movieService.findMovieById(id));
	  return "admin/directorsToAdd.html";
  }

  @GetMapping("/admin/actorsToAdd/{id}")
  public String setActors (@PathVariable("id") Long id, Model model) {
	  Movie movie = this.movieService.findMovieById(id);

	  if(movie!=null) {
		  List<Artist> artistsNotInFilm = (List<Artist>) this.artistService.findAllArtists();
		  List<Artist> artistsInFilm = movie.getActors();

		  artistsNotInFilm.removeAll(artistsInFilm);

		  model.addAttribute("artistsNotInFilm", artistsNotInFilm);
		  model.addAttribute("artistsInFilm", artistsInFilm);
	  	model.addAttribute("movie", movie);
	  	return "admin/actorsToAdd.html";
	  }
	  else
		  return "movieError.html";
  }

  @GetMapping("/admin/removeActor/{movieId}/{actorId}")
  public String removeActor (@PathVariable("movieId") Long movieId, @PathVariable("actorId") Long actorId,  Model model ) {
	  Movie movie = this.movieService.findMovieById(movieId);
	  Artist actor = this.artistService.findArtistById(actorId);

	  if (movie != null) {
		  int indice = movie.getActors().indexOf(actor);
		  movie.getActors().remove(indice);
		  this.movieService.saveMovie(movie);

		  List<Artist> artistsNotInFilm = (List<Artist>) this.artistService.findAllArtists();
		  List<Artist> artistsInFilm = movie.getActors();

		  artistsNotInFilm.removeAll(artistsInFilm);

		  model.addAttribute("artistsNotInFilm", artistsNotInFilm);
		  model.addAttribute("artistsInFilm", artistsInFilm);
		  model.addAttribute("movie", movie);

		  return "admin/actorsToAdd.html";
	  }
	  else
		  return "movieError.html";
  }

  @GetMapping("/admin/addActor/{movieId}/{actorId}")
  public String addActor (@PathVariable("movieId") Long movieId, @PathVariable("actorId") Long actorId,  Model model ) {
	  Movie movie = this.movieService.findMovieById(movieId);
	  Artist actor = this.artistService.findArtistById(actorId);

	  if(movie!=null) {
		  movie.getActors().add(actor);
		  this.movieService.saveMovie(movie);

		  List<Artist> artistsNotInFilm = (List<Artist>) this.artistService.findAllArtists();
		  List<Artist> artistsInFilm = movie.getActors();

		  artistsNotInFilm.removeAll(artistsInFilm);

		  model.addAttribute("artistsNotInFilm", artistsNotInFilm);
		  model.addAttribute("artistsInFilm", artistsInFilm);
		  model.addAttribute("movie", movie);

		  return "admin/actorsToAdd.html";
	  }
	  else
		  return "movieError.html";
  }

  @GetMapping("/admin/setDirectorToMovie/{directorId}/{movieId}")
  public String setDirector (@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model ) {
	  Movie movie = this.movieService.setDirectorToMovie(movieId, directorId);
	  model.addAttribute("movie", movie);
	  model.addAttribute("artists", movie.getActors());
	  return "admin/formUpdateMovie.html";
  }
  
  @GetMapping ("/admin/deleteMovie/{id}")
  public String deleteMovie (@PathVariable("id") Long id, Model model) {
	  this.movieService.deleteMovie(id);
	  model.addAttribute("movies", this.movieService.findAllMovies());
	  return "admin/manageMovies.html";
  }
  
  @PostMapping ("/admin/modifyMovie/{id}")
  public String modifyMovie (Model model, @PathVariable("id") Long id, 
		  @RequestParam String title, @RequestParam Integer year) throws IOException {
	  
	  Movie movie = this.movieService.findMovieById(id);
	  if (movie == null)
		  return "movieError.html";
	  this.movieService.modifyMovie(movie, title, year);

	  return "redirect:/movie/" + id;
  }
}
