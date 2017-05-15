package apiUrlCreator;

import webProject.BibE;

public class IeJournalParam extends ApiUrlDecorator {

	public IeJournalParam(IApiUrl c) {
		super(c);
	}

	@Override
	public String assembleUrl(String baseUrl, BibE bib){
		String s = super.assembleUrl(baseUrl, bib);
		String a = "";
    	if(!bib.getJournal().equals(""))
    		a = "&jn=" + bib.getJournal();
		return s + a;
	}
}