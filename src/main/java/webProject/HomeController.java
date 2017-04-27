package webProject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import CitationFormatting.CitationStyle;
import CitationFormatting.CitationStyleGenerator;
import CitationFormatting.CitationStyleOutputFormat;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;
import org.jbibtex.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@Controller
public class HomeController {
	
	//homepage	
    @RequestMapping("/biblio")
    public String getHomepage(Model model) {
        List<BibE> entries = this.jdbcTemplate.query(
        "select id, author, title, year, journal from entries",
        new RowMapper<BibE>() {
            public BibE mapRow(ResultSet rs, int rowNum) throws SQLException {
            	BibE entry = new BibE(rs.getInt("id"),
            							rs.getString("author"),
                                        rs.getString("title"),
                                        rs.getInt("year"),
                                        rs.getString("journal")
                                        );
                return entry;
            }
        });
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
        return "redirect:biblio"; // back to the biblio view
    }
    
    //delete entry
    @RequestMapping("/deleteEntry/{id}")
    public String deleteEntry(@PathVariable(value="id", required=false) int id,
            Model model) {
        jdbcTemplate.update("delete from entries where id = ?", id);
        return "redirect:/biblio";
    }
    
    //edit entry
    @RequestMapping("/editEntry/{id}")
    public String greeting(Model model, @PathVariable(value="id", required=false) int id) {
        List<BibE> entries = this.jdbcTemplate.query(
        "select id, author, title, year, journal from entries where id = " + id,
        new RowMapper<BibE>() {
            public BibE mapRow(ResultSet rs, int rowNum) throws SQLException {
            	BibE entry = new BibE(rs.getInt("id"),
            							rs.getString("author"),
                                        rs.getString("title"),
                                        rs.getInt("year"),
                                        rs.getString("journal")
                                        );
                return entry;
            }
        });
        model.addAttribute("entry", entries.get(0));
        return "editEntry";
    }

    //save an edited entry
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
        if (file.isEmpty()) {
            System.out.println("file empty");
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
        }
        else {
        	try {
				InputStream inputStream =  new BufferedInputStream(file.getInputStream());
				Reader reader = new InputStreamReader(inputStream);
				BibTeXParser bibtexParser = new BibTeXParser();
				BibTeXDatabase database = bibtexParser.parseFully(reader);
				Map<org.jbibtex.Key, org.jbibtex.BibTeXEntry> entryMap = database.getEntries();
				Collection<org.jbibtex.BibTeXEntry> entries = entryMap.values();
				
				for(BibTeXEntry entry : entries){					
					Value title = entry.getField(BibTeXEntry.KEY_TITLE);
					if(title == null){
						continue;
					}
					Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
					if(author == null){
						continue;
					}
					Value year = entry.getField(BibTeXEntry.KEY_YEAR);
					if(year == null){
						continue;
					}
					Value journal = entry.getField(BibTeXEntry.KEY_JOURNAL);
					if(journal == null){
						continue;
					}
					
					String stitle = title.toUserString();
					String sauthor = author.toUserString();
					Integer iyear = Integer.parseInt(year.toUserString());
					String sjournal = journal.toUserString();
					
			        jdbcTemplate.update("insert into entries (author, title, year, journal) "
			        		+ "values (?, ?, ?, ?)", sauthor, stitle, iyear, sjournal);
				}
							
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("file empty");
			} catch (TokenMgrException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        return "redirect:/biblio"; // back to the biblio view
    }
    
	// export all entries
	@RequestMapping(value = "/downloadEntries", method = RequestMethod.GET, produces = "application/bibtex")
	@ResponseBody void downloadEntries(HttpServletResponse response, Model model) throws IOException {

		List<BibE> entries = getAllEntries();
		StringBuffer sb = entryToString(entries);

		byte[] bytes = sb.toString().getBytes();
		InputStream is = new ByteArrayInputStream(bytes);

		response.setContentType("application/bibtex");
		response.setHeader("Content-Disposition", "attachment; filename=" + "BibtexRecords.bib");
		OutputStream os = response.getOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) != -1) {
			os.write(buffer, 0, len);
		}
		os.flush();
		os.close();
		is.close();
	}
	
    //export selected entry
    @RequestMapping(value="/modifySelected", params="action=Export Selected")
    @ResponseBody void exportSelected(HttpServletResponse response, @RequestParam(value="myCheck", required=true) String id,
			@RequestParam(value="action", required=true) String action,
			Model model) throws IOException {

    	List<BibE> entries = getSelectedEntries(id);
		StringBuffer sb = entryToString(entries);

		byte[] bytes = sb.toString().getBytes();
		InputStream is = new ByteArrayInputStream(bytes);

		response.setContentType("application/bibtex");
		response.setHeader("Content-Disposition", "attachment; filename=" + "BibtexRecords.bib");
		OutputStream os = response.getOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) != -1) {
			os.write(buffer, 0, len);
		}
		os.flush();
		os.close();
		is.close();
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

		List<BibE> formattedEntries = getSelectedEntries(id);
		String ieeeStyleFile = "ieee.csl";
		
		StringBuffer sb = entryToString(formattedEntries);

		byte[] bytes = sb.toString().getBytes();
		InputStream is = new ByteArrayInputStream(bytes);
		
		Reader reader = new InputStreamReader(is);
		BibTeXParser bibtexParser = null;
		try {
			bibtexParser = new BibTeXParser();
		} catch (TokenMgrException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BibTeXDatabase database = bibtexParser.parseFully(reader);
		Map<org.jbibtex.Key, org.jbibtex.BibTeXEntry> entryMap = database.getEntries();
		Collection<org.jbibtex.BibTeXEntry> entries = entryMap.values();
		
		
		//CitationStyle cs = CitationStyle.createCitationStyleFromFile(ieeeStyleFile);
		//CitationStyleOutputFormat coFormat = new CitationStyleOutputFormat(TEXT("text", ""));
		List<String> co = CitationStyleGenerator.generateCitations(formattedEntries, ieeeStyleFile, CitationStyleOutputFormat.TEXT);
		
		for(String s : co) {
			System.out.println(s);
		}
		
		redir.addFlashAttribute("formattedEntries",formattedEntries);		
        return "redirect:biblio"; // back to the biblio view
    }
    
    //search Ieee db
    List<BibE> IeeeEntries = new ArrayList<BibE>();
    @RequestMapping(value="/searchIeeeDb", params="action=Search Ieee")
    public String searchIeeeDb(@RequestParam(value="action", required=true) String action,
						    		@RequestParam(value="author", required=false) String a,
						            @RequestParam(value="title", required=false) String t,
						            @RequestParam(value="journal", required=false) String j,
						            @RequestParam(value="year", required=false) String y,
    								Model model,
    								RedirectAttributes redir) {
    	
    	if(!a.equals("")) {
    		a = "au=" + a;
    	}
    	
    	if(!t.equals("")) {
    		t = "&ti=" + t;
    	}
    	
    	if(!j.equals("")) {
    		j = "&jn=" + j;
    	}
    	
    	if(!y.equals("")) {
    		y = "&py=" + y;
    	}
    	
        String url = "http://ieeexplore.ieee.org/gateway/ipsSearch.jsp?" + a + t + j + y + "&hc=10";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(
                url,
                String.class);
        
        String s = response.getBody().toString();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document xmlDocument = builder.parse( new InputSource( new StringReader( s ) ) );
            
            JAXBContext jaxbContext = JAXBContext.newInstance(Root.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Root document = (Root) jaxbUnmarshaller.unmarshal(xmlDocument);

            IeeeEntries = document.getBibliographies();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        
        for(int i=0; i < 10; i++) {
        	IeeeEntries.get(i).setSearchId(i+1);
        }
        
		redir.addFlashAttribute("IeeeEntries", IeeeEntries);        
        return "redirect:biblio"; // back to the biblio view
    }
    
    //import Ieee records
    @RequestMapping(value="/searchIeeeDb", params="action=Import Selected")
    public String importIeeeSelected(@RequestParam(value="myCheck", required=true) String id,
    								Model model) {
    	
    	System.out.println("author" + id);
    	List<String> items = Arrays.asList(id.split("\\s*,\\s*"));
    	List<BibE> selectedBiblioEntries = new ArrayList<BibE>();
    	
    	for(int i=0; i<items.size(); i++) {
    		selectedBiblioEntries.add(IeeeEntries.get(Integer.parseInt(items.get(i))));
    	}
    	
		for(BibE entry : selectedBiblioEntries){
			
	        jdbcTemplate.update("insert into entries (author, title, year, journal) "
	        		+ "values (?, ?, ?, ?)", entry.getAuthor(), entry.getTitle(),
	        		entry.getYear(), entry.getJournal());
		}
    			
        return "redirect:biblio"; // back to the biblio view
    }
    
    public List<BibE> getSelectedEntries(String id) {
		List<BibE> entries = this.jdbcTemplate.query("select id, author, title, year, journal from entries"
				+ " where id in (" + id + ")",
				new RowMapper<BibE>() {
					public BibE mapRow(ResultSet rs, int rowNum) throws SQLException {
						BibE entry = new BibE(rs.getInt("id"), rs.getString("author"),
								rs.getString("title"), rs.getInt("year"), rs.getString("journal"));
						return entry;
					}
				});
		return entries;
    }
    
    public List<BibE> getAllEntries() {
	List<BibE> entries = this.jdbcTemplate.query("select id, author, title, year, journal from entries",
			new RowMapper<BibE>() {
				public BibE mapRow(ResultSet rs, int rowNum) throws SQLException {
					BibE entry = new BibE(rs.getInt("id"), rs.getString("author"),
							rs.getString("title"), rs.getInt("year"), rs.getString("journal"));
					return entry;
				}
			});
		return entries;
	}
    
    public StringBuffer entryToString(List<BibE> entries) {
		StringBuffer sb = new StringBuffer();

		for (BibE entry : entries) {
			sb.append("@article{");
			sb.append("\n");
			sb.append("author = {" + entry.getAuthor() + "},");
			sb.append("\n");
			sb.append("title = {" + entry.getTitle() + "},");
			sb.append("\n");
			sb.append("year = {" + entry.getYear() + "},");
			sb.append("\n");
			sb.append("journal = {" + entry.getJournal() + "},");
			sb.append("\n");
			sb.append("}");
			sb.append("\n");
			sb.append("\n");
		}
		
		return sb;
    }
    
    @Autowired
	JdbcTemplate jdbcTemplate;
    
}