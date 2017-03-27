package org.rvslab.chapter2;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
	
	List bibliographyList = new ArrayList();
	
	//home page 
    @GetMapping("/")
    public String bibliographyForm(Bibliography bibliography, Model model) {
    	model.addAttribute("bibliographyList", bibliographyList);
        return "bibliography";
    }
    
    //add bibliography
    @PostMapping("/AddBibliography")
    public @ResponseBody List bibliographySubmit(Bibliography bibliography, BindingResult bindingResult, Model model) {
    	bibliographyList.add(bibliography);
    	return bibliographyList;
    }
    
    //get Bibliographies
    @GetMapping("/GetAllBibliographies")
    public @ResponseBody List getAllBibliographies(Model model) {
        return bibliographyList;
    }
    
    //delete user
    @PostMapping(value="DeleteBibliography")
    public String deleteBibliography(@RequestParam Long id) {
    	bibliographyList.remove(id);
        return "redirect:/bibliography";
    }
    
}