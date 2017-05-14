package webProject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import CitationFormatting.CitationStyleGenerator;
import CitationFormatting.CitationStyleOutputFormat;
import IeeeApiFetch.ApiUrl;
import IeeeApiFetch.IApiUrl;
import IeeeApiFetch.IeAuthorParam;
import IeeeApiFetch.IeJournalParam;
import IeeeApiFetch.IeNumResults;
import IeeeApiFetch.IeTitleParam;
import IeeeApiFetch.IeYearParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Controller
public class HomeController {

	@Autowired
	private DatabaseHelper dbHelper;

	// load homepage
	@RequestMapping("/biblio")
	public String getHomepage(Model model) {
		List<BibE> entries = dbHelper.getAllEntries();
		model.addAttribute("entries", entries);
		return "bibliography";
	}

	// add entry
	@RequestMapping("/addEntry")
	public String addEntry(@ModelAttribute BibE bib) {
		dbHelper.insertEntry(bib);
		return "redirect:biblio";
	}

	// edit entry
	@RequestMapping("/editEntry/{id}")
	public String greeting(Model model, @PathVariable(value = "id", required = false) String id) {
		List<BibE> entries = dbHelper.getSelectedEntries(id);
		model.addAttribute("entry", entries.get(0));
		return "editEntry";
	}

	// save edited entry
	@RequestMapping("/saveEntry/{id}")
	public String saveEntry(@ModelAttribute BibE bib) {
		dbHelper.updateEntry(bib);
		return "redirect:/biblio";
	}

	// import bibtex files
	@RequestMapping(value = "/uploadFile")
	public String uploadFile(@RequestParam("file") MultipartFile file) {
		BibEntryImporter importer = new BibEntryImporter();
		List<BibE> beList = importer.entryImporter(file);

		for (BibE entry : beList)
			dbHelper.insertEntry(entry);
		return "redirect:/biblio";
	}

	// export selected entry
	@RequestMapping(value = "/modifySelected", params = "action=Export Selected")
	@ResponseBody
	void exportSelected(HttpServletResponse response, @RequestParam(value = "myCheck", required = true) String id,
			@RequestParam(value = "action", required = true) String action) throws IOException {



		
		List<BibE> entries = dbHelper.getSelectedEntries(id);
		BibEntryExporter exporter = new BibEntryExporter();
		exporter.exportToFile(response, entries);
	}

	// delete selected entry
	@RequestMapping(value = "/modifySelected", params = "action=Delete Selected")
	public String deleteSelected(@RequestParam(value = "myCheck", required = true) String idList,
			@RequestParam(value = "action", required = true) String action) {
		dbHelper.deleteEntries(idList);
		return "redirect:biblio";
	}

	// view formatted selected entry
	@RequestMapping(value = "/modifySelected", params = "action=View Selected in IEEE")
	public String viewFormattedSelected(@RequestParam(value = "myCheck", required = true) String id,
			@RequestParam(value = "action", required = true) String action, RedirectAttributes redir) {
		List<BibE> formattedEntries = dbHelper.getSelectedEntries(id);
		List<String> iEeeFormattedEntries = CitationStyleGenerator.generateCitations(formattedEntries, "ieee.csl",
				CitationStyleOutputFormat.TEXT);

		redir.addFlashAttribute("formattedEntries", iEeeFormattedEntries);
		return "redirect:biblio";
	}

	// search Ieee db
	List<BibE> searchEntries = new ArrayList<BibE>();
	@RequestMapping(value = "/searchIeeeDb", params = "action=Search Ieee")
	public String searchIeeeDb(@RequestParam(value = "action", required = true) String action, 
			@ModelAttribute BibE bib, RedirectAttributes redir) {
		
		ApiEntries ieEntries = new ApiEntries();
		String apiBaseUrl = "http://ieeexplore.ieee.org/gateway/ipsSearch.jsp?";
		IApiUrl IeeeApiUrl = new IeNumResults(new IeYearParam(new IeJournalParam(new IeTitleParam
									(new IeAuthorParam(new ApiUrl(apiBaseUrl, bib))))));
		String IeeeApiUrlString = IeeeApiUrl.assembleUrl(apiBaseUrl, bib);
		
		searchEntries = ieEntries.retrieveIeeeEntries(bib, IeeeApiUrlString);

		redir.addFlashAttribute("IeeeEntries", searchEntries);
		return "redirect:biblio";
	}

	// import Ieee records
	@RequestMapping(value = "/searchIeeeDb", params = "action=Import Selected")
	public String importIeeeSelected(@RequestParam(value = "myCheck", required = true) String id) {
		List<String> items = Arrays.asList(id.split("\\s*,\\s*"));
		List<BibE> selectedBiblioEntries = new ArrayList<BibE>();

		for (int i = 0; i < items.size(); i++)
			selectedBiblioEntries.add(searchEntries.get(Integer.parseInt(items.get(i)) - 1));
		for (BibE entry : selectedBiblioEntries)
			dbHelper.insertEntry(entry);

		return "redirect:biblio";
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

}