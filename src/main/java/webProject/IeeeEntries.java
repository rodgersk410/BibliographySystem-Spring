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
	
	public List<BibE> retrieveIeeeEntries(String a, String t, String j, String y) {
		
		List<BibE> IeeeEntries = new ArrayList<BibE>();
		
    	//Build request url
    	if(!a.equals(""))
    		a = "au=" + a;
    	if(!t.equals(""))
    		t = "&ti=" + t;
    	if(!j.equals(""))
    		j = "&jn=" + j;
    	if(!y.equals(""))
    		y = "&py=" + y;
        String requestUrl = "http://ieeexplore.ieee.org/gateway/ipsSearch.jsp?" + a + t + j + y + "&hc=10";
        
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
        
        //Set temporary id to each result
        for(int i=0; i < 10; i++) {
        	IeeeEntries.get(i).setSearchId(i+1);
        }
        
        return IeeeEntries;
	}

}
