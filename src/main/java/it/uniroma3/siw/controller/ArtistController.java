package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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
		public String indexArtist(Model model) {
		  model.addAttribute("artists", this.artistService.findAllArtists());
		  return "indexArtist.html";
		}

	  @GetMapping("/artist/{id}")
	  public String getArtist(@PathVariable("id") Long id, Model model) {
	    Artist artist = this.artistService.findArtistById(id);
	    if ( artist != null) {
	    	model.addAttribute("artist", artist);
	    	model.addAttribute("moviesDirected", artist.getDirectorOfMovies());
	    	model.addAttribute("moviesRecited", artist.getActorOfMovies());
	    	return "artist.html";
	    }
	    else
			  return "artistError.html";
	  }

	  @PostMapping("/searchArtists")
	  public String searchArtists(Model model, @RequestParam("name") String completeArtistName) {
		  
		String[] NameSurname = completeArtistName.split(" ");
		List<Artist> foundArtists = new ArrayList<>();
		
		String finalName = completeArtistName;
		String finalSurname = "";
		
		
		if(NameSurname.length == 2) {
			String name = NameSurname[0];
			String surname = NameSurname[1];
			
			finalName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
			finalSurname = surname.substring(0, 1).toUpperCase() + surname.substring(1).toLowerCase();
			
			foundArtists = this.artistService.findArtistByNameAndSurname(finalName, finalSurname);
			if(foundArtists.isEmpty()) {
				foundArtists = this.artistService.findArtistByNameAndSurname(finalSurname, finalName);
			}	
		}else if(NameSurname.length == 1){

			String name = NameSurname[0];
			finalName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
			foundArtists = this.artistService.findArtistByNameOrSurname(finalName, finalName);
		}
		model.addAttribute("name", finalName);
		model.addAttribute("surname", finalSurname);
		model.addAttribute("artists", foundArtists);
		return "foundArtists.html";
	  }

	  //************************************* //
	  // CONTROLLER PER RICHIESTE DI UN ADMIN
	  //************************************* //

	  @GetMapping("/admin/indexArtistAdmin")
	  public String getIndexMovieAdmin(Model model) {
		  model.addAttribute("artists", this.artistService.findAllArtists());
		  return "admin/indexArtistAdmin.html";
	  }

	  @GetMapping("/admin/formNewArtist")
	    public String formNewArtist(Model model) {
	    model.addAttribute("artist", new Artist());
	    return "admin/formNewArtist.html";
	  }

	  @PostMapping("/admin/newArtist")
	  public String newArtist(@ModelAttribute("artist") Artist artist, @RequestParam("imageFile") MultipartFile multipartFile,
			  BindingResult bindingResult, Model model){
		  
		  this.artistValidator.validate(artist, bindingResult);
		  if (!bindingResult.hasErrors() || !multipartFile.isEmpty()) {
			  
			  String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			  this.artistService.inizializeArtist(artist, fileName);
			  
			  Artist savedArtist = this.artistService.saveArtist(artist);
			  
			  String uploadDir = "./artist-photo/" + savedArtist.getId();
			  
			  try {
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			  
			  model.addAttribute("artists", this.artistService.findAllArtists()); 
			  return "/admin/indexArtistAdmin.html" ;
		  }
		  else {
			  if(multipartFile.isEmpty()) {
				  model.addAttribute("imageMissed", true);
				  return "admin/formNewArtist.html";
			  }
			  else
				  return "admin/formNewArtist.html";
	    }
	  }
	  
	  @GetMapping ("/admin/deleteArtist/{id}")
	  public String deleteArtist(Model model, @PathVariable("id") Long id) {
		  this.artistService.deleteArtist(id);
		  model.addAttribute("artists", this.artistService.findAllArtists());
		  return "admin/indexArtistAdmin.html";
	  }

	  @PostMapping ("/admin/modifyArtist/{id}")
	  public String modifyArtist (Model model, @PathVariable("id") Long id,
			  @RequestParam("name") String name, @RequestParam("surname") String surname, 
			  @RequestParam("birth") LocalDate birth, @RequestParam("death") LocalDate death,
			  @RequestParam("imageFile") MultipartFile multipartFile) throws IOException {
		  
		  Artist artist = this.artistService.findArtistById(id);
		  if (artist == null)
			  return "artistError.html";
		  
		  this.artistService.modifyArtist(artist, name, surname, birth, death);
		  
		  if(!multipartFile.isEmpty()) {
			  
			  String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			  artist.setPhoto(fileName);
			  
			  String uploadDir = "./artist-image/" + artist.getId();
			  
			  try {
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }
		  
		  this.artistService.saveArtist(artist);
		  
		  model.addAttribute("artists", this.artistService.findAllArtists()); 
		  return "/admin/indexArtistAdmin.html" ;
	  }

	}

