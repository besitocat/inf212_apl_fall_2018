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
import java.lang.reflect.Method; 
import java.lang.reflect.Field; 
import java.lang.reflect.Constructor; 
  
//Evita Bakopoulou, UCInet: ebakopou

public class Seventeen {
    
    
    public Seventeen(){
        return;
    }
	
	//reads the data file and loads the lines into a list:
	public static ArrayList<String> readFile(ArrayList<String> data, String filePath){
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
	public static ArrayList<String> filterCharsAndNormalize(ArrayList<String> data){
		ArrayList<String> filtered_data = new ArrayList<String>();
		for (String line: data){
			line = line.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
			line = line.toLowerCase();
        	filtered_data.add(line);
		}
		return filtered_data;
	}
	
	//Splits line into words and returns a list of words:
	public static ArrayList<String> scanWords(ArrayList<String> data){
		ArrayList<String> words = new ArrayList<String>();
		for (String line: data){
			String [] split = line.split(" ");
        	for (String w:split){
        		words.add(w);
        	}
        }
        return words;
	}
	
	//loads stopword file and removes them from our data list:
	public static ArrayList<String> removeStopWords(ArrayList<String> data){
		String stopwords_path = "../stop_words.txt";
		String line;
        ArrayList<String> stopword_list = new ArrayList<String>();
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
        ArrayList<String> new_data = new ArrayList<String>();
        for (String w:data){
        	if (w.length()>1 && !stopword_list.contains(w)){
        		new_data.add(w);
        	}
        }
        return new_data;
	}
	
	//gets the counts per word:
	public static Map<String,Integer> getFrequencies(ArrayList<String> data){
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
	public static LinkedHashMap<String,Integer> sort(Map<String,Integer> termFreq){
		LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
		termFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));		
         return sortedTermFreq;
	}	
	
	public static void main(String[] args) throws Exception{
		ArrayList<String> data = new ArrayList<String>();
		Map<String, Integer> termFreq = new HashMap<>();
		LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();

        Seventeen obj = new Seventeen();
        Class cls = obj.getClass(); 
        Constructor constructor = cls.getConstructor();
        Method methodcall1;

        methodcall1 = cls.getDeclaredMethod("readFile", 
                                                 ArrayList.class, String.class); 
    g
        data = (ArrayList)methodcall1.invoke(obj, data, args[0]); 
        
        methodcall1 = cls.getDeclaredMethod("filterCharsAndNormalize", 
                                                 ArrayList.class); 
    
        data = (ArrayList)methodcall1.invoke(obj, data); 
        methodcall1 = cls.getDeclaredMethod("scanWords", 
                                                 ArrayList.class); 
        data = (ArrayList)methodcall1.invoke(obj, data); 
        methodcall1 = cls.getDeclaredMethod("removeStopWords", 
                                                 ArrayList.class); 
        data = (ArrayList)methodcall1.invoke(obj, data); 
    	
    	methodcall1 = cls.getDeclaredMethod("getFrequencies", 
                                                 ArrayList.class); 
        termFreq = (Map)methodcall1.invoke(obj, data); 
        methodcall1 = cls.getDeclaredMethod("sort", Map.class); 
        sortedTermFreq = (LinkedHashMap)methodcall1.invoke(obj, termFreq); 
	
		int k = 25;
		int counter = 0;
   	    for (String key: sortedTermFreq.keySet()){
   	   	   if(counter<k){
   	   	   	   System.out.println(key + " - " + Integer.toString(sortedTermFreq.get(key)));
   	   	   	   counter +=1;
   	   	   }
   	   }  	
	}
}