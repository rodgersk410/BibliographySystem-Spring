package webProject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

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
    
    @Autowired
	JdbcTemplate jdbcTemplate;

}
