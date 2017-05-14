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

public class ApiEntries {
	private List<BibE> ApiEntries;
	public ApiEntries(){}
	
	public List<BibE> getApiEntries() {
		return this.ApiEntries;
	}
	private void setApiEntries(List<BibE> ApiEntries) {
		this.ApiEntries = ApiEntries;
	}
	
	public List<BibE> retrieveIeeeEntries(BibE entry, String ApiUrl) {
        
        //Request and get response
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(ApiUrl, String.class);
        String responseString = response.getBody().toString();
        
        //Deserialize XML to Bibliography entry list
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;
        try  
        {  
            builder = factory.newDocumentBuilder();  
            Document xmlDocument = builder.parse(new InputSource(new StringReader(responseString)));
            
            JAXBContext jaxbContext = JAXBContext.newInstance(Root.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Root document = (Root) jaxbUnmarshaller.unmarshal(xmlDocument);

            ApiEntries = document.getBibliographies();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        
        return ApiEntries;
	}

}
