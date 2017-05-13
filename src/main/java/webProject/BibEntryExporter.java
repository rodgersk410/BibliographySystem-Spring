package webProject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class BibEntryExporter {
	
	public void exportToFile(HttpServletResponse response, List<BibE> entries) throws IOException{
		
		//convert to string
		StringBuffer sb = new StringBuffer();
		for(BibE entry : entries)
			sb = sb.append(entry.entryToString());

		byte[] bytes = sb.toString().getBytes();
		InputStream is = new ByteArrayInputStream(bytes);

		//move string to bibtex file
		response.setContentType("application/bibtex");
		response.setHeader("Content-Disposition", "attachment; filename=" + "BibtexRecords.bib");
		OutputStream os = response.getOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) != -1)
			os.write(buffer, 0, len);
		os.flush();
		os.close();
		is.close();
	}
}