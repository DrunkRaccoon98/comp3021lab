package base;

import java.util.ArrayList;
import java.util.Objects;

public class Folder {
	
	private ArrayList<Note> notes;
	private String name;

	public Folder(String title) {
		this.name = title;
		notes = new ArrayList<Note>();
	}
	
	public void addNote(Note note) {
		notes.add(note);
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Note> getNotes() {
		return notes;
	}
	
	public String toString() {
		int textCount = 0;
		int imageCount = 0;
		
		for(int i = 0; i < notes.size(); i++) {
			if(notes.get(i) instanceof TextNote) textCount++;
			if(notes.get(i) instanceof ImageNote) imageCount++;
		}
		
		return name + ":" + textCount + ":" + imageCount;
	}
	
	@Override
	public boolean equals(Object obj) {
		Folder other = (Folder) obj;
		if(other != null) return Objects.equals(name, other.name);
		return false;
	}
}
