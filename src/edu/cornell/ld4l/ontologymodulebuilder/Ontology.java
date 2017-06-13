package edu.cornell.ld4l.ontologymodulebuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ontology {
	private String ontologyName = null;
	private String namespace = null;
	private String ontologyVersion = null;
	private String code = null;

	private List<Entity> classes = null;
	private List<Entity> objProperties = null;
	private List<Entity> dataProperties = null;
	private List<Entity> annotProperties = null;
	private List<Entity> individual = null;

	public Ontology(){
		
	}
	
	
	public Ontology(String ontologyName, String namespace, String ontologyVersion, String code, List<Entity> classes,
			List<Entity> objProperties, List<Entity> dataProperties, List<Entity> annotProperties,
			List<Entity> individual) {
		super();
		this.ontologyName = ontologyName;
		this.namespace = namespace;
		this.ontologyVersion = ontologyVersion;
		this.code = code;
		this.classes = classes;
		this.objProperties = objProperties;
		this.dataProperties = dataProperties;
		this.annotProperties = annotProperties;
		this.individual = individual;
	}


	public String getOntologyName() {
		return ontologyName;
	}
	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}
	public String getOntologyVersion() {
		return ontologyVersion;
	}
	public void setOntologyVersion(String ontologyVersion) {
		this.ontologyVersion = ontologyVersion;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<Entity> getClasses() {
		return classes;
	}
	public void setClasses(List<Entity> classes) {
		this.classes = classes;
	}
	public void addClass(Entity clas) {
		if(this.classes == null){
			this.classes = new ArrayList<Entity>();
		}
		this.classes.add(clas);
	}

	public List<Entity> getObjProperties() {
		return objProperties;
	}
	public void setObjProperties(List<Entity> objProperties) {
		this.objProperties = objProperties;
	}
	public void addObjProperty(Entity objProp) {
		if(this.objProperties == null){
			this.objProperties = new ArrayList<Entity>();
		}
		this.objProperties.add(objProp);
	}

	public List<Entity> getDataProperties() {
		return dataProperties;
	}
	public void setDataProperties(List<Entity> dataProperties) {
		this.dataProperties = dataProperties;
	}
	public void addDataProperty(Entity dataProp) {
		if(this.dataProperties == null){
			this.dataProperties = new ArrayList<Entity>();
		}
		this.dataProperties.add(dataProp);
	}
	
	public List<Entity> getIndividuals() {
		return individual;
	}
	public void setIndividuals(List<Entity> indv) {
		this.individual = indv;
	}
	public void addIndividual(Entity indv) {
		if(this.individual == null){
			this.individual = new ArrayList<Entity>();
		}
		this.individual.add(indv);
	}
	public List<Entity> getAnnotProperties() {
		return annotProperties;
	}
	public void setAnnotProperties(List<Entity> annotProperties) {
		this.annotProperties = annotProperties;
	}
	public void addAnnotationProperty(Entity annot) {
		if(this.annotProperties == null){
			this.annotProperties = new ArrayList<Entity>();
		}
		this.annotProperties.add(annot);
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	

}

