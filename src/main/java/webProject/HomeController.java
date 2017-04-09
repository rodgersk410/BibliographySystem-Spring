package webProject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXObject;
import org.jbibtex.BibTeXParser;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;
import org.jbibtex.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@Controller
public class HomeController {
	
	//homepage	
    @RequestMapping("/biblio")
    public String getHomepage(Model model) {
        List<Bibliography> entries = this.jdbcTemplate.query(
        "select id, author, title, year, journal from entries",
        new RowMapper<Bibliography>() {
            public Bibliography mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Bibliography entry = new Bibliography(rs.getInt("id"),
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
        List<Bibliography> entries = this.jdbcTemplate.query(
        "select id, author, title, year, journal from entries where id = " + id,
        new RowMapper<Bibliography>() {
            public Bibliography mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Bibliography entry = new Bibliography(rs.getInt("id"),
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
    
	// download entries
	@RequestMapping(value = "/downloadEntries", method = RequestMethod.GET, produces = "application/bibtex")
	@ResponseBody void downloadEntries(HttpServletResponse response, Model model) throws IOException {

		List<Bibliography> entries = this.jdbcTemplate.query("select id, author, title, year, journal from entries",
				new RowMapper<Bibliography>() {
					public Bibliography mapRow(ResultSet rs, int rowNum) throws SQLException {
						Bibliography entry = new Bibliography(rs.getInt("id"), rs.getString("author"),
								rs.getString("title"), rs.getInt("year"), rs.getString("journal"));
						return entry;
					}
				});

		StringBuffer sb = new StringBuffer();

		for (Bibliography entry : entries) {
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
    
    @Autowired
	JdbcTemplate jdbcTemplate;
    
}