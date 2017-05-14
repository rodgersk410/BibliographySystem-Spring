package IeeeApiFetch;

import webProject.BibE;

public class IeYearParam extends ApiUrlDecorator {

	public IeYearParam(IApiUrl c) {
		super(c);
	}
	
	@Override
	public String assembleUrl(String baseUrl, BibE bib){
		String s = super.assembleUrl(baseUrl, bib);
		String yearString;
		if(bib.getYear() != null)
			yearString = bib.getYear().toString();
		else
			yearString = "";
		
    	if(!yearString.equals(""))
    		yearString = "&py=" + bib.getYear();
		return s + yearString;
	}
}