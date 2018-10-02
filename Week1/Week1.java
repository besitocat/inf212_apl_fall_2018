import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections; 
import java.util.Comparator; 
import java.util.HashMap; 
import java.util.LinkedHashMap; 
import java.util.Map;

public class Week1 { 
	
	public static void main(String[] args) { 
        int k =25;
        List<String> stopword_list;
        stopword_list = loadStopwords();
        LinkedHashMap<String, Integer> sortedTermFreqCounts;
        String testInput = "../pride-and-prejudice.txt";
        sortedTermFreqCounts = getTermFrequency(testInput, stopword_list);
        printResults(sortedTermFreqCounts,k);
   }
   
   private static List<String> loadStopwords(){
   	    String stopwords_path = "../stop_words.txt";
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
        return stopword_list;
   }
   
   private static LinkedHashMap<String,Integer> getTermFrequency(String textFile, List<String> stopwords){
   	   Map<String, Integer> termFreq = new HashMap<>();
   	   LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
   	    try{
        	BufferedReader r = new BufferedReader(new FileReader(textFile));
        	String line;
        	String regex = "[\\W]|_";
        	while((line=r.readLine())!=null){
        		line = line.replaceAll(regex, " "); //replace non-alphanumeric characters
        		line = line.toLowerCase();
        		String [] split_line = line.split(" ");
        		for (String w: split_line){
        			if (!stopwords.contains(w) && w.length()>1){
						if (termFreq.containsKey(w)){
							int curFreq = termFreq.get(w)  + 1;
							termFreq.put(w,curFreq);
						}
						else{
							termFreq.put(w,1);
						}
						
        			}
        		}	
        	}
        	termFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));		 
        }
        catch (IOException e){
        	e.printStackTrace();
        }
     return sortedTermFreq;
   }
   
   
   
   private static void printResults(LinkedHashMap<String,Integer> sortedTermFreqCounts, int k){
   	   int counter = 0;
   	   for (String key: sortedTermFreqCounts.keySet()){
   	   	   if(counter<k){
   	   	   	   System.out.println(key + " - " + Integer.toString(sortedTermFreqCounts.get(key)));
   	   	   	   counter +=1;
   	   	   }
   	   }
   	   
   }
}