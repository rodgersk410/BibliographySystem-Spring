package webProject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHelper {
	
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
    List<BibE> entries = new ArrayList<BibE>();
	entries = jdbcTemplate.query("select id, author, title, year, journal from entries",
			new RowMapper<BibE>() {
				public BibE mapRow(ResultSet rs, int rowNum) throws SQLException {
					BibE entry = new BibE(rs.getInt("id"), rs.getString("author"),
							rs.getString("title"), rs.getInt("year"), rs.getString("journal"));
					return entry;
				}
			});
		return entries;
	}
    
    public void insertEntry(BibE bib) {
    	if(this.isUnique(bib))
    	{
    		jdbcTemplate.update("insert into entries (author, title, year, journal) "
        		+ "values (?, ?, ?, ?)", bib.getAuthor(), bib.getTitle(), bib.getYear(), bib.getJournal());
    	}
    	else
    		System.out.println("Cannot add new entry " + bib.getTitle() + ". Entry already exists.");
	}
    
    public void updateEntry(BibE bib) {
    	if(this.isUnique(bib))
    	{
    		jdbcTemplate.update("update entries set author=?, title=?, year=?, journal=? " + "where id = ?",
				bib.getAuthor(), bib.getTitle(), bib.getYear(), bib.getJournal(), bib.getId());
    	}
    	else
    		System.out.println("Cannot add new entry " + bib.getTitle() + ". Entry already exists.");
	}
    
    public void deleteEntries(String idList) {
    	jdbcTemplate.update("delete from bibliographies.entries where id in (" + idList + ")");
	}
    
    public Boolean isUnique(BibE bib) {
		List<BibE> entries = this.jdbcTemplate.query("select id, author, title, year, journal from entries"
				+ " where author = '" + bib.getAuthor() + "' and title = '" + bib.getTitle() + "' and year = "
				+ bib.getYear(),
				new RowMapper<BibE>() {
					public BibE mapRow(ResultSet rs, int rowNum) throws SQLException {
						BibE entry = new BibE(rs.getInt("id"), rs.getString("author"),
								rs.getString("title"), rs.getInt("year"), rs.getString("journal"));
						return entry;
					}
				});
		
		if(entries.isEmpty())
			return true;
		
		return false;
    }
    
    @Autowired
	JdbcTemplate jdbcTemplate;

}
