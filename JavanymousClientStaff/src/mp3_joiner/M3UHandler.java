package mp3_joiner;

import java.io.File;
import java.util.List;

// This class should create mp3 file object list and then join them to one object. 

public class M3UHandler {
	
	private M3UHandler(){}

	// main method:
	public static File joinMP3FilesFromM3U(File m3uFile) throws JoinerException {
		M3UFile m3uData = new M3UFile(m3uFile);
		return m3uData.joinMP3Files();
	}
	
	// another accessory:
	public static List<File> getMP3FileListFromM3U(File m3uFile) throws JoinerException {
		M3UFile m3uData = new M3UFile(m3uFile);
		return m3uData.getSourceFileList();
	}
	
}
