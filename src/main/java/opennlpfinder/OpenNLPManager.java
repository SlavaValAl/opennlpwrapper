package opennlpfinder;

import com.hp.hpl.jena.rdf.model.Model;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import rdfserializer.JenaRdfSerialiser;

public class OpenNLPManager {
    public static void main(String[] args) throws IOException {
        // create new OpenNLPFinder model using VM cli options
        OpenNLPFinder myfinder = new OpenNLPFinder();
        // perform search procedure
        myfinder.singleModelSearch();
        // parse found results to entities
        myfinder.ParseResults();
        String baseURI = "http://sstu.ru";
        // pass results to serializer & get rdf model as result
        Model rdfmodel = JenaRdfSerialiser.ConvertEntitiesToRdf(myfinder.GetResults(),
            baseURI);
        // write results in rdf format
        rdfmodel.write(
            new FileWriter(
                new File(System.getProperty("result.file"))
            )
        );
    }   
}