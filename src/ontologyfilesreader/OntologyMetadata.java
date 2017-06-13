package ontologyfilesreader;

public class OntologyMetadata {

	private String prefix = null;
	private String ontologyName = null;
	private String namespace = null;
	private String ontologyDocBaseURL = null;
	private String ontologyDocExactURL = null;
	
	
	
	public OntologyMetadata(String prefix, String ontologyName, String namespace, String ontologyDocBaseURL, String ontologyDocExactURL) {
		super();
		this.prefix = prefix;
		this.ontologyName = ontologyName;
		this.namespace = namespace;
		this.ontologyDocBaseURL = ontologyDocBaseURL;
		this.setOntologyDocExactURL(ontologyDocExactURL);
	}
	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getOntologyName() {
		return ontologyName;
	}
	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
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
