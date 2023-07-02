package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.validator.ReviewValidator;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

	@Autowired
	GlobalController globalController;
	@Autowired
	ReviewValidator reviewValidator;
	@Autowired
	ReviewService reviewService;
	@Autowired
	MovieService movieService;
	@Autowired
	UserService userService;
	@Autowired
	CredentialsService credentialsService;
	
	 @PostMapping("/newReview/{movieId}")
	 public String newReview(@Valid @ModelAttribute("review") Review review, BindingResult bindingResult, Model model,
			 @PathVariable("movieId") Long movieId) {
		 
		String userUsername = globalController.getUser().getUsername();
		Movie movie = this.movieService.findMovieById(movieId);
		User user = this.credentialsService.getCredentials(userUsername).getUser();
		
		this.reviewService.setMovieAndWriter(review, user, movie, userUsername);
		
		user.getReviews().add(review);
		movie.getReviews().add(review);
			
		this.movieService.setVote(movie,review.getVote());
			
		this.movieService.saveMovie(movie);
		this.userService.saveUser(user);
		this.reviewService.saveReview(review);
		return "redirect:/movie/" + movieId;
	 }

	 
	 @PostMapping("/modifyReview/{id}")
	 public String modifyReview (Model model,@PathVariable("id") Long id, @RequestParam String title, @RequestParam Integer vote, @RequestParam String text) {
		
		 Review review = this.reviewService.findReviewById(id);
		 Movie movie = review.getMovieReviewed();
		 this.movieService.deleteOldVote(review.getVote(), movie);
		 
		 this.reviewService.modifyReview(review, title, vote, text);
		 this.reviewService.saveReview(review);
		 this.movieService.setVote(movie, review.getVote());
		 this.movieService.saveMovie(movie);
		
		 
		 return "redirect:/movie/" + movie.getId();
	 }
	 
	 @GetMapping("/deleteReview/{reviewId}/{movieId}")
	 public String deleteReview(Model model, @PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId) {
		 User user = this.credentialsService.getCredentials(globalController.getUser().getUsername()).getUser();
		 Review review = this.reviewService.findReviewById(reviewId);
		 Movie movie = review.getMovieReviewed();
		 
		 this.movieService.deleteOldVote(review.getVote(), movie);
		 this.reviewService.deleteReview(reviewId, movieId, user);
		 this.movieService.saveMovie(movie);
		 return "redirect:/movie/" + movieId;
	 }
}
