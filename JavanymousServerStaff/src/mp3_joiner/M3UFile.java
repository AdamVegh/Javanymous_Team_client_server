package mp3_joiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class M3UFile {

	// member variables:
	private File m3uFile;
	private File destinationFile;
	private List<File> sourceFileList;

	// constructor:
	public M3UFile(File m3uFile) throws JoinerException {
		try {
			if (!checkIfValidM3U(m3uFile))
				throw new JoinerException("Inappropriate m3u-file:\n" + m3uFile.getAbsolutePath());
			this.m3uFile = m3uFile;
			this.destinationFile = createDestinationFileObj(m3uFile);
			this.sourceFileList = parseM3UFile(m3uFile);
			ensureSourceFileListIsValid();
		}
		catch (IOException exception) {
			throw new JoinerException("An IOException occoured:\n" + exception.toString());
		}
	}

	// getters:
	public File getM3UFile() {
		return m3uFile;
	}

	public File getDestinationFile() {
		return destinationFile;
	}

	public List<File> getSourceFileList() {
		return sourceFileList;
	}

	// m3u file-checker used by constructor:
	public static boolean checkIfValidM3U(File m3uFile) {
		System.out.println(m3uFile.getName());
		System.out.println(m3uFile.isFile());
		System.out.println(m3uFile.exists());
		return m3uFile.isFile() && m3uFile.getName().toLowerCase().endsWith(".m3u");
	}

	// processor method:
	public File joinMP3Files() throws JoinerException {
		try {
			MP3Joiner.joinFiles(sourceFileList, destinationFile);
			return destinationFile;
		}
		catch (IOException exception) {
			throw new JoinerException("An IOException occoured:\n" + exception.toString());
		}
	}

	// other assistant functions used by constructor:
	private static File createDestinationFileObj(File m3uFile) {
		String m3uPath = m3uFile.getAbsolutePath();
		String destinationPath = m3uPath.substring(0, m3uPath.length() - ".m3u".length()) + ".mp3";
		return new File(destinationPath);
	}

	private static List<File> parseM3UFile(File m3uFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(m3uFile));
		List<File> sourceFileList = new ArrayList<>();
		{
			String line = null;
			String nonComment = null;
			File actualSourceFile = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				nonComment = line.split("#")[0];
				actualSourceFile = new File(nonComment);
				sourceFileList.add(actualSourceFile);
			}
		}
		reader.close();
		return sourceFileList;
	}
	
	@SuppressWarnings("unused")
	private void ensureSourceFileListIsValid() throws JoinerException {
		if (sourceFileList.size() > 0) 
		{
			String message = "Invalid mp3-files on list:";
			for (File file: sourceFileList) {
				message += "\n" + file.getAbsolutePath();
			}
			//throw new JoinerException(message);
		}
	}
}
