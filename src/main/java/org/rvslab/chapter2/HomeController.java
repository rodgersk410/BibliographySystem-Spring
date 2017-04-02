package org.rvslab.chapter2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@Controller
public class HomeController {
	
	//homepage	
    @RequestMapping("/biblio")
    public String greeting(Model model) {
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

    @Autowired
	JdbcTemplate jdbcTemplate;
    
}