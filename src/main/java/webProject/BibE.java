package webProject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.jdbc.core.RowMapper;

import javax.xml.bind.annotation.XmlAccessType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@XmlRootElement(name="root")
@XmlAccessorType(XmlAccessType.FIELD)
class Root {
	
	@XmlElement(name = "document")
	List<BibE> bibliographies;
	
	@XmlElement(name = "totalfound")
	TotalFound totalFound;
	
	@XmlElement(name = "totalsearched")
	TotalSearched totalSearched;
	
	List<BibE> getBibliographies() {
		return this.bibliographies;
	}
	
}

@XmlRootElement
class TotalFound {}

@XmlRootElement
class TotalSearched {}

@XmlRootElement(name="document")
@XmlAccessorType(XmlAccessType.FIELD)
public class BibE {

	@XmlElement(name = "authors")
    private String author;
	
	@XmlElement(name = "title")
	private String title;
	@XmlElement(name = "py")
	private int year;
	@XmlElement(name = "publisher")
	private String journal;
	
	private int id;
	private int searchId;
    
    public BibE(int id, String author, String title, int year, String journal) {
    	this.author = author;
    	this.title = title;
    	this.year = year;
    	this.journal = journal;
    	this.id = id;
    }
    
    public BibE(){}
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    
    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }
    
    public int getSearchId() {
        return searchId;
    }
    
    public void setSearchId(int searchId) {
    	this.searchId = searchId;
    }
    
    public StringBuffer entryToString() {
		StringBuffer sb = new StringBuffer();

	//	for (BibE entry : entries) {
			sb.append("@article{");
			sb.append("\n");
			sb.append("author = {" + this.getAuthor() + "},");
			sb.append("\n");
			sb.append("title = {" + this.getTitle() + "},");
			sb.append("\n");
			sb.append("year = {" + this.getYear() + "},");
			sb.append("\n");
			sb.append("journal = {" + this.getJournal() + "},");
			sb.append("\n");
			sb.append("}");
			sb.append("\n");
			sb.append("\n");
	//	}
		
		return sb;
    }
    
    
}