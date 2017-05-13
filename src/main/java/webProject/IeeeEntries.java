package webProject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class IeeeEntries {
	private List<BibE> IeeeEntries;
	public IeeeEntries(){}
	
	public List<BibE> getIeeeEntries() {
		return this.IeeeEntries;
	}
	private void setIeeEntries(List<BibE> IeeeEntries) {
		this.IeeeEntries = IeeeEntries;
	}
	
	public List<BibE> retrieveIeeeEntries(BibE entry) {
		String yearString;
		List<BibE> IeeeEntries = new ArrayList<BibE>();
		String a = "", t = "", j = "";
		if(entry.getYear() != null)
			yearString = entry.getYear().toString();
		else
			yearString = "";
		
    	//Build request url
    	if(!entry.getAuthor().equals(""))
    		a = "au=" + entry.getAuthor();
    	if(!entry.getTitle().equals(""))
    		t = "&ti=" + entry.getTitle();
    	if(!entry.getJournal().equals(""))
    		j = "&jn=" + entry.getJournal();
    	if(!yearString.equals(""))
    		yearString = "&py=" + yearString;
        String requestUrl = "http://ieeexplore.ieee.org/gateway/ipsSearch.jsp?" + a + t + j + yearString + "&hc=10";
        
        //Request and get response
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
        String responseString = response.getBody().toString();
        
        //Deserialize XML to Bibliography entry list
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document xmlDocument = builder.parse( new InputSource(new StringReader(responseString)));
            
            JAXBContext jaxbContext = JAXBContext.newInstance(Root.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Root document = (Root) jaxbUnmarshaller.unmarshal(xmlDocument);

            IeeeEntries = document.getBibliographies();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        
        return IeeeEntries;
	}

}
