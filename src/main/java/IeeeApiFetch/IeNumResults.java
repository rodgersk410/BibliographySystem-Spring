package IeeeApiFetch;

import webProject.BibE;

public class IeNumResults extends ApiUrlDecorator {

	public IeNumResults(IApiUrl c) {
		super(c);
	}
	
	@Override
	public String assembleUrl(String baseUrl, BibE bib){
		String s = super.assembleUrl(baseUrl, bib);
		return s + "&hc=10";
	}
}