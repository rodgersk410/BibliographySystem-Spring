package apiUrlCreator;

import webProject.BibE;

public class IeAuthorParam extends ApiUrlDecorator {

	public IeAuthorParam(IApiUrl c) {
		super(c);
	}

	@Override
	public String assembleUrl(String baseUrl, BibE bib){
		String s = super.assembleUrl(baseUrl, bib);
		String a = "";
		
    	if(!bib.getAuthor().equals(""))
    		a = "au=" + bib.getAuthor();
		return s + a;
	}
}