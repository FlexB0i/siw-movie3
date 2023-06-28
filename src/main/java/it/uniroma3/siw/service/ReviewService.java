package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class ReviewService {

	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	MovieService MovieService;
	@Autowired
	UserRepository userRepository;
	
	@Transactional
	public void saveReview (Review review) {
		this.reviewRepository.save(review);
	}
	
	@Transactional
	public Review findReviewById(Long id) {
		return this.reviewRepository.findById(id).orElse(null);
	}
	
	@Transactional
	public void setMovieAndWriter (Review review, User user, Movie movie, String userusername) {
		review.setMovieReviewed(movie);
		review.setWriter(user);
		review.setUserUsername(userusername);
	}
	
	@Transactional
	public Review findWrittenReview (Movie movie, User user) {
		for (Review r : movie.getReviews()) {
			 if (r.getWriter().equals(user)) {
				return r;
			 }
		}
		return null;
	}
	
	@Transactional
	public void modifyReview (Review review, String title, int vote, String text) {
		review.setTitle(title);
		review.setVote(vote);
		review.setText(text);
	}

	@Transactional
	public void deleteReview(Long reviewId, Long movieId, User user) {
		
		Movie movie = this.MovieService.findMovieById(movieId);
		Review removedReview = this.reviewRepository.findById(reviewId).get();
		
		movie.getReviews().remove(removedReview);
		this.MovieService.saveMovie(movie);
		
		user.getReviews().remove(removedReview);
		this.userRepository.save(user);
		
		this.reviewRepository.deleteById(reviewId);
	}
}
	

