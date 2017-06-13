package edu.cornell.ld4l.ontologymodulebuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LODEPageSourceReader {

	public static void main(String[] args) {
		File file = new File("resources/input/lode.xml");
		Map<String, String> map = null;
		try {
			map = readLODEClasses(file);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		System.out.println(map.size());
	}


	public static Map<String, String> readLODEClasses(File xmlFile) throws ParserConfigurationException, SAXException, IOException{
		List<String> entityList = new ArrayList<String>();
		entityList.add("classes");
		entityList.add("objectproperties");
		entityList.add("dataproperties");
		entityList.add("annotationproperties");
		entityList.add("namedindividuals");

		Map<String, String> map = new HashMap<String, String>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		NodeList entryList = doc.getElementsByTagName("div");	
		for(int index=0; index< entryList.getLength(); index++){
			Node node = entryList.item(index);
			Element eElement = (Element) node;
			String id  = eElement.getAttribute("id");
			if(!entityList.contains(id)) continue;
			NodeList liList = eElement.getElementsByTagName("a");
			for(int inx =0; inx < liList.getLength(); inx++){
				Node liNode = liList.item(inx);
				Element liElement = (Element) liNode;
				String href  = liElement.getAttribute("href");
				href = "http://www.essepuntato.it/lode/owlapi/"
						+ "http://vivoweb.org/ontology/core"+href;
				String iri = liElement.getAttribute("title");
				map.put(iri, href);
			}
		}
			return map;
		}
	}
