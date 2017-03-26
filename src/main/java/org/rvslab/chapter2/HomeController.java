package org.rvslab.chapter2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
	
	List bibliographyList = new ArrayList();
	
    @GetMapping("/")
    public String bibliographyForm(Model model) {
        model.addAttribute("bibliography", new Bibliography());
        return "bibliography";
    }

    @PostMapping("/")
    public @ResponseBody List bibliographySubmit(@ModelAttribute Bibliography bibliography) {
    	bibliographyList.add(bibliography);
    	return bibliographyList;
    }
    
    @GetMapping("/GetAllBibliographies")
    public @ResponseBody List getAllBibliographies(Model model) {
        return bibliographyList;
    }
    
}