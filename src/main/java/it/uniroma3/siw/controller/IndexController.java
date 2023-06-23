package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	@GetMapping("/index.html")
	public String index() {
		return "index.html";
	}

	@GetMapping("/admin/indexAdmin.html")
	public String indexAdmin() {
		return "indexAdmin().html";
	}
}
