package edu.cornell.ld4l.ontologydomainmodelbuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DomainEntry {

	private String modelTitle;
	private String image;
	private String modelDescription;
	
	private Set<Entity> entities = new HashSet<Entity>();
	private Set<String> depenOnt = new HashSet<String>();
	

	public DomainEntry(){
		
	}
	
	public DomainEntry(String modelTitle, String image, String modelDescription) {
		super();
		this.modelTitle = modelTitle;
		this.image = image;
		this.modelDescription = modelDescription;
	}
	
	public String getModelTitle() {
		return modelTitle;
	}
	public void setModelTitle(String modelTitle) {
		this.modelTitle = modelTitle;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getModelDescription() {
		return modelDescription;
	}
	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}
	public Set<Entity> getEntities() {
		return entities;
	}

	public void setEntities(Set<Entity> entities) {
		this.entities = entities;
	}
	
	public Set<String> getDepenOnt() {
		return depenOnt;
	}

	public void setDepenOnt(Set<String> depenOnt2) {
		this.depenOnt = depenOnt2;
	}
	
	
	
}
