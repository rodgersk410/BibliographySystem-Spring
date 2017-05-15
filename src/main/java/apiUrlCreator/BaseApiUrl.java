package apiUrlCreator;

import webProject.BibE;

public class BaseApiUrl implements IApiUrl {

	String baseUrl;
	BibE entry;
	
	
	public BaseApiUrl(String baseUrl, BibE entry) {
		this.baseUrl = baseUrl;
		this.entry = entry;
	}

	@Override
	public String assembleUrl(String baseUrl, BibE bib) {
		return baseUrl;
	}

}
