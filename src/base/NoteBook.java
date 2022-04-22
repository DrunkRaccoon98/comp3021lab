package base;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteBook implements Serializable {
	
	private static final long serialVersionUID = 1L;

	ArrayList<Folder> folders;
	
	public NoteBook() {
		folders = new ArrayList<Folder>();
	}
	
	public NoteBook(String file) {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			NoteBook n = (NoteBook) in.readObject();
			this.folders = n.getFolders();
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addFolder(String folderName) {
		for(Folder f: folders) {
			if(f.getName() == folderName) return;
		}
		folders.add(new Folder(folderName));
	}
	
	public boolean createTextNote(String folderName, String title, String content) {
		TextNote note = new TextNote(title, content);
		return insertNote(folderName, note);
	}
	
	public boolean createImageNote(String folderName, String title) {
		ImageNote note = new ImageNote(title);
		return insertNote(folderName, note);
	}
	
	public ArrayList<Folder> getFolders() {
		return folders;
	}
	
	public boolean insertNote(String folderName, Note note) {
		Folder f = null;
		for(Folder f1: folders)
			if(f1.getName() == folderName)
				f = f1;
		
		if(f == null) {
			// Folder does not exist, create folder
			f = new Folder(folderName);
			f.addNote(note);
			folders.add(f);
			return true;
		} else {
			for(Note n: f.getNotes()) {
				if(n.equals(note)) {
					// Note already exists
					System.out.println("Creating note " + note.getTitle() + " under folder " + folderName + " failed");
					return false;
				}
			}

			// Note does note exist, add note to folder
			f.addNote(note);
			return true;
		}
	}
	
	public void sortFolders() {
		for(Folder f: folders) {
			f.sortNotes();
		}
		Collections.sort(folders);
	}
	
	public List<Note> searchNotes(String keywords) {
		List<Note> result = new ArrayList<Note>();
		
		for(Folder f: folders) {
			List<Note> subresult = f.searchNotes(keywords);
			for(Note n: subresult) {
				result.add(n);
			}
		}
		
		return result;
	}
	
	public boolean save(String file) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
