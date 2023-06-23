package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.LocalDate;

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
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.validator.ArtistValidator;

@Controller
public class ArtistController {
	@Autowired ArtistService artistService;
	@Autowired ArtistValidator artistValidator;


	  // ********************************************** //
	  // CONTROLLER PER RICHIESTE DI UN UTENTE GENERICO
	  //********************************************** //

	  @GetMapping("/indexArtist")
		public String indexArtist() {
			return "indexArtist.html";
		}

	  @GetMapping("/artist/{id}")
	  public String getArtist(@PathVariable("id") Long id, Model model) {
	    Artist artist = this.artistService.findArtistById(id);
	    if ( artist != null) {
	    	model.addAttribute("artist", artist);
	    	return "artist.html";
	    }
	    else
			  return "artistError.html";
	  }

	  @GetMapping("/artists")
	  public String showArtists(Model model) {
	    model.addAttribute("artists", this.artistService.findAllArtists());
	    return "artists.html";
	  }

	  @GetMapping("/formSearchArtists")
	  public String formSearchArtists() {
	    return "formSearchArtists.html";
	  }

	  @PostMapping("/searchArtists")
	  public String searchArtists(Model model, @RequestParam String name, @RequestParam String surname) {
	    model.addAttribute("artists", this.artistService.findaArtistByNameAndSurname(name,surname));
	    return "foundArtists.html";
	  }

	  //************************************* //
	  // CONTROLLER PER RICHIESTE DI UN ADMIN
	  //************************************* //

	  @GetMapping("/admin/indexArtistAdmin")
	  public String getIndexMovieAdmin() {
		  return "admin/indexArtistAdmin.html";
	  }

	  @GetMapping("/admin/formNewArtist")
	    public String formNewArtist(Model model) {
	    model.addAttribute("artist", new Artist());
	    return "admin/formNewArtist.html";
	  }

	  @PostMapping("/admin/newArtist")
	  public String newArtist(@ModelAttribute("artist") Artist artist, @RequestParam("photo") MultipartFile multipartFile,
			  BindingResult bindingResult, Model model) throws IOException {
		  
		  this.artistValidator.validate(artist, bindingResult);
		  if (!bindingResult.hasErrors()) {
			  
			  String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			  artist.setPhoto(fileName);
			  
			  Artist savedArtist = this.artistService.saveArtist(artist);
			  
			  String uploadDir = "artist-photo/" + savedArtist.getId();
			  FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			  
			  model.addAttribute("artist", artist);
			  return "artist.html";
		  }
		  else {
			  return "admin/formNewArtist.html";
	    }
	  }
	  
	  @GetMapping ("/admin/manageArtists")
	  public String manageArtists(Model model) {
		  model.addAttribute("artists", this.artistService.findAllArtists());
		  return "admin/manageArtists.html";
	  }
	  
	  @GetMapping ("/admin/deleteArtist/{id}")
	  public String deleteArtist(Model model, @PathVariable("id") Long id) {
		  this.artistService.deleteArtist(id);
		  model.addAttribute("artists", this.artistService.findAllArtists());
		  return "admin/manageArtists.html";
	  }

	  @PostMapping ("/admin/modifyArtist/{id}")
	  public String modifyArtist (Model model, @PathVariable("id") Long id,
			  @RequestParam String name, @RequestParam String surname, @RequestParam LocalDate birth, @RequestParam LocalDate death,
			  @RequestParam MultipartFile photo) throws IOException {
		  
		  Artist artist = this.artistService.findArtistById(id);
		  if (artist == null)
			  return "artistError.html";
		  
		  this.artistService.modifyArtist(artist, name, surname, birth, death);
		  
		  if (photo != null) {
			  String fileName = StringUtils.cleanPath(photo.getOriginalFilename());
			  artist.setPhoto(fileName);
			  
			  Artist savedArtist = this.artistService.saveArtist(artist);
			  
			  String uploadDir = "artist-photo/" + savedArtist.getId();
			  FileUploadUtil.saveFile(uploadDir, fileName, photo);
		  }
		  else
			  this.artistService.saveArtist(artist);
		
		  model.addAttribute("artist", artist);
		  return "artist.html";
	  }

	}

