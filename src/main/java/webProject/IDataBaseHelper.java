package webProject;

import java.util.List;

public interface IDataBaseHelper {
	public List<BibE> getSelectedEntries(String id);
	public List<BibE> getAllEntries();
	public void insertEntry(BibE bib);
	public void updateEntry(BibE bib);
	public void deleteEntries(String idList);
}
