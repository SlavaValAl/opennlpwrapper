package opennlpfinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.AggregatedFeatureGenerator;
import opennlp.tools.util.featuregen.PreviousMapFeatureGenerator;
import opennlp.tools.util.featuregen.TokenClassFeatureGenerator;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class OpenNLPFinder {

    private SentenceDetectorME sdetector;
    private NameFinderME finder;
    private Tokenizer tokenizer;
    private final ArrayList<SentenceResult> SentencesResults;
    private final HashSet<Entity> Entities;

    // <properties>
    public SentenceDetectorME getSdetector() {
        return sdetector;
    }

    public void setSdetector(SentenceDetectorME sdetector) {
        this.sdetector = sdetector;
    }

    public NameFinderME getFinder() {
        return finder;
    }

    public void setFinder(NameFinderME finder) {
        this.finder = finder;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }
    // </properties>
    
    // Create new OpenNLP finder object
    public OpenNLPFinder () throws IOException
    {
        // Array for sentence results
        this.SentencesResults = new ArrayList<>();

        // Array for results
        this.Entities = new HashSet<>();

        // Create new sentence detector based on prepared recognition model
        this.sdetector = new SentenceDetectorME(
          new SentenceModel(this.GetSentenceModel())
        );

        // Create new tokenizer based on prepared recognition model
        this.tokenizer = new TokenizerME(
          new TokenizerModel(this.GetTokenModel())   
        );

        // Create new name finder based on prepared recognition model
        this.finder = new NameFinderME(
          new TokenNameFinderModel(this.GetFinderModel())
        );

    }

    public OpenNLPFinder (File sentenceModel, File TokenModel,
            File finderModel) throws IOException
    {
        // Array for sentence results
        this.SentencesResults = new ArrayList<>();

        // Array for results
        this.Entities = new HashSet<>();

        // Create new sentence detector based on prepared recognition model
        this.sdetector = new SentenceDetectorME(
          new SentenceModel(sentenceModel)
        );

        this.tokenizer = new TokenizerME(
          new TokenizerModel(TokenModel)
        );

        // Create new name finder based on prepared recognition model
        this.finder = new NameFinderME(
          new TokenNameFinderModel(finderModel)
        );
    }

    // Use several recogniton models for one input file
    public void multiModel() throws IOException {

        File modelDir = GetModelDir();

        String[] sentences = {
          "Former first lady Nancy Reagan was taken to a " +
                  "suburban Los Angeles " +
          "hospital \"as a precaution\" Sunday after a fall at " +
                  "her home, an " +
          "aide said. ",
          "The 86-year-old Reagan will remain overnight for " +
          "observation at a hospital in Santa Monica, California, " +
                  "said Joanne " +
          "Drake, chief of staff for the Reagan Foundation."};
        NameFinderME[] finders = new NameFinderME[3];
        String[] names = {"person", "location", "date"};
        for (int mi = 0; mi < names.length; mi++) {
          finders[mi] = new NameFinderME(new TokenNameFinderModel(
              new FileInputStream(
                  new File(modelDir, "en-ner-" + names[mi] + ".bin")
              )));
        }

        Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
          for (String sentence : sentences) {
              List<Annotation> allAnnotations = new ArrayList<>();
              String[] tokens = tokenizer.tokenize(sentence);
              for (int fi = 0; fi < finders.length; fi++) {
                  Span[] spans = finders[fi].find(tokens);
                  double[] probs = finders[fi].probs(spans);
                  for (int ni = 0; ni < spans.length; ni++) {
                      allAnnotations.add(
                              new Annotation(names[fi], spans[ni], probs[ni])
                      );
                  }
              }   removeConflicts(allAnnotations);
          }
    }

    // Generate name finder model based on prepared text
    public void trainNameFinder() throws IOException {
        File baseDir = new File("src/test/resources");
        File destDir = new File("target");

        File inFile = new File(baseDir,"person.train");
        NameSampleDataStream nss = new NameSampleDataStream(
          new PlainTextByLineStream(
            new java.io.FileReader(inFile)));

        int iterations = 100;
        int cutoff = 5;
        TokenNameFinderModel model = NameFinderME.train(
            "en", // language
            "person", // type
            nss, 
            (AdaptiveFeatureGenerator) null,
            Collections.<String,Object>emptyMap(),
            iterations,
            cutoff);

        File outFile = new File(destDir, "person-custom.bin");
        FileOutputStream outFileStream = new FileOutputStream(outFile);
        model.serialize(outFileStream);
    }

    // Generate name finder model based on prepared text
    // with custom arguments
    public void trainNameFinderWithCustomFeatures() throws IOException {
        File baseDir = new File("src/test/resources");
        File destDir = new File("target");

        AggregatedFeatureGenerator featureGenerators = 
          new AggregatedFeatureGenerator(
            new WindowFeatureGenerator(
              new TokenFeatureGenerator(), 2, 2),
            new WindowFeatureGenerator(
              new TokenClassFeatureGenerator(), 2, 2),
            new PreviousMapFeatureGenerator()
          );  

        File inFile = new File(baseDir,"person.train");
        NameSampleDataStream nss = new NameSampleDataStream(
          new PlainTextByLineStream(
            new java.io.FileReader(inFile)));

        int iterations = 100;
        int cutoff = 5;

        TokenNameFinderModel model = NameFinderME.train(
            "en", // language
            "person", // type
            nss, 
            featureGenerators, 
            Collections.<String,Object>emptyMap(),
            iterations, 
            cutoff);

        File outFile = new File(destDir,"person-custom2.bin");
        FileOutputStream outFileStream = new FileOutputStream(outFile);
        model.serialize(outFileStream); //<co id="co.opennlp.name.persist2"/>

        NameFinderME finder = new NameFinderME(
            new TokenNameFinderModel(
                new FileInputStream(
                    new File(destDir, "person-custom2.bin")
                    )), featureGenerators, NameFinderME.DEFAULT_BEAM_SIZE);
    }
        // Use single recogniton models for one input file
    public void singleModelSearch() throws IOException {
        // Split text by sentences
        String sentences[] = this.getSdetector().sentDetect(GetInputText());

        // Parse sentences one by one
        for (String sentence : sentences) {
            // Split sentences into words use created tokenizer
            String[] tokens = this.getTokenizer().tokenize(sentence);
            // Pass words(tokens) array to NameFinder
            // and get Array of the Span objects as result
            // Span class has three fields: start, end, type.
            //   Start - index of the first element from tokens array
            //   End - index of the last element from tokens array
            //   Type - type of the found element
            Span[] names = this.getFinder().find(tokens);        
            if (names.length > 0) {
                // Get recognized items probabilities
                double [] spanProbs = this.getFinder().probs(names);
                // Write data to the the object
                this.SentencesResults.add(new SentenceResult(names, tokens, spanProbs));
            }
        }
    }

    // Write results to file
    public void writeResultToFile () throws IOException {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        StringBuilder sb = this.getStringResults();

        try {
          // Get result file from CLI option
          fileWriter = new FileWriter(
            new File(System.getProperty("result.file"))
          );
          bufferedWriter = new BufferedWriter(fileWriter);
          bufferedWriter.write(sb.toString());
        } catch (IOException e) { 
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null && fileWriter != null) {
              try {
                bufferedWriter.close();
                fileWriter.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
            } 
        }
    }

    // Save result as Stringbuilder object
    public StringBuilder getStringResults() {
        if (this.Entities.isEmpty()) {
            this.ParseResults();
        }
        // Array for string results
        StringBuilder sb = new StringBuilder();
        // Handle all entities one by one
        for (Entity entity : this.Entities) {
            // Collect found names from result array
            sb.append("Recognized value: ").append(entity.Name());
            sb.append("\n\ttype: ").append(entity.Type());
            sb.append("\n\tprobability: ").append(entity.Probability());
            sb.append("\n###\n");
        }
        return sb;
    }

    // Parse results to array of entities
    public void ParseResults() {
        // Clear entities array
        this.Entities.removeAll(Entities);
        for (SentenceResult sentenceResult  : this.SentencesResults) {
          // transform each sentence's results to result array
          this.Entities.addAll(sentenceResult.toEntities());
        }
    }

    // Parse results to array of entities
    public Iterable<Entity> GetResults() {
        // Clear entities array
        return this.Entities;
    }

    // Display recognized entities
    private void DisplayEntities() {
        for (Entity entity : this.Entities) {
            System.out.println("Recognized value: " + entity.Name());
            System.out.println("\n\ttype: " + entity.Type());
            System.out.println("\n\tprobability: " + entity.Probability());
            System.out.println("\n###\n");
        }
    }

    // Return folder with model files based on CLI option
    private File GetModelDir() throws IOException {
        return new File(System.getProperty("model.dir"));
    }

    // Return token model file based on CLI option
    private File GetTokenModel()throws IOException {
        return new File(GetModelDir(), System.getProperty("token.file"));
    }

    // Return sentence detector model file based on CLI option
    private File GetSentenceModel()throws IOException {
        return new File(GetModelDir(), System.getProperty("sent.file"));
    }

    // Return person model file based on CLI option
    private File GetFinderModel()throws IOException {
        return new File(GetModelDir(), System.getProperty("person.file"));
    }

    // Return specific model file based on CLI option
    private String GetInputText() throws IOException {
        String inputFilePath = System.getProperty("input.file");
        String inputFileEncoding = System.getProperty("file.encoding");
        byte[] encoded = Files.readAllBytes(Paths.get(inputFilePath));

        return new String(encoded, inputFileEncoding);
    }

    // Remove conflict results for multiModel regognition
    private void removeConflicts(List<Annotation> allAnnotations) {
        java.util.Collections.sort(allAnnotations);
        List<Annotation> stack = new ArrayList<>();
        stack.add(allAnnotations.get(0));
        for (int ai = 1; ai < allAnnotations.size(); ai++) {
          Annotation curr = (Annotation) allAnnotations.get(ai);
          boolean deleteCurr = false;
          for (int ki = stack.size() - 1; ki >= 0; ki--) {
            Annotation prev = (Annotation) stack.get(ki);
            if (prev.getSpan().equals(curr.getSpan())) {
              if (prev.getProb() > curr.getProb()) {
                deleteCurr = true;
                break;
              } else {
                allAnnotations.remove(stack.remove(ki));
                ai--;
              }
            } else if (prev.getSpan().intersects(curr.getSpan())) {
              if (prev.getProb() > curr.getProb()) {
                deleteCurr = true;
                break;
              } else {
                allAnnotations.remove(stack.remove(ki));
                ai--;
              }
            } else if (prev.getSpan().contains(curr.getSpan())) {
              break;
            } else {
              stack.remove(ki);
            }
          }
          if (deleteCurr) {
            allAnnotations.remove(ai);
            ai--;
            deleteCurr = false;
          } else {
            stack.add(curr);
          }
        }
    }
}