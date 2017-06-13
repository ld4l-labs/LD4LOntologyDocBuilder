package ontologyfilesreader;

import java.util.ArrayList;
import java.util.List;

public class OWLRDFOntology {
	private String prefix = null;
	private String ontologyName = null;
	private String namespace = null;
	private String ontologyDocBaseURL = null;
	private String ontologyDocExactURL = null;
	
	private List<Entity> classes = null;
	private List<Entity> objProperties = null;
	private List<Entity> dataProperties = null;
	private List<Entity> annotProperties = null;
	private List<Entity> individual = null;
	private List<Entity> rdfsClasses = null;
	private List<Entity> rdfProperties = null;

	public OWLRDFOntology(){
		
	}
	
	
	public OWLRDFOntology(String ontologyName, String namespace, String ontologyDocBaseURL, String ontologyDocExactURL, String perfix, List<Entity> classes,
			List<Entity> objProperties, List<Entity> dataProperties, List<Entity> annotProperties,
			List<Entity> individual, List<Entity> rdfsClasses, List<Entity> rdfProperties) {
		super();
		this.ontologyName = ontologyName;
		this.namespace = namespace;
		this.setOntologyDocBaseURL(ontologyDocBaseURL);
		this.setOntologyDocExactURL(ontologyDocExactURL);
		this.prefix = perfix;
		this.classes = classes;
		this.objProperties = objProperties;
		this.dataProperties = dataProperties;
		this.annotProperties = annotProperties;
		this.individual = individual;
		this.rdfsClasses = rdfsClasses;
		this.rdfProperties = rdfProperties;
	}


	public String getOntologyName() {
		return ontologyName;
	}
	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}
	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
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


	public List<Entity> getRdfsClasses() {
		return rdfsClasses;
	}


	public void setRdfsClasses(List<Entity> rdfsClasses) {
		this.rdfsClasses = rdfsClasses;
	}

	public void addRDFSClass(Entity clas) {
		if(this.rdfsClasses == null){
			this.rdfsClasses = new ArrayList<Entity>();
		}
		this.rdfsClasses.add(clas);
	}

	public List<Entity> getRdfProperties() {
		return rdfProperties;
	}


	public void setRdfProperties(List<Entity> rdfProperties) {
		this.rdfProperties = rdfProperties;
	}
	
	public void addRDFProperty(Entity prop) {
		if(this.rdfProperties == null){
			this.rdfProperties = new ArrayList<Entity>();
		}
		this.rdfProperties.add(prop);
	}


	public String getOntologyDocBaseURL() {
		return ontologyDocBaseURL;
	}


	public void setOntologyDocBaseURL(String ontologyDocBaseURL) {
		this.ontologyDocBaseURL = ontologyDocBaseURL;
	}


	public String getOntologyDocExactURL() {
		return ontologyDocExactURL;
	}


	public void setOntologyDocExactURL(String ontologyDocExactURL) {
		this.ontologyDocExactURL = ontologyDocExactURL;
	}

}

