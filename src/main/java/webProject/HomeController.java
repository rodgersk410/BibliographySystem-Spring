package webProject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import CitationFormatting.CitationStyleGenerator;
import CitationFormatting.CitationStyleOutputFormat;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXParser;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


@Controller
public class HomeController {
	
	@Autowired
	private DatabaseHelper dbhelper;
	
	//homepage	
    @RequestMapping("/biblio")
    public String getHomepage(Model model) {
		List<BibE> entries = dbhelper.getAllEntries();
        model.addAttribute("entries", entries);
        return "bibliography";
    }

    //add entry
    @RequestMapping("/addEntry")
    public String addEntry(@RequestParam(value="author", required=true) String a,
            @RequestParam(value="title", required=true) String t,
            @RequestParam(value="year", required=true) int y,
            @RequestParam(value="journal", required=true) String j,
            Model model) {
        jdbcTemplate.update("insert into entries (author, title, year, journal) "
        		+ "values (?, ?, ?, ?)", a, t, y, j);
        return "redirect:biblio";
    }
    
    //delete entry
    @RequestMapping("/deleteEntry/{id}")
    public String deleteEntry(@PathVariable(value="id", required=false) int id, Model model) {
        jdbcTemplate.update("delete from entries where id = ?", id);
        return "redirect:/biblio";
    }
    
    //edit entry
    @RequestMapping("/editEntry/{id}")
    public String greeting(Model model, @PathVariable(value="id", required=false) String id) {
		List<BibE> entries = dbhelper.getSelectedEntries(id);
        model.addAttribute("entry", entries.get(0));
        return "editEntry";
    }

    //save edited entry
    @RequestMapping("/saveEntry/{id}")
    public String saveEntry(@RequestParam(value="author", required=true) String a,
            @RequestParam(value="title", required=true) String t,
            @RequestParam(value="year", required=true) int y,
            @RequestParam(value="journal", required=true) String j,
            @RequestParam(value="id", required=false) String id,
            Model model) {
        jdbcTemplate.update("update entries set author=?, title=?, year=?, journal=? "
        		+ "where id = ?", a, t, y, j, id);
        return "redirect:/biblio"; // back to the biblio view
    }
    
    //import bibtex files
    @RequestMapping(value = "/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
    	BibEntryImporter importer = new BibEntryImporter();
    	List<BibE> beList = importer.entryImporter(file);
    	
    	for(BibE entry : beList) {
    		jdbcTemplate.update("insert into entries (author, title, year, journal) "
    				+ "values (?, ?, ?, ?)", entry.getAuthor(), entry.getTitle(), entry.getYear(), entry.getJournal());
    	}    	
    	
        return "redirect:/biblio"; // back to the biblio view
    }
	
    //export selected entry
    @RequestMapping(value="/modifySelected", params="action=Export Selected")
    @ResponseBody void exportSelected(HttpServletResponse response, @RequestParam(value="myCheck", required=true) String id,
			@RequestParam(value="action", required=true) String action,
			Model model) throws IOException {

    	List<BibE> entries = dbhelper.getSelectedEntries(id);
    	BibEntryExporter exporter = new BibEntryExporter();
    	exporter.exportToFile(response, entries);
    }
    
    //delete selected entry
    @RequestMapping(value="/modifySelected", params="action=Delete Selected")
    public String deleteSelected(@RequestParam(value="myCheck", required=true) String id,
    								@RequestParam(value="action", required=true) String action,
    								Model model) {    	
        jdbcTemplate.update("delete from bibliographies.entries where id in (" + id + ")");
        return "redirect:biblio"; // back to the biblio view
    }
    
    //view formatted selected entry
    @RequestMapping(value="/modifySelected", params="action=View Selected in IEEE")
    public String viewFormattedSelected(@RequestParam(value="myCheck", required=true) String id,
    								@RequestParam(value="action", required=true) String action,
    								Model model,
    								RedirectAttributes redir) {

    	List<BibE> formattedEntries = dbhelper.getSelectedEntries(id);
		String ieeeStyleFile = "ieee.csl";
		List<String> iEeeFormattedEntries = CitationStyleGenerator.generateCitations(formattedEntries, 
				ieeeStyleFile, CitationStyleOutputFormat.TEXT);
		//TODO: needs refactoring so that the index number on the entry is in ascending order
		
		redir.addFlashAttribute("formattedEntries", iEeeFormattedEntries);
        return "redirect:biblio"; // back to the biblio view
    }
    
    //search Ieee db
    List<BibE> searchEntries = new ArrayList<BibE>();
    @RequestMapping(value="/searchIeeeDb", params="action=Search Ieee")
    public String searchIeeeDb(@RequestParam(value="action", required=true) String action,
						    		@RequestParam(value="author", required=false) String a,
						            @RequestParam(value="title", required=false) String t,
						            @RequestParam(value="journal", required=false) String j,
						            @RequestParam(value="year", required=false) String y,
    								Model model,
    								RedirectAttributes redir) {
    	
    	IeeeEntries ieEntries = new IeeeEntries();
    	searchEntries = ieEntries.retrieveIeeeEntries(a, t, j, y);
        
		redir.addFlashAttribute("IeeeEntries", searchEntries);        
        return "redirect:biblio"; // back to the biblio view
    }
    
    //import Ieee records
    @RequestMapping(value="/searchIeeeDb", params="action=Import Selected")
    public String importIeeeSelected(@RequestParam(value="myCheck", required=true) String id,
    								Model model) {
    	
    	List<String> items = Arrays.asList(id.split("\\s*,\\s*"));
    	List<BibE> selectedBiblioEntries = new ArrayList<BibE>();
    	
    	for(int i=0; i<items.size(); i++) {
    		selectedBiblioEntries.add(searchEntries.get(Integer.parseInt(items.get(i))));
    	}
    	
		for(BibE entry : selectedBiblioEntries){
	        jdbcTemplate.update("insert into entries (author, title, year, journal) "
	        		+ "values (?, ?, ?, ?)", entry.getAuthor(), entry.getTitle(),
	        		entry.getYear(), entry.getJournal());
		}
    			
        return "redirect:biblio"; // back to the biblio view
    }
    
    @Autowired
	JdbcTemplate jdbcTemplate;
    
}