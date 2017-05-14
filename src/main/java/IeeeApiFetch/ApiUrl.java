package IeeeApiFetch;

import webProject.BibE;

public class ApiUrl implements IApiUrl {

	String baseUrl;
	BibE entry;
	
	
	public ApiUrl(String baseUrl, BibE entry) {
		this.baseUrl = baseUrl;
		this.entry = entry;
	}

	@Override
	public String assembleUrl(String baseUrl, BibE bib) {
		return baseUrl;
	}

}
