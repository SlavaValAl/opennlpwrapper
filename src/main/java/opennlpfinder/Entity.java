package opennlpfinder;

import java.util.Objects;

public final class Entity {
    private String name;
    private String type;
    private double probability;
    
    public String Name()
    {
        return this.name;
    }
    
    public void Name(String name)
    {
        this.name = name.trim();
    }
    
    public String Type()
    {
        return this.type;
    }
    
    public void Type(String type)
    {
        this.type = type;
    }
    
    public double Probability()
    {
        return this.probability;
    }
    
    public void Probability(double probability)
    {
        this.probability = probability;
    }
    
    // TODO: provide uri fomat for entity
    //delete all dots + replace whitespace with '_'
    public String URIName()
    {
        return this.Name();
    }

    public Entity(String name, String type, double probability){
        this.Name(name);
        this.Type(type);
        this.Probability(probability);
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Entity)) {
           return false;
        }

    Entity that = (Entity) other;

    return this.Name().equals(that.Name()) && 
        this.Type().equals(that.Type());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.Name());
        hash = 53 * hash + Objects.hashCode(this.Type());
        return hash;
    }
}
