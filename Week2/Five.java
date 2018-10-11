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

//Evita Bakopoulou, UCInet: ebakopou

public class Five {
	
	//reads the data file and loads the lines into a list:
	private static List<String> readFile(String filePath){
		List<String> data = new ArrayList<String>();
		String line;
        try{
        	BufferedReader r = new BufferedReader(new FileReader(filePath));
        	while((line=r.readLine())!=null){
        		data.add(line);
        	}
        }
        catch (IOException e){
        	e.printStackTrace();
        }
        return data;
	}
	
	//filters non alphanumeric and converts to lowercase:
	private static List<String> filterCharsAndNormalize(List<String> data){
		List<String> filtered_data = new ArrayList<String>();
		for (String line: data){
			line = line.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
			line = line.toLowerCase();
        	filtered_data.add(line);
		}
		return filtered_data;
	}
	
	//Splits line into words and returns a list of words:
	private static List<String> scanWords(List<String> data){
		List<String> words = new ArrayList<String>();
		for (String line: data){
			String [] split = line.split(" ");
        	for (String w:split){
        		words.add(w);
        	}
        }
        return words;
	}
	
	//function with currying, it gets the data list and it applies to it the second argument,
	//which is the path of the stopwords
	private static Function<String, List<String>> removeStopwords(List<String> data) {
	    return stopwords_path -> {
	        String line;
            List<String> stopword_list = new ArrayList<String>();
            try{
            	BufferedReader r = new BufferedReader(new FileReader(stopwords_path));
            	while((line=r.readLine())!=null){
            		String [] split_line = line.split(",");
            		for (String w: split_line){
            			stopword_list.add(w);
            		}
            	}
            }
            catch (IOException e){
            	e.printStackTrace();
            }
            List<String> new_data = new ArrayList<String>();
            for (String w:data){
            	if (w.length()>1 && !stopword_list.contains(w)){
            		new_data.add(w);
            	}
            }
	        return new_data;
	    };
	}

	//gets the counts per word:
	private static Map<String,Integer> getFrequencies(List<String> data){
		Map<String, Integer> termFreq = new HashMap<>();
		for (String w: data){
			if (termFreq.containsKey(w)){
				int curFreq = termFreq.get(w)  + 1;
				termFreq.put(w,curFreq);
			}
			else{
				termFreq.put(w,1);
			}
        }
		return termFreq;		
	}
	
	//sorts the hashmap in descreasing order:
	private static LinkedHashMap<String,Integer> sort(Map<String,Integer> termFreq){
		LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
		termFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));		
         return sortedTermFreq;
	}
	
	private static void printResults(LinkedHashMap<String,Integer> sortedTermFreqCounts){
		int k = 25;
		int counter = 0;
		for (String key: sortedTermFreqCounts.keySet()){
			if(counter<k){
				System.out.println(key + " - " + Integer.toString(sortedTermFreqCounts.get(key)));
				counter +=1;
			}
	   }  
   }
	
	public static void main(String[] args){
		printResults(sort(getFrequencies(removeStopwords((scanWords(filterCharsAndNormalize(readFile(args[0]))))).apply(args[1]))));
		
	}
}