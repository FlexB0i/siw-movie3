package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
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

  private void inizializeIndexMovie(Model model) {
	  List <Movie> allMoviesOrderedByVote = (List<Movie>) this.movieService.findAllMoviesOrderedByVote();
	  if (allMoviesOrderedByVote.isEmpty()) {
		  model.addAttribute("firstMovie", null);
		  model.addAttribute("secondMovie", null);
		  model.addAttribute("thirdMovie", null);
	  }
	  else if(allMoviesOrderedByVote.size()==1) {
		  model.addAttribute("firstMovie", allMoviesOrderedByVote.get(0));
		  model.addAttribute("secondMovie", null);
		  model.addAttribute("thirdMovie", null);
	  }
	  else if(allMoviesOrderedByVote.size()==2) {
		  model.addAttribute("firstMovie", allMoviesOrderedByVote.get(0));
		  model.addAttribute("secondMovie", allMoviesOrderedByVote.get(1));
		  model.addAttribute("thirdMovie", null);
	  }
	  else {
		  model.addAttribute("firstMovie", allMoviesOrderedByVote.get(0));
		  model.addAttribute("secondMovie", allMoviesOrderedByVote.get(1));
		  model.addAttribute("thirdMovie", allMoviesOrderedByVote.get(2));
	  }
	  model.addAttribute("movies", allMoviesOrderedByVote);
  }
  
  private void inizializeAddActor(Movie movie, Model model) {
	  List<Artist> artistsNotInFilm = this.artistService.findAllArtistNotInMovie(movie);
	  List<Artist> artistsInFilm = movie.getActors();
	  
	  model.addAttribute("artistsNotInFilm", artistsNotInFilm);
	  model.addAttribute("artistsInFilm", artistsInFilm);
	  model.addAttribute("movie",movie);
  }
  
  @GetMapping("/indexMovie")
	public String indexMovie(Model model) {
	  this.inizializeIndexMovie(model);
	  return "indexMovie.html";
	}

  @PostMapping("/searchMovies")
  public String searchMovies(Model model, @RequestParam Integer year) {
	model.addAttribute("year", year);
    model.addAttribute("movies", this.movieService.findMovieByYear(year));
    return "foundMovies.html";
  }

  @GetMapping("/movie/{id}")
  public String movie(Model model,@PathVariable("id") Long id) {
	  Movie movie = this.movieService.findMovieById(id);
	  
	  List<Review> movieReviews = movie.getReviews();
	  
	  UserDetails userDetails = globalController.getUser();
	  if( userDetails == null) {
		  model.addAttribute("userReview", null);
	  }
	  else {
		  User user = this.credentialsService.getCredentials(userDetails.getUsername()).getUser();
		  Review writtenReview = reviewService.findWrittenReview(movie, user);
		  	
		  model.addAttribute("userReview", writtenReview);
		  movieReviews.remove(writtenReview);
	  }
	  
	  model.addAttribute("review", new Review());
	  model.addAttribute("movie", movie);	
	  model.addAttribute("actors", movie.getActors());
	  model.addAttribute("reviewNumber", movie.getNumOfVote());
	  model.addAttribute("reviews", movieReviews);
	  return "movie.html";
  }

  //************************************* //
  // CONTROLLER PER RICHIESTE DI UN ADMIN
  //************************************* //

  @GetMapping("/admin/movieAdmin/{id}")
  public String getMovieAdmin(Model model,@PathVariable("id") Long id) {
	  Movie movie = this.movieService.findMovieById(id);
	  
	  List<Review> movieReviews = movie.getReviews();
	  
	  UserDetails userDetails = globalController.getUser();
	  if( userDetails == null) {
		  model.addAttribute("userReview", null);
	  }
	  else {
		  User user = this.credentialsService.getCredentials(userDetails.getUsername()).getUser();
		  Review writtenReview = reviewService.findWrittenReview(movie, user);
		  	
		  model.addAttribute("userReview", writtenReview);
		  movieReviews.remove(writtenReview);
	  }
	  
	  model.addAttribute("review", new Review());
	  model.addAttribute("movie", movie);	
	  model.addAttribute("actors", movie.getActors());
	  model.addAttribute("reviewNumber", movie.getNumOfVote());
	  model.addAttribute("reviews", movieReviews);
	  return "admin/movieAdmin.html";
  }
  
  @GetMapping("/admin/indexMovieAdmin")
  public String getIndexMovieAdmin(Model model) {
	  this.inizializeIndexMovie(model);
	  model.addAttribute("artists", this.artistService.findAllArtists());
	  return "admin/indexMovieAdmin.html";
  }
  
  @GetMapping("/admin/formNewMovie")
  public String formNewMovie(Model model) {
	  model.addAttribute("artists", this.artistService.findAllArtists());
	  model.addAttribute("movie", new Movie());
	  return "admin/formNewMovie.html";
  }

  @PostMapping("/admin/newMovie")
  public String newMovie(@Valid @ModelAttribute("movie") Movie movie, @RequestParam("imageFile") MultipartFile multipartFile, 
		  @RequestParam("artistId") Long ArtistId,
		  BindingResult bindingResult, Model model){
	  
	  this.movieValidator.validate(movie, bindingResult);
	  if (!bindingResult.hasErrors() || !multipartFile.isEmpty()) {
		  
		  String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		  
		  this.movieService.inizializeMovie(movie, fileName, this.artistService.findArtistById(ArtistId) );
		  Movie savedMovie = this.movieService.saveMovie(movie);
		  
		  String uploadDir = "./movie-image/" + savedMovie.getId();
		  
		  try {
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
		  this.inizializeAddActor(movie, model);
		  return "admin/addActor.html";
	  }
	  else {
		  if(multipartFile.isEmpty()) {
			  model.addAttribute("imageMissed", true);
			  return "admin/formNewMovie.html";
		  }
		  else
			  return "admin/formNewMovie.html";
    }
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

  @GetMapping("/admin/removeActor/{movieId}/{actorId}")
  public String removeActor (@PathVariable("movieId") Long movieId, @PathVariable("actorId") Long actorId,  Model model ) {
	  Movie movie = this.movieService.findMovieById(movieId);
	  Artist actor = this.artistService.findArtistById(actorId);

	  if (movie != null) {
		  int indice = movie.getActors().indexOf(actor);
		  movie.getActors().remove(indice);
		  this.movieService.saveMovie(movie);

		  this.inizializeAddActor(movie, model);

		  return "admin/addActor.html";
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

		  this.inizializeAddActor(movie, model);

		  return "admin/addActor.html";
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
	  this.inizializeIndexMovie(model);
	  return "admin/indexMovieAdmin.html";
  }
  
  @PostMapping ("/admin/modifyMovie/{id}")
  public String modifyMovie (Model model, @PathVariable("id") Long id, 
		  @RequestParam("title") String title, @RequestParam("year") Integer year,
		  @RequestParam("imageFile") MultipartFile multipartFile, @RequestParam("artistId") Long artistId ) throws IOException {
	  
	  Movie movie = this.movieService.findMovieById(id);
	  
	  if (movie == null)
		  return "movieError.html";
	  
	  this.movieService.modifyMovie(movie, title, year, this.artistService.findArtistById(artistId));

	  if(!multipartFile.isEmpty()) {
		  
		  String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		
		  movie.setImage(fileName);
		  
		  String uploadDir = "./movie-image/" + movie.getId();
		  
		  try {
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	  
	  this.movieService.saveMovie(movie);

	  this.inizializeAddActor(movie, model);
	  return "admin/addActor.html";
  }
}
