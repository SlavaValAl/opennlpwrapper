package rdfserializer;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import opennlpfinder.Entity;

public class JenaRdfSerialiser {
        private static Model rdfmodel;

        public static Model ConvertEntitiesToRdf(Iterable<Entity> entities,
            String baseUri) {

        JenaRdfSerialiser.rdfmodel = ModelFactory.createDefaultModel();

        for (Entity entity : entities) {
            rdfmodel.createResource(baseUri + entity.Name())
                 .addProperty(VCARD.FN, entity.Name())
                 .addProperty(VCARD.CATEGORIES, entity.Type())
                 .addProperty(VCARD.Other, String.valueOf(entity.Probability()));
        }

        // now write the model in XML form to a file
        return rdfmodel;
    }
}
