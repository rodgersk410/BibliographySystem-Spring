package webProject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;
import org.jbibtex.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.multipart.MultipartFile;

public class BibEntryImporter implements IBibEntryImporter {
	
	public List<BibE> entryImporter(MultipartFile file) {
		List<BibE> entryList = new ArrayList<BibE>();
		
        if (file.isEmpty()) {
            System.out.println("file empty");
        }
        else {
        	try {
        		//parse bibtex file
				InputStream inputStream =  new BufferedInputStream(file.getInputStream());
				Reader reader = new InputStreamReader(inputStream);
				BibTeXParser bibtexParser = new BibTeXParser();
				BibTeXDatabase database = bibtexParser.parseFully(reader);
				Map<org.jbibtex.Key, org.jbibtex.BibTeXEntry> entryMap = database.getEntries();
				Collection<org.jbibtex.BibTeXEntry> entries = entryMap.values();
				
				//map parsed bibtex entries to our model
				for(BibTeXEntry entry : entries){					
					Value title = entry.getField(BibTeXEntry.KEY_TITLE);
					if(title == null)
						continue;
					Value author = entry.getField(BibTeXEntry.KEY_AUTHOR);
					if(author == null)
						continue;
					Value year = entry.getField(BibTeXEntry.KEY_YEAR);
					if(year == null)
						continue;
					Value journal = entry.getField(BibTeXEntry.KEY_JOURNAL);
					if(journal == null)
						continue;
				
					BibE be = new BibE();
					be.setAuthor(author.toUserString());
					be.setTitle(title.toUserString());
					be.setJournal(journal.toUserString());
					be.setYear(Integer.parseInt(year.toUserString()));
					
					entryList.add(be);	//add to list of entries
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
        return entryList;
		
	}
    @Autowired
	JdbcTemplate jdbcTemplate;	
}
