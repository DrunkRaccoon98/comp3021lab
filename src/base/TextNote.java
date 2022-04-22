package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class TextNote extends Note {
	private static final long serialVersionUID = 4L;
	String content;
	
	public TextNote(File f) {
		super(f.getName());
		this.content = getTextFromFile(f.getAbsolutePath());
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTextFromFile(String absolutePath) {
		String result = "";
		FileInputStream fis = null;
		InputStreamReader in = null;
		try {
			fis = new FileInputStream(absolutePath);
			in = new InputStreamReader(fis);
			int nextchar = in.read();
			while(nextchar != -1) {
				result += (char) nextchar;
				nextchar = in.read();
			}
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public void exportTextToFile(String pathFolder) {
		if(pathFolder == "") {
			pathFolder = ".";
		}
		File file = new File(pathFolder + File.separator + this.getTitle().replace(' ', '_') + ".txt");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public TextNote(String title, String content) {
		super(title);
		this.content = content;
	}
}
