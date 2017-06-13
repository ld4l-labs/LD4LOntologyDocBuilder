package edu.cornell.ld4l.entrypoint;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.xml.sax.SAXException;

import edu.cornell.ld4l.configuration.Configuration;
import edu.cornell.ld4l.ontologydomainmodelbuilder.OntologyDomainModulesBuilder;
import edu.cornell.ld4l.ontologymodulebuilder.OwlOntologyModelsBuilder;

public class LD4LOntologyModularizerEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(LD4LOntologyModularizerEntryPoint.class.getName());


	public static void main(String[] args) {
		try {
			if(args.length > 0){
				init(args[0]);
			}else{
				String propFilePath = "resources/setup.properties";
				init(propFilePath);
			}
			LD4LOntologyModularizerEntryPoint mep = new LD4LOntologyModularizerEntryPoint();
			mep.runProcess();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runProcess() {
		//Run the MODULE Generation Process
		LOGGER.info("\n\n---------- STARTING MODULES GENERATION PROCESS----------");
		try{
			runModuleProcess();
		}catch(Exception exp){
			LOGGER.log(Level.WARNING, "\n\n---------ERROR OCCURED:----------", exp);
		}

		//Run the MODLES Generation Process
		LOGGER.info("\n\n---------- STARTING DOMAIN-SPECIFIC MODELS GENERATION PROCESS----------");
		try{
			runModelsProcess();
		}catch(Exception exp){
			LOGGER.log(Level.WARNING, "\n\n---------ERROR OCCURED:----------", exp);
		}
	}

	private void runModelsProcess() {
		OwlOntologyModelsBuilder obj = new OwlOntologyModelsBuilder();
		try {
			obj.runProcess();
		} catch (OWLOntologyCreationException | ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	private void runModuleProcess() {
		OntologyDomainModulesBuilder obj = new OntologyDomainModulesBuilder();
		try {
			obj.runProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void init(String propFilePath) throws IOException{
		String date = getCurrentDate();
		Configuration.setDate(date);
		generateDirectorites(date, propFilePath);
	}



	private static void generateDirectorites(String date, String propFilePath) throws IOException {
		SetupPropertyValues properties = new SetupPropertyValues();
		Map<String, String> map = properties.getPropValues(propFilePath);
		Configuration.setINPUT_FOLDER(map.get("INPUT_FILE_FOLDER"));
		Configuration.setOUTPUT_FOLDER(map.get("OUTPUT_FILE_FOLDER"));
		Configuration.setDate(date);

		createFolder(new File(Configuration.OUTPUT_FOLDER+"/"+date));
	}

	private static void createFolder(File file) {
		if (!file.exists()) {
			if (file.mkdirs()) {
				LOGGER.info(file.getAbsolutePath()+" folder created!");
			} else {
				LOGGER.throwing("VivoOntologyModularizerEntryPoint", "createFolder", new Throwable("EXCEPTION: Could not create folder..."));
			}
		}
	}

	private static String getCurrentDate() {
		String date = null;
		Date now = new Date();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("E, y-M-d 'at' h:m:s a z");
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		date = dateFormatter.format(now);
		return date;
	}

}
