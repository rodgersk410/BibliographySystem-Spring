package webProject;

import java.lang.reflect.Field;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javax.xml.bind.annotation.XmlAccessType;

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
	private Integer year;
	@XmlElement(name = "publisher")
	private String journal;
	
	private Integer id;
    
    public BibE(Integer id, String author, String title, Integer year, String journal) {
    	this.author = author;
    	this.title = title;
    	this.year = year;
    	this.journal = journal;
    	this.id = id;
    }
    
    public BibE(){}
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
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
    
    public Integer getYear() {
    	
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
    
    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }
    
    public StringBuffer entryToString() {
		StringBuffer sb = new StringBuffer();
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
		
		return sb;
    }
    
    /*the following functions assist with creating 
     * insert/update statements to db*/
    
	public String listOfClassFieldNames() {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (Field f : getClass().getDeclaredFields()) {
			if (!(f.getName().equals("id"))) {
				sb.append(prefix);
				prefix = ",";
				sb.append(f.getName());
			}
		}
		return sb.toString();
	}
    
	public String listOfObjectValues() {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (Field f : getClass().getDeclaredFields()) {
			if (!(f.getName().equals("id"))) {
				sb.append(prefix);
				prefix = ",";
				try {
					if (f.getType().toString().equals("class java.lang.String")) {
						sb.append("'");
						sb.append(f.get(this));
						sb.append("'");
					} else {
						sb.append(f.get(this));
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	public String listOfNameAndValues() {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (Field f : getClass().getDeclaredFields()) {
			if (!(f.getName().equals("id"))) {
				sb.append(prefix);
				prefix = ",";
				try {
					sb.append(f.getName());
					sb.append(" = ");
					if (f.getType().toString().equals("class java.lang.String")) {
						sb.append("'");
						sb.append(f.get(this));
						sb.append("'");
					} else {
						sb.append(f.get(this));
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}