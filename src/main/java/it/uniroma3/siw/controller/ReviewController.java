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
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.validator.ReviewValidator;
import jakarta.validation.Valid;

@Controller
public class ReviewController {

	@Autowired
	ReviewValidator reviewValidator;
	@Autowired
	ReviewService reviewService;
	@Autowired
	MovieService movieService;
	@Autowired
	UserService userService;
	
	 @GetMapping("/formNewReview/{id}")
	 public String formNewReview(Model model, @PathVariable("id") Long id) {
		 model.addAttribute("review", new Review());
		 model.addAttribute("movie", this.movieService.findMovieById(id));
		 return "formNewReview.html";
	 }
	 
	 @PostMapping("/newReview/{movieId}/{userId}")
	 public String newReview(@Valid @ModelAttribute("review") Review review, BindingResult bindingResult, Model model,
			 @PathVariable("movieId") Long movieId, @PathVariable("userId") Long userId) {
		 
		this.reviewValidator.validate(review, bindingResult);
		Movie movie = this.movieService.findMovieById(movieId);
		User user = this.userService.findUserById(userId);
		
		if (!bindingResult.hasErrors()) {
			this.reviewService.setMovieAndWriter(review, user, movie);
			this.reviewService.saveReview(review);
			model.addAttribute("movie", movie);
			model.addAttribute("reviews", movie.getReviews());
			return "movie.html";
		}
		else {
			model.addAttribute("movie", movie);
			return "formNewReview.html";
		}
	 }

	 @GetMapping("/formModifyReview/{movieId}/{userId}")
	 public String formModifyReview (Model model, @PathVariable("movieId") Long movieId, @PathVariable("userId") Long userId  ) {
		 Movie movie = this.movieService.findMovieById(movieId);
		 User user = this.userService.findUserById(userId);
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
}
