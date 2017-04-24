package webProject;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="root")
@XmlAccessorType(XmlAccessType.FIELD)
class Root {
	
	@XmlElement(name = "document")
	List<Bibliography> bibliographies;
	
	@XmlElement(name = "totalfound")
	TotalFound totalFound;
	
	@XmlElement(name = "totalsearched")
	TotalSearched totalSearched;
	
	List<Bibliography> getBibliographies() {
		return this.bibliographies;
	}
	
}

@XmlRootElement
class TotalFound {}

@XmlRootElement
class TotalSearched {}

@XmlRootElement(name="document")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bibliography {

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
    
    public Bibliography(int id, String author, String title, int year, String journal) {
    	this.author = author;
    	this.title = title;
    	this.year = year;
    	this.journal = journal;
    	this.id = id;
    }
    
    public Bibliography(){}
    
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

}