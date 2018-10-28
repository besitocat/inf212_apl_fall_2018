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
import java.util.stream.Collectors;
import java.util.Map;
import java.util.function.Function;

//Evita Bakopoulou, UCInet: ebakopou

class Twelve{
    public static HashMap<String, Object> data_storage_obj = new HashMap<String, Object>();
    public static HashMap<String, Object> stop_words_obj =  new HashMap<String, Object>();
    public static HashMap<String, Object> word_freq_obj = new HashMap<String, Object>();
    
    public static void extract_words(HashMap<String, Object> obj, String filePath) throws IOException{
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
        	obj.put("data", words);
        }
        catch (IOException e){
        	e.printStackTrace();
        }
    }
    
    
    public static void load_stop_words(HashMap<String, Object> obj) throws IOException{
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
        obj.put("stop_words", stopword_list);
    }

    
    public static void increment_count(HashMap<String, Object> freq_obj) {
		HashMap<String, Integer> freq = ((HashMap<String, Integer>) word_freq_obj.get("freqs"));
		List<String> words = (ArrayList<String>) data_storage_obj.get("words");
		boolean is_stopword;
		for (String word : words) {
		    if(word.length()>1){
    		        is_stopword = ((Function<String, Boolean>) stop_words_obj.get("is_stop_word")).apply(word);
        		    if(!is_stopword){
            		      if (freq.containsKey(word)){
            				freq.put(word, freq.get(word) + 1);}
            			  else{
            				freq.put(word, 1);
            		      }
    			     }
		     }
	     }
		freq_obj.put("freqs", freq);
	}
    
    public static void main(String[] args) throws IOException{
        List<String> data = new ArrayList<>();
        data_storage_obj.put("data", data);
        data_storage_obj.put("init", (Runnable) (() -> {
            try{
                extract_words(data_storage_obj, args[0]);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }));
        ((Runnable) data_storage_obj.get("init")).run();
        
        List<String> words = (List<String>)data_storage_obj.get("data");
        data_storage_obj.put("words", words);
        
        HashMap<String, Object> stop_words_obj = new HashMap<>();
        stop_words_obj.put("stop_words", new ArrayList<String>());
        stop_words_obj.put("init", (Runnable) ( () -> {
            try{
                load_stop_words(stop_words_obj);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }));
        
        stop_words_obj.put("is_stop_word", (Function<String, Boolean>) w -> ((List<String>)stop_words_obj.get("stop_words")).contains(w));
        words = (List<String>)data_storage_obj.get("words");
        ((Runnable) stop_words_obj.get("init")).run();
        
        HashMap<String, Integer> freqs = new HashMap<String, Integer>();
        word_freq_obj.put("freqs", freqs);
        word_freq_obj.put("increment_count", (Runnable) ( () -> {
			HashMap<String, Integer> freq = ((HashMap<String, Integer>) word_freq_obj.get("freqs"));
    		final List<String> words2 = (ArrayList<String>) data_storage_obj.get("words");
    		boolean is_stopword;
    		for (String word : words2) {
                is_stopword = ((Function<String, Boolean>) stop_words_obj.get("is_stop_word")).apply(word);
                if((is_stopword==false) && word.length()>1){
        		      if (freq.containsKey(word)){
                	       freq.put(word, freq.get(word) + 1);
                	  }
                	  else{
                			freq.put(word, 1);
        		      }
			     }
    	     }
    	     word_freq_obj.put("freqs", freq);
		}));
		((Runnable) word_freq_obj.get("increment_count")).run();
		
		word_freq_obj.put("sorted", (Runnable) ( () -> {
		    LinkedHashMap<String, Integer> ordered = ((HashMap<String, Integer>)word_freq_obj.get("freqs")).entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            word_freq_obj.put("freqs", ordered);
		}));
		((Runnable) word_freq_obj.get("sorted")).run();
		
		word_freq_obj.put("top25", (Runnable) ( () -> {
		    ((Map<String, Integer>)word_freq_obj.get("freqs")).entrySet().stream().limit(25).forEach(p -> System.out.println(p.getKey() + " - " + p.getValue()));
		
		}));
		((Runnable) word_freq_obj.get("top25")).run();
	}

}