package opennlpfinder;

import java.util.Objects;

public final class Entity {
    private String name;
    private String type;
    public String uriName;
    private double probability;
    
    public String Name()
    {
        return this.name;
    }
    
    private void Name(String name)
    {
        this.name = name.trim();
    }
    
    public String Type()
    {
        return this.type;
    }
    
    private void Type(String type)
    {
        this.type = type;
    }
    
    public double Probability()
    {
        return this.probability;
    }
    
    private void Probability(double probability)
    {
        this.probability = probability;
    }
    
    private void URIName(String name) {
        this.uriName = name.replaceAll("[^а-яА-Я]", "");
    }

    public String URIName()
    {
        return this.uriName;
    }

    public Entity(String name, String type, double probability){
        this.Name(name);
        this.Type(type);
        this.Probability(probability);
        this.URIName(this.Name());
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
