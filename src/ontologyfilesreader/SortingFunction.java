package ontologyfilesreader;

import java.util.Comparator;

public class SortingFunction implements Comparator<OWLRDFOntology> {

	@Override
	public int compare(OWLRDFOntology o1, OWLRDFOntology o2) {
		return o1.getOntologyName().compareTo(o2.getOntologyName());
	}

}
