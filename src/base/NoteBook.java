package base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteBook {
	ArrayList<Folder> folders;
	
	public NoteBook() {
		folders = new ArrayList<Folder>();
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
	
}
