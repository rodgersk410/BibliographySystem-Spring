package org.rvslab.chapter2;

import java.util.concurrent.atomic.AtomicInteger;

public class Bibliography {

    private String author;
    private String title;
    private int year;
    private String journal;
    private final int id;
    private static final AtomicInteger count = new AtomicInteger(0); 
    
    public Bibliography() {
    	id = count.incrementAndGet();
    }
    public Bibliography(String author, String title, int year, String journal) {
    	this.author = author;
    	this.title = title;
    	this.year = year;
    	this.journal = journal;
    	id = count.incrementAndGet();
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