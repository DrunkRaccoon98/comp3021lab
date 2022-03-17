package base;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Note implements Comparable<Note>, Serializable {
	private static final long serialVersionUID = 3L;
	private Date date;
	private String title;
	
	public Note(String title) {
		this.title = title;
		this.date = new Date(System.currentTimeMillis());
	}
	
	public String getTitle() {
		return title;
	}

	@Override
	public boolean equals(Object obj) {
		Note other = (Note) obj;
		if(other != null) return other.title == this.title;
		return false;
	}
	
	@Override
	public int compareTo(Note o) {
		if(this.date == o.date) return 0;
		else return -this.date.compareTo(o.date);
	}
	
	public String toString() {
		return date.toString() + "\t" + title;
	}
}
