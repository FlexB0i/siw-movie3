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
	
	 @GetMapping("/formNewReview/{id}")
	 public String formNewReview(Model model, @PathVariable("id") Long id) {
		 model.addAttribute("review", new Review());
		 model.addAttribute("movie", this.movieService.findMovieById(id));
		 return "formNewReview.html";
	 }
	 
	 @PostMapping("/newReview/{movieId}")
	 public String newReview(@Valid @ModelAttribute("review") Review review, BindingResult bindingResult, Model model,
			 @PathVariable("movieId") Long movieId) {
		 
		String userUsername = globalController.getUser().getUsername();
		Movie movie = this.movieService.findMovieById(movieId);
		User user = this.credentialsService.getCredentials(userUsername).getUser();
		
		this.reviewService.setMovieAndWriter(review, user, movie, userUsername);
		this.reviewValidator.validate(review, bindingResult);
		
		if (!bindingResult.hasErrors()) {
			user.getReviews().add(review);
			movie.getReviews().add(review);
			this.movieService.saveMovie(movie);
			this.userService.saveUser(user);
			this.reviewService.saveReview(review);
			return "redirect:/movie/" + movieId;
		}
		else {
			model.addAttribute("movie", movie);
			return "formNewReview.html";
		}
	 }

	 @GetMapping("/formModifyReview/{movieId}")
	 public String formModifyReview (Model model, @PathVariable("movieId") Long movieId) {
		 Movie movie = this.movieService.findMovieById(movieId);
		 User user = this.credentialsService.getCredentials(globalController.getUser().getUsername()).getUser();
		 Review review = this.reviewService.findWrittenReview(movie, user);
		 
		 if (review != null) {
			model.addAttribute("review", review);
			return "formModifyReview.html";
		 }
		 return "reviewError.html";
	 }
	 
	 @PostMapping("/modifyReview/{id}")
	 public String modifyReview (Model model,@PathVariable("id") Long id, @RequestParam String title, @RequestParam Integer vote, @RequestParam String text) {
		 Review review = this.reviewService.findReviewById(id);
		 
		 this.reviewService.modifyReview(review, title, vote, text);
		 
		 this.reviewService.saveReview(review);
		 
		 Movie movieReviewed = review.getMovieReviewed();
		 model.addAttribute("movie", movieReviewed);
		 model.addAttribute("reviews", movieReviewed.getReviews());
		 
		 return "movie.html";
	 }
	 
	 @GetMapping("/deleteReview/{reviewId}/{movieId}")
	 public String deleteReview(Model model, @PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId) {
		 User user = this.credentialsService.getCredentials(globalController.getUser().getUsername()).getUser();
		 this.reviewService.deleteReview(reviewId, movieId, user);
		 return "redirect:/movie/" + movieId;
	 }
}
