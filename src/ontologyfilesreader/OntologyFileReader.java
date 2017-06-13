package ontologyfilesreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import edu.cornell.ld4l.entrypoint.LD4LOntologyModularizerEntryPoint;

public class OntologyFileReader {

	private static final String BF ="http://id.loc.gov/ontologies/bibframe/";
	private static final String CIDOC_CRM = "http://www.cidoc-crm.org/cidoc-crm/";
	private static final String DC_ELEMENT	= "http://purl.org/dc/elements/1.1/";
	private static final String DC_TERM	= "http://purl.org/dc/terms/";
	private static final String FOAF = "http://xmlns.com/foaf/0.1/";
	private static final String GEO = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String BIBLIOTEK = "http://bibliotek-o.org/ontology/";
	private static final String LINGVOJ = "http://www.lingvoj.org/ontology#";
	private static final String OA = "http://www.w3.org/ns/oa#";
	private static final String PROV = "http://www.w3.org/ns/prov#";
	private static final String RDA ="http://rdaregistry.info/Elements/u/";
	private static final String SCHEMA ="http://schema.org/";
	private static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
	private static final String VIVOCORE = 	"http://vivoweb.org/ontology/core#";

	private Map<String, OWLRDFOntology> ontologyList = null;
	private Map<String, OntologyMetadata> ontologyMetadata = null;
	private String inputPath = null;
	private String outputPath = null;
	
	private static final Logger LOGGER = Logger.getLogger(OntologyFileReader.class.getName());

	
	public static void main(String args[]){
		OntologyFileReader obj = new OntologyFileReader();
		
		LOGGER.info("Parameters length: "+Integer.toString(args.length));
		
		if(args.length >0 && !args[0].isEmpty()){
			obj.inputPath = args[0];
		}else{
			LOGGER.warning("Target Ontologies folder path required:");
			LOGGER.warning("Example: java -jar ld4ldoc.jar /Users/mj495/Documents/LD4L/target-ontologies/");
			return;
		}
		
		if(args.length >1 && !args[1].isEmpty()){
			obj.outputPath = args[1];
		}else{
			LOGGER.warning("Output folder path not given: args[1]");
			LOGGER.warning("modules.json file will be saved in folder where jar file exists.");
			obj.outputPath = "modules.json";
		}
		
		try {
			obj.ontologyMetadata = obj.readMetadataFile("ld4lontologydoc.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		obj.readFiles(obj.inputPath, obj.outputPath);
	}

	private Map<String, OntologyMetadata> readMetadataFile(String filePath) throws IOException {	
		Map<String, OntologyMetadata> map =  new HashMap<String, OntologyMetadata>();
		BufferedReader br = null;
		String line = "";
		long lineCount = 0;
		br = new BufferedReader(new FileReader(new File(filePath)));
		while ((line = br.readLine()) != null) {
			lineCount++;
			if(line.trim().length() == 0) continue;
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new StringReader(line),',','\"');
			String[] tokens;
			while ((tokens = reader.readNext()) != null) {
				try {
					if (lineCount == 1) continue; // header line
					String prefix = tokens[0];
					String ontName = tokens[1];
					String ontNamespace = tokens[2];
					String docBaseURL = tokens[3];
					String docExactURL = tokens[4];
					map.put(ontNamespace, new OntologyMetadata(prefix,ontName, ontNamespace, docBaseURL, docExactURL));
				}catch (ArrayIndexOutOfBoundsException exp) {
					for (String s : tokens) {
						System.out.println("ArrayIndexOutOfBoundsException: "+ lineCount+" :"+ s);
					}
				}
			}
		}
		br.close();
		return map;
	}

	private void readFiles(String inputPath, String outputPath) {

		ontologyList = new HashMap<String, OWLRDFOntology>();
		String OUTPUT_FILE = outputPath;

		File folder = new File(inputPath);
		if(folder.isDirectory()){
			File files[] = folder.listFiles();
			for(File file : files){
				if(file.getName().startsWith(".") || file.getName().endsWith(".xml")) continue;
				System.out.println("\n\nReading File:"+ file.getName());
				processFile(file.getAbsolutePath());
			}
		}

		try {
			Collection<OWLRDFOntology> col = ontologyList.values();
			List<OWLRDFOntology> list = new ArrayList<OWLRDFOntology>(col);
			Collections.sort(list, new SortingFunction());
			createJSONFile(list, OUTPUT_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createJSONFile(List<OWLRDFOntology> collection, String filePath) throws JsonGenerationException, JsonMappingException, IOException{

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		mapper.writeValue(new File(filePath), collection);
		jsonInString = mapper.writeValueAsString(collection);
		//System.out.println(jsonInString);
	}

	private void processFile(String filePath) {

		Set<String> namespaces = new HashSet<String>();
		Map<String, List<Resource>> map = new HashMap<String, List<Resource>>();

		Model model = ModelFactory.createDefaultModel() ;
		model.read(filePath);
		Set<Resource> set = new HashSet<Resource>();

		//System.out.println("\n\nOWL Classes");
		ResIterator cls = model.listSubjectsWithProperty(RDF.type, OWL.Class);
		Set<Resource> clsSet = new HashSet<Resource>();
		while (cls.hasNext()) {
			Resource r = cls.nextResource();
			namespaces.add(r.getNameSpace());
			//System.out.println(r.getURI());
			set.add(r);
			clsSet.add(r);
		}

		List<Resource> clsList = new ArrayList<Resource>(clsSet);
		if(clsSet != null && clsSet.size() > 0){
			Collections.sort(clsList, new Comparator<Resource>() {
				public int compare(Resource o1, Resource o2) {
					if(o1.getLocalName() != null && o2.getLocalName() != null){
						return o1.getLocalName().compareTo(o2.getLocalName());
					}else{
						return 0;
					}

				}
			});
		}
		map.put("owlClasses", clsList);


		//System.out.println("\n\nRDFS Classes");
		ResIterator rdfscls = model.listSubjectsWithProperty(RDF.type, RDFS.Class);
		Set<Resource> rdfsClsSet = new HashSet<Resource>();
		while (rdfscls.hasNext()) {
			Resource r = rdfscls.nextResource();
			namespaces.add(r.getNameSpace());
			//System.out.println(r.getURI());
			set.add(r);
			if(!clsSet.contains(r)){
				rdfsClsSet.add(r);
			}	
		}

		List<Resource> rdfsList = new ArrayList<Resource>(rdfsClsSet);
		if(rdfsList != null && rdfsList.size() > 0){
			Collections.sort(rdfsList, new Comparator<Resource>() {
				public int compare(Resource o1, Resource o2) {
					if(o1.getLocalName() != null && o2.getLocalName() != null){
						return o1.getLocalName().compareTo(o2.getLocalName());
					}else{
						return 0;
					}

				}
			});
		}
		map.put("rdfsClasses", rdfsList);


		//System.out.println("\n\nOWL Object Properties");
		ResIterator objprop = model.listSubjectsWithProperty(RDF.type, OWL.ObjectProperty);
		Set<Resource> objPropSet = new HashSet<Resource>();
		while (objprop.hasNext()) {
			Resource r = objprop.nextResource();
			namespaces.add(r.getNameSpace());
			//System.out.println(r.getURI());
			set.add(r);
			objPropSet.add(r);
		}

		//System.out.println("\n\nSymmetric Object Properties");
		ResIterator symmprop = model.listSubjectsWithProperty(RDF.type, OWL.SymmetricProperty);
		while (symmprop.hasNext()) {
			Resource r = symmprop.nextResource();
			namespaces.add(r.getNameSpace());
			//System.out.println(r.getURI());
			set.add(r);
			objPropSet.add(r);  // adding symmetric properties within object property list
		}

		List<Resource> objPropList = new ArrayList<Resource>(objPropSet);
		if(objPropList != null && objPropList.size() > 0){
			Collections.sort(objPropList, new Comparator<Resource>() {
				public int compare(Resource o1, Resource o2) {
					if(o1.getLocalName() != null && o2.getLocalName() != null){
						return o1.getLocalName().compareTo(o2.getLocalName());
					}else{
						return 0;
					}

				}
			});
		}
		map.put("objProperties", objPropList);


		//System.out.println("\n\nOWL Data Properties");
		ResIterator dataprop = model.listSubjectsWithProperty(RDF.type, OWL.DatatypeProperty);
		Set<Resource> dataPropSet = new HashSet<Resource>();
		while (dataprop.hasNext()) {
			Resource r = dataprop.nextResource();
			namespaces.add(r.getNameSpace());
			//System.out.println(r.getURI());
			set.add(r);
			dataPropSet.add(r);
		}
		List<Resource> dataPropList = new ArrayList<Resource>(dataPropSet);
		if(dataPropList != null && dataPropList.size() > 0){
			Collections.sort(dataPropList, new Comparator<Resource>() {
				public int compare(Resource o1, Resource o2) {
					if(o1.getLocalName() != null && o2.getLocalName() != null){
						return o1.getLocalName().compareTo(o2.getLocalName());
					}else{
						return 0;
					}

				}
			});
		}
		map.put("dataProperties", dataPropList);


		//System.out.println("\n\nOWL Annotation Properties");
		ResIterator annotProp = model.listSubjectsWithProperty(RDF.type, OWL.AnnotationProperty);
		Set<Resource> annotPropSet = new HashSet<Resource>();
		while (annotProp.hasNext()) {
			Resource r = annotProp.nextResource();
			namespaces.add(r.getNameSpace());
			//System.out.println(r.getURI());
			set.add(r);
			annotPropSet.add(r);
		}
		List<Resource> annotPropList = new ArrayList<Resource>(annotPropSet);
		if(annotPropList != null && annotPropList.size() > 0){
			Collections.sort(annotPropList, new Comparator<Resource>() {
				public int compare(Resource o1, Resource o2) {
					if(o1.getLocalName() != null && o2.getLocalName() != null){
						return o1.getLocalName().compareTo(o2.getLocalName());
					}else{
						return 0;
					}

				}
			});
		}
		map.put("annotProperties", annotPropList);

		//System.out.println("\n\nRDF Properties");
		ResIterator prop = model.listSubjectsWithProperty(RDF.type, RDF.Property);
		Set<Resource> rdfPropSet = new HashSet<Resource>();
		while (prop.hasNext()) {
			Resource r = prop.nextResource();
			namespaces.add(r.getNameSpace());
			//System.out.println(r.getURI());
			set.add(r);
			if(!objPropSet.contains(r) && !dataPropSet.contains(r) && !annotPropSet.contains(r)){
				rdfPropSet.add(r);
			}
		}
		List<Resource> rdfPropList = new ArrayList<Resource>(rdfPropSet);
		if(rdfPropList != null && rdfPropList.size() > 0){
			Collections.sort(rdfPropList, new Comparator<Resource>() {
				public int compare(Resource o1, Resource o2) {
					if(o1.getLocalName() != null && o2.getLocalName() != null){
						return o1.getLocalName().compareTo(o2.getLocalName());
					}else{
						return 0;
					}

				}
			});
		}
		map.put("rdfProperties", rdfPropList);

		//System.out.println("\n\nOWL Individuals");
		ResIterator ind = model.listSubjectsWithProperty(RDF.type);
		Set<Resource> indvSet = new HashSet<Resource>();
		while (ind.hasNext()) {
			Resource r = ind.nextResource();
			if(!set.contains(r) && r.getNameSpace()!= null && !r.getNameSpace().isEmpty()){
				namespaces.add(r.getNameSpace());
				//System.out.println(r.getURI());
				indvSet.add(r);
			}
		}
		List<Resource> indvList = new ArrayList<Resource>(indvSet);
		if(indvList != null && indvList.size() > 0){
			Collections.sort(indvList, new Comparator<Resource>() {
				public int compare(Resource o1, Resource o2) {
					if(o1.getLocalName() != null && o2.getLocalName() != null){
						return o1.getLocalName().compareTo(o2.getLocalName());
					}else{
						return 0;
					}

				}
			});
		}
		map.put("individuals", indvList);

		for(String n: namespaces) {
			OWLRDFOntology ont = null;
			System.out.println("Namespace: "+n);
			if(ontologyMetadata.get(n) == null) {
				System.err.println(n+" namespace not found in csv metadata file...continuing.");
				continue;
			}
			if(ontologyList.get(ontologyMetadata.get(n).getPrefix()) == null){
				ont = new OWLRDFOntology();
				ont.setPrefix(ontologyMetadata.get(n).getPrefix());
				ont.setNamespace(ontologyMetadata.get(n).getNamespace());
				ont.setOntologyName(ontologyMetadata.get(n).getOntologyName());
				ont.setOntologyDocBaseURL(ontologyMetadata.get(n).getOntologyDocBaseURL());
				ont.setOntologyDocExactURL(ontologyMetadata.get(n).getOntologyDocExactURL());
			}else{
				ont = ontologyList.get(ontologyMetadata.get(n).getPrefix());
			}
			saveData(ont, map);
		}
	}

	private void saveData(OWLRDFOntology ont, Map<String, List<Resource>> map) {

		List<Resource> clsSet = map.get("owlClasses");
		for(Resource r : clsSet){
			if(r.getLocalName().isEmpty()) continue;
			ont.addClass(new Entity(r.getLocalName(), r.getURI(), getLink(r, ont, "owlClasses")));
		}

		List<Resource> rdfsClsSet = map.get("rdfsClasses");
		for(Resource r : rdfsClsSet){
			if(r.getLocalName().isEmpty()) continue;
			ont.addRDFSClass(new Entity(r.getLocalName(), r.getURI(), getLink(r, ont, "rdfsClasses")));
		}

		List<Resource> rdfPropSet = map.get("rdfProperties");
		for(Resource r : rdfPropSet){
			if(r.getLocalName().isEmpty()) continue;
			ont.addRDFProperty(new Entity(r.getLocalName(), r.getURI(), getLink(r, ont, "rdfProperties")));
		}

		List<Resource> objPropSet = map.get("objProperties");
		for(Resource r : objPropSet){
			if(r.getLocalName().isEmpty()) continue;
			ont.addObjProperty(new Entity(r.getLocalName(), r.getURI(), getLink(r, ont, "objProperties")));
		}

		List<Resource> dataPropSet = map.get("dataProperties");
		for(Resource r : dataPropSet){
			if(r.getLocalName().isEmpty()) continue;
			ont.addDataProperty(new Entity(r.getLocalName(), r.getURI(), getLink(r, ont, "dataProperties")));
		}

		List<Resource> annotPropSet = map.get("annotProperties");
		for(Resource r : annotPropSet){
			if(r.getLocalName().isEmpty()) continue;
			ont.addAnnotationProperty(new Entity(r.getLocalName(), r.getURI(), getLink(r, ont, "annotProperties")));
		}

		List<Resource> indvSet = map.get("individuals");
		for(Resource r : indvSet){
			if(r.getLocalName().isEmpty()) continue;
			ont.addIndividual(new Entity(r.getLocalName(), r.getURI(), getLink(r, ont, "individuals")));
		}

		ontologyList.put(ont.getPrefix(), ont);
	}

	private String getLink(Resource r, OWLRDFOntology ont, String type) {
	
		String link= null;
		
		switch(r.getNameSpace()){
		case BF:
			String typeChar = null;
			if(type.equals("owlClasses") || type.equals("rdfsClasses")){
				typeChar = "c";
			}else if(type.equals("rdfProperties") || type.equals("objProperties") || type.equals("dataProperties")){
				typeChar = "p";
			}
			link = ont.getOntologyDocBaseURL()+typeChar+"_"+r.getLocalName();
			break;
		case CIDOC_CRM: 
			link = ont.getOntologyDocExactURL();
			break;
		case DC_ELEMENT:
			link = ont.getOntologyDocBaseURL()+r.getLocalName();
			break;
		case DC_TERM:
			link = ont.getOntologyDocBaseURL()+r.getLocalName();
			break;
		case FOAF:
			link = ont.getOntologyDocBaseURL()+r.getLocalName();
			break;
		case GEO:
			link = ont.getOntologyDocBaseURL()+r.getLocalName();
			break;
		case BIBLIOTEK: 
			link = ont.getOntologyDocBaseURL()+r.getLocalName();
			break;
		case OA:
			link = ont.getOntologyDocBaseURL()+r.getLocalName().toLowerCase();
			break;
		case PROV:
			link = ont.getOntologyDocBaseURL()+r.getLocalName();
			break;
		case RDA:
			link = ont.getOntologyDocBaseURL()+r.getLocalName();
			break;
		case SCHEMA:
			link = ont.getOntologyDocBaseURL()+r.getLocalName();
			break;
		case SKOS:
			link = ont.getOntologyDocBaseURL()+r.getLocalName();
			break;
		case VIVOCORE:
			link = ont.getOntologyDocExactURL();
			break;
		}
		
		return link;
	}

}
