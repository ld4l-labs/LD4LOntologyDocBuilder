package ontologyfilesreader;

import java.util.Comparator;

public class Entity implements Comparator {

	private String label;
	private String uri;
	private String link;
	
	
	public Entity(String label, String uri, String link) {
		super();
		this.label = label;
		this.uri = uri;
		this.link = link;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}


	@Override
	public int compare(Object o1, Object o2) {
		Entity e1 = (Entity) o1;
		Entity e2 = (Entity) o2;
		
		int compare = (e1.getLabel().compareTo(e2.getLabel()));
		if (compare < 0){
		    return 1;
		}
		else if (compare > 0) {
		   return -1;
		}
		else {
		    return 0;
		}
	}

	
	
}
