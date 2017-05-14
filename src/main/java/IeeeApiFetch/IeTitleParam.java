package IeeeApiFetch;

import webProject.BibE;

public class IeTitleParam extends ApiUrlDecorator {

	public IeTitleParam(IApiUrl c) {
		super(c);
	}

	@Override
	public String assembleUrl(String baseUrl, BibE bib){
		String s = super.assembleUrl(baseUrl, bib);
		String a = "";
		
    	if(!bib.getTitle().equals(""))
    		a = "&ti=" + bib.getTitle();
		return s + a;
	}
}