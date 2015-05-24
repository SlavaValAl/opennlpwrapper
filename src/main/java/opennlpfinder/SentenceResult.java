package opennlpfinder;

import java.util.HashSet;
import opennlp.tools.util.Span;

public class SentenceResult {
    private final Span[] names;
    private final String[] tokens;
    private final double[] spanProbs;
    
    public Span[] getNames()
    {
        return this.names;
    }
    
    public String[] getTokens()
    {
        return this.tokens;
    }
    
    public double[] getProbs()
    {
        return this.spanProbs;
    }

    public SentenceResult(Span[] names, String[] tokens, double[] spanProbs){
        this.names = names;
        this.tokens = tokens;
        this.spanProbs = spanProbs;
    }
    
    public HashSet<Entity> toEntities(){
        HashSet<Entity> EntitiesList = new HashSet<>();
        int index = 0;
        for (Span name : this.getNames()) {
            StringBuilder fullname = new StringBuilder();
            // Collect found names from words array
            for (int ti = name.getStart(); ti < name.getEnd(); ti++) {
                // If we have complex NE(when entity contain multiple words),
                // entity should be combained to string using whitespace
                // separator
                fullname.append(this.getTokens()[ti]).append(" ");
            }
            EntitiesList.add(new Entity(fullname.toString(), name.getType(),
                    this.getProbs()[index]));
            // add recognized value to array
            index++;
        }
        return EntitiesList;
    }
}