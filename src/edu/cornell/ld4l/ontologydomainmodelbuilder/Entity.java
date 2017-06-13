package edu.cornell.ld4l.ontologydomainmodelbuilder;

public class Entity {
	
	private String fragment;
	private String type;
	private String iri;
	private String ontology;
	
	
	
	public Entity(String fragment, String type, String iri, String ontology) {
		super();
		this.fragment = fragment;
		this.type = type;
		this.iri = iri;
		this.ontology = ontology;
	}
	
	public String getFragment() {
		return fragment;
	}
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIri() {
		return iri;
	}
	public void setIri(String iri) {
		this.iri = iri;
	}
	public String getOntology() {
		return ontology;
	}
	public void setOntology(String ontology) {
		this.ontology = ontology;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fragment == null) ? 0 : fragment.hashCode());
		result = prime * result + ((iri == null) ? 0 : iri.hashCode());
		result = prime * result + ((ontology == null) ? 0 : ontology.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (fragment == null) {
			if (other.fragment != null)
				return false;
		} else if (!fragment.equals(other.fragment))
			return false;
		if (iri == null) {
			if (other.iri != null)
				return false;
		} else if (!iri.equals(other.iri))
			return false;
		if (ontology == null) {
			if (other.ontology != null)
				return false;
		} else if (!ontology.equals(other.ontology))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
}
