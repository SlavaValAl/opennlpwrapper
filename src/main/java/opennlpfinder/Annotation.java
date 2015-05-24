package opennlpfinder;

import opennlp.tools.util.Span;

public class Annotation implements Comparable<Annotation> {
  private final Span span;
  private final String type;
  private final double prob;

  public Annotation(String type, Span span, double prob) {
    this.span = span;
    this.type = type;
    this.prob = prob;
  }

  public Span getSpan() {
    return span;
  }

  public String getType() {
    return type;
  }

  public double getProb() {
    return prob;
  }

  public int compareTo(Annotation a) {
    int c = span.compareTo(a.span);
    if (c == 0) {
      c = Double.compare(prob, a.prob);
      if (c == 0) {
        c = type.compareTo(a.type);
      }
    }
    return c;
  }

  public String toString() {
    return type + " " + span + " " + prob;
  }
}