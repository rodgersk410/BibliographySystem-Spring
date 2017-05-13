package webProject;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public interface IBibEntryExporter {
	public void exportToFile(HttpServletResponse response, List<BibE> entries) throws IOException;
}