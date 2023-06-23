package it.uniroma3.siw.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Review;

@Component
public class ReviewValidator implements Validator{

	@Override
	public void validate(Object o, Errors errors) {
		Review review = (Review) o;
		
		for (Review r : review.getMovieReviewed().getReviews()) {
			if (review.getWriter().equals(r.getWriter()))
				errors.reject("review.duplicate");
		}
		
	}
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Review.class.equals(aClass);
	}
}
