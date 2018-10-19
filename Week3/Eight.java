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

//Evita Bakopoulou, UCInet: ebakopou

interface IFunction{
	public void call(Object arg, IFunction func); 
	}

class ReadFile implements IFunction{

	
	//reads the data file and loads the lines into a list:
	 public void call(Object arg, IFunction func){
		String filePath = (String)arg;
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
        func.call(data, new Normalize());
    }
}
	

class FilterChars implements IFunction{
	
	//filters non alphanumeric and converts to lowercase: 
	 public void call(Object arg, IFunction func){
		List<String> data = (List<String>)arg;
		List<String> filtered_data = new ArrayList<String>();
		for (String line: data){
			line = line.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
			line = line.toLowerCase();
        	filtered_data.add(line);
		}
		func.call(filtered_data, new ScanWords());
	}
}

class Normalize implements IFunction{
	
	//filters non alphanumeric and converts to lowercase:
	 public void call(Object arg, IFunction func){
		List<String> data = (List<String>)arg;
		List<String> filtered_data = new ArrayList<String>();
		for (String line: data){
			line = line.toLowerCase();
        	filtered_data.add(line);
		}
		func.call(filtered_data, new RemoveStopWords());
	}
}

class ScanWords implements IFunction{
	
	//Splits line into words and returns a list of words:
	public void call(Object arg, IFunction func){
		List<String> data = (List<String>)arg;
		List<String> words = new ArrayList<String>();
		for (String line: data){
			String [] split = line.split(" ");
        	for (String w:split){
        		words.add(w);
        	}
        }
        func.call(words, new GetFrequencies());
	}
}

class RemoveStopWords implements IFunction{
	
	//loads stopword file and removes them from our data list:
	public void call(Object arg, IFunction func){
		List<String> data = (List<String>)arg;
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
        List<String> new_data = new ArrayList<String>();
        for (String w:data){
        	if (w.length()>1 && !stopword_list.contains(w)){
        		new_data.add(w);
        	}
        }
        func.call(new_data, new Sort());
	}
}
	

class GetFrequencies implements IFunction{
	
	//gets the counts per word:
	public void call(Object arg, IFunction func){
		List<String> data = (List<String>)arg;
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
		func.call(termFreq, new PrintResults());		
	}
}
	
class Sort implements IFunction{
	
	//sorts the hashmap in descreasing order:
	public void call(Object arg, IFunction func){
		HashMap<String, Integer> termFreq = (HashMap<String, Integer>)arg;
		LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
		termFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));		
         func.call(sortedTermFreq, new NoOp());
	}
}

class PrintResults implements IFunction{
	
		public void call(Object arg, IFunction func){
		LinkedHashMap<String,Integer> sortedTermFreqCounts = (LinkedHashMap<String, Integer>)arg;
		int k = 25;
		int counter = 0;
		for (String key: sortedTermFreqCounts.keySet()){
			if(counter<k){
				System.out.println(key + " - " + Integer.toString(sortedTermFreqCounts.get(key)));
				counter +=1;
			}
	   }  
	   func.call(null, null);
   }
 }
   
class NoOp implements IFunction{
	
   public void call(Object arg, IFunction func){
   }
   
}	

public class Eight{	
	
	public static void main(String[] args){
		ReadFile r = new ReadFile();
		r.call(args[0], new FilterChars());
	}
}