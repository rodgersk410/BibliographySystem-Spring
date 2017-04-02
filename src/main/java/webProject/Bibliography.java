package webProject;

public class Bibliography {

    private String author;
    private String title;
    private int year;
    private String journal;
    private final int id;
    
    public Bibliography(int id, String author, String title, int year, String journal) {
    	this.author = author;
    	this.title = title;
    	this.year = year;
    	this.journal = journal;
    	this.id = id;
    }
    
    public int getId() {
        return id;
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

}