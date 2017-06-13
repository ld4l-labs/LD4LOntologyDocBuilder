package edu.cornell.ld4l.ontologydomainmodelbuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import edu.cornell.ld4l.configuration.Configuration;
import edu.cornell.ld4l.entrypoint.LD4LOntologyModularizerEntryPoint;

public class OntologyDomainModulesBuilder {

	private static final String BIBO = 		"http://purl.org/ontology/bibo/";
	private static final String C4O =  		"http://purl.org/spar/c4o/";
	private static final String CITO = 		"http://purl.org/spar/cito/";
	private static final String DC = 		"http://purl.org/dc/";
	private static final String EVENT = 	"http://purl.org/NET/c4dm/event.owl#";
	private static final String FABIO = 	"http://purl.org/spar/fabio/";
	private static final String FOAF = 		"http://xmlns.com/foaf/0.1/";
	private static final String GEO = 		"http://aims.fao.org/aos/geopolitical.owl#";
	private static final String OBO = 		"http://purl.obolibrary.org/obo/";
	private static final String OCRE_RSCH = "http://purl.org/net/OCRe/research.owl#";
	private static final String OCRE_SD = 	"http://purl.org/net/OCRe/study_design.owl#";
	private static final String OCRE_SP = 	"http://purl.org/net/OCRe/study_protocol.owl#";
	private static final String OCRE_STAT = "http://purl.org/net/OCRe/statistics.owl#";
	private static final String RO = 		"http://www.obofoundry.org/ro/ro.owl#";
	private static final String SKOS = 		"http://www.w3.org/2004/02/skos/core#";
	private static final String SOFTWARE = 	"http://www.ebi.ac.uk/efo/swo/";
	private static final String VANN = 		"http://purl.org/vocab/vann/";
	private static final String VCARD = 	"http://www.w3.org/2006/vcard/ns#";
	private static final String VITRO = 	"http://vitro.mannlib.cornell.edu/ns/vitro/public#";
	private static final String VIVOCORE = 	"http://vivoweb.org/ontology/core#";

	private OWLOntology owlOntology = null;
	private OWLOntologyManager manager = null;
	private Map<String, String> owlMap = new HashMap<String, String>();  //<IRI, Type>
	private List<DomainEntry> entrylist = new ArrayList<DomainEntry>();
	private Map<String, DomainEntry> descripMap = new HashMap<String, DomainEntry>();

	private String ONTOLOGY_FILE = null;
	private String DOMAIN_MODEL_DESCRIP_FILE = null;
	private String NT_FILE_FOLDER = null;
	private String OUTPUT_FILE = null;

	public static void main(String[] args) {
		OntologyDomainModulesBuilder obj = new OntologyDomainModulesBuilder();
		try {
			LD4LOntologyModularizerEntryPoint.init("resources/setup.properties");
			obj.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setLocalDirectories() {
		ONTOLOGY_FILE = Configuration.INPUT_FOLDER +"/"+ Configuration.date +"/"+Configuration.ONTOLOGY_FILENAME;
		DOMAIN_MODEL_DESCRIP_FILE = Configuration.INPUT_FOLDER +"/"+ Configuration.date +"/"+Configuration.DOMAIN_MODEL_DESCRIPTION_FILENAME;
		NT_FILE_FOLDER = Configuration.INPUT_FOLDER +"/"+ Configuration.date +"/"+Configuration.NT_FILES_FOLDERNAME;
		OUTPUT_FILE = Configuration.OUTPUT_FOLDER +"/"+ Configuration.date +"/"+Configuration.OUTPUT_MODELS_FILENAME;
	}

	public void runProcess() throws IOException{
		setLocalDirectories();
		processOntologyFile(ONTOLOGY_FILE);
		descripMap = processDescriptionCSVFile(DOMAIN_MODEL_DESCRIP_FILE);
		processFiles(NT_FILE_FOLDER);
		createJSONFile(entrylist, OUTPUT_FILE);
	}

	private void processOntologyFile(String filePath) {
		manager = OWLManager.createOWLOntologyManager();
		IRI inputDocumentIRI = IRI.create(new File(filePath));
		try {
			owlOntology = manager.loadOntologyFromOntologyDocument(inputDocumentIRI);

			for (OWLClass cl : owlOntology.getClassesInSignature()) {
				assert cl != null;
				owlMap.put(cl.getIRI().toString(), "Class");
			}
			for (OWLObjectProperty objprop : owlOntology.getObjectPropertiesInSignature()) {
				assert objprop != null;
				owlMap.put(objprop.getIRI().toString(), "Object Property");
			}
			for (OWLDataProperty dataprop : owlOntology.getDataPropertiesInSignature()) {
				assert dataprop != null;
				owlMap.put(dataprop.getIRI().toString(), "Datatype Property");
			}
			for (OWLAnnotationProperty annotprop : owlOntology.getAnnotationPropertiesInSignature()) {
				assert annotprop != null;
				owlMap.put(annotprop.getIRI().toString(), "Annotation Property");
			}
			for (OWLNamedIndividual indv : owlOntology.getIndividualsInSignature()) {
				assert indv != null;
				owlMap.put(indv.getIRI().toString(), "Instance");
			}

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		System.out.println("Total Ontology Map siz: "+owlMap.size());
	}

	private Map<String, DomainEntry> processDescriptionCSVFile(String file) throws IOException {
		Map<String, DomainEntry> map = new HashMap<String, DomainEntry>();
		CSVReader reader;
		reader = new CSVReader(new FileReader(file),',','\"');
		String [] nextLine;	
		reader.readNext();  // header
		while ((nextLine = reader.readNext()) != null) {
			DomainEntry entry = new DomainEntry();
			entry.setModelTitle(nextLine[0]);
			entry.setImage(nextLine[1]);
			entry.setModelDescription(nextLine[2]);
			map.put(entry.getModelTitle().toLowerCase(), entry);
		}
		reader.close();
		return map;
	}


	private void processFiles(String inputFolder) {
		File folder = new File(inputFolder);
		File files[] = folder.listFiles();
		for(File file: files){
			processNTFile(file);
		}
	}

	private void createJSONFile(Collection<DomainEntry> collection, String filePath){
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		try {
			mapper.writeValue(new File(filePath), collection);
			jsonInString = mapper.writeValueAsString(collection);
			//System.out.println(jsonInString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processNTFile(File file) {
		Set<String> depenOnt = new HashSet<String>();
		String fileName = file.getName();
		if(fileName.startsWith(".")) return;
		
		DomainEntry entry = new DomainEntry();
		setDesciptionData(entry, fileName);

		Set<Entity> entities = new HashSet<Entity>();

		Model model = ModelFactory.createDefaultModel();
		model.read(file.getAbsolutePath()) ;

		StmtIterator stmtIt = model.listStatements();
		for(;stmtIt.hasNext();){
			Statement stmt = stmtIt.nextStatement();

			Resource sub = stmt.getSubject();
			if(owlMap.get(sub.getURI())!= null){
				System.out.println(sub.getURI() +"["+ owlMap.get(sub.getURI())+"]");
				if(!getOntology(sub.getNameSpace()).isEmpty()){
					Entity entity = new Entity(sub.getLocalName(), owlMap.get(sub.getURI()), sub.getURI(), getOntology(sub.getNameSpace()));
					entities.add(entity);
					depenOnt.add(getOntology(sub.getNameSpace()));
				}
			}

			Property pred = stmt.getPredicate();
			if(owlMap.get(pred.getURI())!= null){
				System.out.println(pred.getURI() +"["+ owlMap.get(pred.getURI())+"]");
				if(!getOntology(pred.getNameSpace()).isEmpty()){
					Entity entity = new Entity(pred.getLocalName(), owlMap.get(pred.getURI()), pred.getURI(), getOntology(pred.getNameSpace()));
					entities.add(entity);
					depenOnt.add(getOntology(pred.getNameSpace()));
				}
			}
			RDFNode node = stmt.getObject();
			if(node.isResource()){
				Resource obj = (Resource) node;
				if(owlMap.get(obj.getURI())!= null){
					System.out.println(obj.getURI() +"["+ owlMap.get(obj.getURI())+"]");
					if(!getOntology(obj.getNameSpace()).isEmpty()){
						Entity entity = new Entity(obj.getLocalName(), owlMap.get(obj.getURI()), obj.getURI(), getOntology(obj.getNameSpace()));
						entities.add(entity);
						depenOnt.add(getOntology(obj.getNameSpace()));
					}
				}
			}
		}
		entry.setEntities(entities);
		entry.setDepenOnt(depenOnt);
		entrylist.add(entry);
	}

	private String getOntology(String uri) {
		String ontology = null;

		switch(uri){
		case BIBO:	 	ontology = "bibo"; 		break;
		case C4O: 		ontology = "c4o";		break;
		case CITO: 		ontology = "cito";		break;
		case DC: 		ontology = "dc";		break;
		case EVENT: 	ontology = "event";		break;
		case FABIO: 	ontology = "fabio";		break;
		case FOAF: 		ontology = "foaf"; 		break;
		case GEO: 		ontology = "geo"; 		break;
		case OBO: 		ontology = "obo"; 		break;
		case OCRE_RSCH: ontology = "ocre_rsch"; break;
		case OCRE_SD: 	ontology = "ocre_sd"; 	break;
		case OCRE_SP: 	ontology = "ocre_sp"; 	break;
		case OCRE_STAT: ontology = "ocre_stat"; break;
		case RO: 		ontology = "ro"; 		break;
		case SKOS: 		ontology = "skos";		break;
		case SOFTWARE: 	ontology = "software";	break;
		case VANN: 		ontology = "vann";		break;
		case VCARD: 	ontology = "vcard";		break;
		case VITRO: 	ontology = "vitro";		break;
		case VIVOCORE:	ontology = "vivocore";	break;
		}

		return ontology != null ? ontology.toUpperCase():"";
	}

	private void setDesciptionData(DomainEntry entry, String fileName) {
		String modelName = getModelName(fileName);
		DomainEntry ent = descripMap.get(modelName.toLowerCase());
		entry.setModelTitle(ent.getModelTitle());
		entry.setModelDescription(ent.getModelDescription());
		entry.setImage(ent.getImage());
	}

	private String getModelName(String fileName) {
		return fileName.substring(0, fileName.indexOf('.'));
	}
}
