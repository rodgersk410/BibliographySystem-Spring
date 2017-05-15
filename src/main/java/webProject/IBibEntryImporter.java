package webProject;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface IBibEntryImporter {
	public List<BibE> entryImporter(MultipartFile file);
}
