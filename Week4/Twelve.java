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

class Twelve{
    
    public void extract_words(HashMap<String, List<String>> obj, String path_to_file) throws IOException{
        List<String> words = new ArrayList<String>();
		String line;
        try{
        	BufferedReader r = new BufferedReader(new FileReader(filePath));
        	while((line=r.readLine())!=null){
        		line = line.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
			    line = line.toLowerCase();
			    String [] split = line.split(" ");
        	    for (String w:split){
        		  words.add(w);
        	      }
        	}
        	obj.put('data', words);
        }
        catch (IOException e){
        	e.printStackTrace();
        }
    }
    
    
    public void load_stop_words(HashMap<String, List<String>> obj){
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
        obj.put('stop_words', stopword_list);
    }
    
    
    public void increment_count(HashMap<String, List<String>> obj, String w){
        if (obj.get('freqs').containsKey(w)){
			int curFreq = obj.get('freqs').get(w)  + 1;
			obj.get('freqs').put(w,curFreq);
		}
		else{
			obj.get('freqs').put(w,1);
		}
    }
    
    
    public static void main(String[] args){
        HashMap<String, List<String>> data_storage_obj = new HashMap<>();
        data_storage_obj.put('data') = new List<String>();
        data_storage_obj.put('init') = "";
        data_storage_obj.put('words') = data_storage.get('data');

        
    }

}