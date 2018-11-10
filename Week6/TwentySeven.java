import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections; 
import java.util.Comparator; 
import java.util.HashMap; 
import java.util.LinkedHashMap; 
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.Paths;
import java.nio.file.Files;

//Evita Bakopoulou, UCInet: ebakopou

public class TwentySeven{
    
    public static void extract_words(String filename) throws IOException{
        List<String> stopwords = new ArrayList<>();
        LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
        //load stopwords into a list
		try (BufferedReader br = Files.newBufferedReader(Paths.get("../stop_words.txt"))) {
			for (String line: br.lines().collect(Collectors.toList())){
			    String[] split = line.split(",");
			    for(String w: split){
			        stopwords.add(w);
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
       try (Stream<String> stream = Files.lines(Paths.get(filename))) {
       
            final List<String> stopwords2 = stopwords; 
            //for each line read, get normalized words, filter stopwords, create a map and sort it in descreasing order
            stream.map(w -> w.replaceAll("[\\W]|_", " "))
            .map(String::toLowerCase)
            .flatMap(line -> Stream.of(line.split(" ")))
            .filter(w-> w.length()>1)
            .filter(w -> !stopwords2.contains(w))
            .collect(Collectors.toMap(word -> word, word -> 1, Integer::sum))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue())); //store entries into a LinkedHashMap
            
            print25(sortedTermFreq);
    }
    catch (IOException e){
		e.printStackTrace();
	}
     
    }

            
    public static void print25(LinkedHashMap<String, Integer> sortedTermFreq){
        int counter = 0;
        int k = 25;
        for(String key: sortedTermFreq.keySet()){
            if(counter<k){
                System.out.println(key + " - " + Integer.toString(sortedTermFreq.get(key)));
				counter +=1;
    		}
		}
	}
    
    public static void main(String[] args) throws IOException{
        extract_words(args[0]);
    }
}