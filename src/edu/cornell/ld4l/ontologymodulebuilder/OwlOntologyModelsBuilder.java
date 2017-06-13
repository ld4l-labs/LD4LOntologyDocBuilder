package edu.cornell.ld4l.ontologymodulebuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import edu.cornell.ld4l.configuration.Configuration;
import edu.cornell.ld4l.entrypoint.LD4LOntologyModularizerEntryPoint;

public class OwlOntologyModelsBuilder {
	
	private static final String	SW_VOCAB = "http://www.w3.org/2003/06/sw-vocab-status/ns#";
	private static final String PROV = "http://www.w3.org/ns/prov#";
	private static final String FOAF = "http://xmlns.com/foaf/0.1/";
	private static final String REGAP = "http://metadataregistry.org/uri/profile/regap/";
	private static final String DC	= "http://purl.org/dc/";
	private static final String VANN ="http://purl.org/vocab/vann/";
	private static final String LD4L ="http://bib.ld4l.org/ontology/";
	private static final String SCHEMA ="http://schema.org/";
	private static final String RDA ="http://rdaregistry.info/Elements/u/";
	private static final String BF ="http://id.loc.gov/ontologies/bibframe/";
	private static final String CIDOC_CRM = "http://www.cidoc-crm.org/cidoc-crm/";
	private static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
	private static final String VIVOCORE = 	"http://vivoweb.org/ontology/core#";
	
	private TreeMap<String, List<OWLClass>> cls_map = new TreeMap<String, List<OWLClass>>();
	private TreeMap<String, List<OWLObjectProperty>> objprop_map = new TreeMap<String, List<OWLObjectProperty>>();
	private TreeMap<String, List<OWLDataProperty>> datatypeprop_map = new TreeMap<String, List<OWLDataProperty>>();
	private TreeMap<String, List<OWLAnnotationProperty>>  annotprop_map = new TreeMap<String, List<OWLAnnotationProperty>>();
	private TreeMap<String, List<OWLNamedIndividual>> indprop_map = new TreeMap<String, List<OWLNamedIndividual>>();
	private List<Ontology> ontologyList = null;
	private OWLOntology owlOntology = null;
	private Map<String, List<String>> map = new HashMap<String, List<String>>();
	private Map<String, String> lodeMap = null;
	private String [] namespaces = {LD4L, BF, DC, FOAF, RDA, PROV, CIDOC_CRM, REGAP, SW_VOCAB, VANN,
		SCHEMA, SKOS, VIVOCORE};

	private String LODE_FILE = null;
	private String ONTOLOGY_VERSION_FILE = null;
	private String ONTOLOGY_FILE = null;
	private String OUTPUT_FILE = null;

	public static void main(String args[]){
		OwlOntologyModelsBuilder obj = new OwlOntologyModelsBuilder();
		try {
			String propFilePath = "resources/setup.properties";
			LD4LOntologyModularizerEntryPoint.init(propFilePath);
			obj.runProcess();
		} catch (OWLOntologyCreationException | ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	public void runProcess() throws ParserConfigurationException, SAXException, IOException, OWLOntologyCreationException{
		setLocalDirectories();
		lodeMap = LODEPageSourceReader.readLODEClasses(new File(LODE_FILE));
		processontologyversionfile(ONTOLOGY_VERSION_FILE);
		processFile(ONTOLOGY_FILE);
	}


	private void setLocalDirectories() {
		ONTOLOGY_FILE = Configuration.INPUT_FOLDER +"/"+ Configuration.date +"/"+Configuration.ONTOLOGY_FILENAME;
		ONTOLOGY_VERSION_FILE = Configuration.INPUT_FOLDER +"/"+ Configuration.date +"/"+Configuration.ONTOLOGY_VER_CSV_FILE;
		LODE_FILE = Configuration.INPUT_FOLDER +"/"+ Configuration.date +"/"+Configuration.LODE_FILE;
		OUTPUT_FILE = Configuration.OUTPUT_FOLDER +"/"+ Configuration.date +"/"+Configuration.OUTPUT_MODULES_FILENAME;
	}

	private void processontologyversionfile(String filePath) {
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(new File(filePath)),',','\"');
			String [] nextLine;	
			reader.readNext();  // header
			while ((nextLine = reader.readNext()) != null) {
				List<String> list = new ArrayList<String>();
				list.add(nextLine[0].trim());
				list.add(nextLine[1].trim());
				list.add(nextLine[2].trim());
				list.add(nextLine[3].trim());
				map.put(nextLine[0].trim(), list);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(map.size());
	}

	private void processFile(String filePath) throws OWLOntologyCreationException {
		ontologyList = new ArrayList<Ontology>();
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		IRI inputDocumentIRI = IRI.create(new File(filePath));
		owlOntology = m.loadOntologyFromOntologyDocument(inputDocumentIRI);

		System.out.println("Total Axioms: "+owlOntology.getAxiomCount());

		for(String ns: namespaces){
			createClassMap(ns, owlOntology);
			createObjectPropertyMap(ns, owlOntology);
			createDatatypePropertyMap(ns, owlOntology);
			creatAnnotationPropertyMap(ns, owlOntology);
			createIndividualMap(ns, owlOntology);
		}

		saveDataInJSONFile();

	}

	private void saveDataInJSONFile() {
		String code ="";
		for(String ns: namespaces){
			code ="";
			switch(ns){
			
			case DC:	code = "dc"; 		break;
			case SW_VOCAB: 	code = "sw_vocab";		break;
			case PROV: 		code = "prov";		break;
			case FOAF: 		code = "foaf";		break;
			case REGAP: 	code = "regap";		break;
			case VANN: 		code = "vann"; 		break;
			case LD4L: 		code = "ld4l"; 		break;
			case SCHEMA: 	code = "schema"; 		break;
			case RDA:		code = "rda"; break;
			case BF: 		code = "bf"; 	break;
			case CIDOC_CRM: code = "cidoc_crm"; 	break;
			case SKOS: 		code = "skos";		break;
			case VIVOCORE:	code = "vivocore";	break;
			}
			if(code.isEmpty()) continue;

			List<OWLClass> clsList = cls_map.get(ns);
			List<OWLObjectProperty> objList = objprop_map.get(ns);
			List<OWLDataProperty> dataList= datatypeprop_map.get(ns);
			List<OWLAnnotationProperty> annotList = annotprop_map.get(ns);
			List<OWLNamedIndividual> indvList= indprop_map.get(ns);
			saveData(ns, code, clsList, objList, dataList, annotList, indvList);
		}

		try {
			createJSONFile(ontologyList, OUTPUT_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createJSONFile(Collection<Ontology> collection, String filePath) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		mapper.writeValue(new File(filePath), collection);
		jsonInString = mapper.writeValueAsString(collection);
		//System.out.println(jsonInString);
	}

	private void saveData(String namespace, String code, List<OWLClass> cList, List<OWLObjectProperty> oList, List<OWLDataProperty> dList, List<OWLAnnotationProperty> aList, List<OWLNamedIndividual> iList) {
		Ontology ont = new Ontology();

		List<String> metadata = map.get(code);
		if(metadata != null){
			ont.setCode(metadata.get(0));
			ont.setOntologyName(metadata.get(1));
			ont.setNamespace(metadata.get(2));
			ont.setOntologyVersion(metadata.get(3));
		}

		for(OWLClass cls : cList){
			String localname = getIRIFragment(cls.getIRI());
			String uri = cls.getIRI().toString();
			String lodeLink = getLODELink(cls.getIRI());
			Entity e = new Entity(localname, uri, lodeLink);
			ont.addClass(e);
		}
		if(ont.getClasses()!= null && ont.getClasses().size() > 0){
			Collections.sort(ont.getClasses(), new Comparator<Entity>() {
				public int compare(Entity o1, Entity o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
		}

		for(OWLObjectProperty objProp : oList){
			String localname = getIRIFragment(objProp.getIRI());
			String uri = objProp.getIRI().toString();
			String lodeLink = getLODELink(objProp.getIRI());
			Entity e = new Entity(localname, uri, lodeLink);
			ont.addObjProperty(e);
		}
		if(ont.getObjProperties()!= null && ont.getObjProperties().size() > 0){
			Collections.sort(ont.getObjProperties(), new Comparator<Entity>() {
				public int compare(Entity o1, Entity o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
		}

		for(OWLDataProperty dataProp : dList){
			String localname = getIRIFragment(dataProp.getIRI());
			String uri = dataProp.getIRI().toString();
			String lodeLink = getLODELink(dataProp.getIRI());
			Entity e = new Entity(localname, uri, lodeLink);
			ont.addDataProperty(e);
		}
		if(ont.getDataProperties()!= null && ont.getDataProperties().size() > 0){
			Collections.sort(ont.getDataProperties(), new Comparator<Entity>() {
				public int compare(Entity o1, Entity o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
		}

		for(OWLAnnotationProperty aProp : aList){
			String localname = getIRIFragment(aProp.getIRI());
			String uri = aProp.getIRI().toString();
			String lodeLink = getLODELink(aProp.getIRI());
			Entity e = new Entity(localname, uri, lodeLink);
			ont.addAnnotationProperty(e);
		}
		if(ont.getAnnotProperties()!= null && ont.getAnnotProperties().size() > 0){
			Collections.sort(ont.getAnnotProperties(), new Comparator<Entity>() {
				public int compare(Entity o1, Entity o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
		}

		for(OWLNamedIndividual ind : iList){
			String localname = getIRIFragment(ind.getIRI());
			String uri = ind.getIRI().toString();
			String lodeLink = getLODELink(ind.getIRI());
			Entity e = new Entity(localname, uri, lodeLink);
			ont.addIndividual(e);
		}
		if(ont.getIndividuals()!= null && ont.getIndividuals().size() > 0){
			Collections.sort(ont.getIndividuals(), new Comparator<Entity>() {
				public int compare(Entity o1, Entity o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
		}

		ontologyList.add(ont);
	}

	private String getLODELink(IRI iri) {
		String href = lodeMap.get(iri.toString());
		if (href == null){
			System.err.println("No LODE LINK: "+iri.toString());
		}
		return href!=null?href:"";
	}

	private String getRDFLabel(IRI iri){
		String label = "";
		for(OWLAnnotationAssertionAxiom a : owlOntology.getAnnotationAssertionAxioms(iri)) {
			if(a.getProperty().isLabel()) {
				if(a.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) a.getValue();
					label = val.getLiteral();
					//System.out.println(val.getLiteral().toUpperCase());
				}
			}
		}
		return label;
	}

	private String getIRIFragment(IRI iri){
		String fragment = "";
		String iriStr =iri.toString();
		if(iriStr.contains("#")){
			fragment = (iriStr.substring(iriStr.lastIndexOf('#')+1));
		}else{
			fragment = (iriStr.substring(iriStr.lastIndexOf('/')+1));
		}  

		//TODO Making spacing between camel case capitalized fragments

		return fragment;
	}

	private void createClassMap(String namespace, OWLOntology ontology) {
		Set<OWLClass> iris = new HashSet<OWLClass>();
		for (OWLClass cl : ontology.getClassesInSignature()) {
			assert cl != null;
			if(cl.getIRI().toString().startsWith(namespace)){
				iris.add(cl);
			}
		}
		List<OWLClass> list = new ArrayList<OWLClass>(iris);
		cls_map.put(namespace, list);
		//		System.out.println(namespace);
		//		for(OWLClass tt : list){
		//			System.out.print(tt.getIRI().toString()+", ");
		//		}
		//		System.out.println("\n\n");
	}


	private void createObjectPropertyMap(String namespace, OWLOntology ontology) {
		Set<OWLObjectProperty> iris = new HashSet<OWLObjectProperty>();
		for (OWLObjectProperty objprop : ontology.getObjectPropertiesInSignature()) {
			assert objprop != null;
			if(objprop.getIRI().toString().startsWith(namespace)){
				iris.add(objprop);
			}
		}
		List<OWLObjectProperty> list = new ArrayList<OWLObjectProperty>(iris);
		objprop_map.put(namespace, list);

		//		System.out.println(namespace);
		//		for(OWLObjectProperty tt : list){
		//			System.out.print(tt.getIRI().toString());
		//		}
		//		System.out.println("\n\n");
	}


	private void creatAnnotationPropertyMap(String namespace, OWLOntology ontology) {
		Set<OWLAnnotationProperty> iris = new HashSet<OWLAnnotationProperty>();
		for (OWLAnnotationProperty annotprop : ontology.getAnnotationPropertiesInSignature()) {
			assert annotprop != null;
			if(annotprop.getIRI().toString().startsWith(namespace)){
				iris.add(annotprop);
			}
		}
		List<OWLAnnotationProperty> list = new ArrayList<OWLAnnotationProperty>(iris);
		annotprop_map.put(namespace, list);
	}


	private void createDatatypePropertyMap(String namespace, OWLOntology ontology) {
		Set<OWLDataProperty> iris = new HashSet<OWLDataProperty>();
		for (OWLDataProperty dataprop : ontology.getDataPropertiesInSignature()) {
			assert dataprop != null;
			if(dataprop.getIRI().toString().startsWith(namespace)){
				iris.add(dataprop);
			}
		}
		List<OWLDataProperty> list = new ArrayList<OWLDataProperty>(iris);
		datatypeprop_map.put(namespace, list);

		System.out.println(namespace);
		//		for(OWLDataProperty tt : list){
		//			//System.out.print(tt.getIRI().toString()+", ");
		//		}
		//		System.out.println("\n\n");
	}

	private void createIndividualMap(String namespace, OWLOntology ontology) {
		Set<OWLNamedIndividual> iris = new HashSet<OWLNamedIndividual>();
		for (OWLNamedIndividual indv : ontology.getIndividualsInSignature()) {
			assert indv != null;
			if(indv.getIRI().toString().startsWith(namespace)){
				iris.add(indv);
			}
		}
		List<OWLNamedIndividual> list = new ArrayList<OWLNamedIndividual>(iris);
		indprop_map.put(namespace, list);

		//		System.out.println(namespace);
		//		for(OWLNamedIndividual tt : list){
		//			System.out.print(tt.getIRI().toString()+", ");
		//		}
		//		System.out.println("\n\n");
	}



}
