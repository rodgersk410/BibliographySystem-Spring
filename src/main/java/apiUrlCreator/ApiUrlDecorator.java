package apiUrlCreator;

import webProject.BibE;

public class ApiUrlDecorator implements IApiUrl {

	private IApiUrl IApiUrl;
	
	public ApiUrlDecorator(IApiUrl url){
		this.IApiUrl=url;
	}

	@Override
	public String assembleUrl(String baseUrl, BibE bib) {
		String s = this.IApiUrl.assembleUrl(baseUrl, bib);
		return s;
	}

}