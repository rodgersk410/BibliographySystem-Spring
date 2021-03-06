package webProject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHelper implements IDatabaseHelper {
	
	@Override
	public List<BibE> getSelectedEntries(String id){
		String sql = "SELECT * FROM entries where id in (" + id + ")";

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<BibE> entries  = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(BibE.class));

		return entries;
	}
    
	@Override
    public List<BibE> getAllEntries() {
		String sql = "SELECT * FROM entries";

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<BibE> entries  = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(BibE.class));

		return entries;
	}
    
	@Override
    public void insertEntry(BibE bib) {
    	if(this.isUnique(bib))
    	{
    		String fields = bib.listOfClassFieldNames();
    		String fieldValues = bib.listOfObjectValues();
    		jdbcTemplate.update("insert into entries (" + fields + ")"
        		+ " values (" + fieldValues + ")");
    	}
    	else
    		System.out.println("Cannot add new entry " + bib.getTitle() + ". Entry already exists.");
	}
    
	@Override
    public void updateEntry(BibE bib) {
    	if(this.isUnique(bib))
    	{
    		String fieldAndValues = bib.listOfNameAndValues();
    		jdbcTemplate.update("update entries set " + fieldAndValues + "where id = " + bib.getId());
    	}
    	else
    		System.out.println("Cannot update entry " + bib.getTitle() + ". Entry already exists.");
	}
    
	@Override
    public void deleteEntries(String idList) {
    	jdbcTemplate.update("delete from bibliographies.entries where id in (" + idList + ")");
	}
    
    //this method helps to prevent duplicate entries in the system
    private Boolean isUnique(BibE bib) {
		
		String yearOperator = "= ";
		if(bib.getYear() == null)
			yearOperator = "is ";
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<BibE> entries = this.jdbcTemplate.query("select * from entries" + 
				" where author = '" + bib.getAuthor() + 
				"' and title = '" + bib.getTitle() + 
				"' and year " + yearOperator + bib.getYear(),
				new BeanPropertyRowMapper(BibE.class));
		
		if(entries.isEmpty())
			return true;
		
		return false;
    }
    
    @Autowired
	JdbcTemplate jdbcTemplate;

}
