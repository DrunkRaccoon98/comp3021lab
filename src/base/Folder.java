package base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Folder implements Comparable<Folder> {
	
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
	
	public void sortNotes() {
		Collections.sort(notes);
	}
	
	public List<Note> searchNotes(String keywords) {
		List<Note> result = new ArrayList<Note>();
		
		keywords = keywords.toLowerCase();
		String[] words = keywords.split(" ");
		
		for(Note n: notes) {
			String title = n.getTitle().toLowerCase();
			String content = "";
			if(n instanceof TextNote) content = ((TextNote) n).getContent().toLowerCase();
			boolean valid = true;
			for(int i = 0; i < words.length; i++) {
				if(words[i].equals("or")) {
					continue;
				}
				// System.out.println("Checking note: " + n.getTitle() + " for keyword: " + words[i]);
				if(title.contains(words[i])) {
					continue;
				} else if(content.contains(words[i])) {
					continue;
				} else {
					if(i > 1 && words[i-1].equals("or")) {
						// System.out.println("Checking note: " + n.getTitle() + " for keyword: " + words[i-2]);
						if(title.contains(words[i-2])) {
							continue;
						} else if(content.contains(words[i-2])) {
							continue;
						}
					}
					if(i < words.length-2 && words[i+1].equals("or")) {
						// System.out.println("Checking note: " + n.getTitle() + " for keyword: " + words[i+2]);
						if(title.contains(words[i+2])) {
							continue;
						} else if(content.contains(words[i+2])) {
							continue;
						}
					}
					valid = false;
					// break;
				}
			}
			if(valid) result.add(n);
		}
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		Folder other = (Folder) obj;
		if(other != null) return Objects.equals(name, other.name);
		return false;
	}
	
	@Override
	public int compareTo(Folder o) {
		if(this.name == o.name) return 0;
		else return this.name.compareTo(o.name);
	}
}
