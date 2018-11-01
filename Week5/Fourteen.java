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


public class Fourteen{
    
    //private static WordFrequencyCounter wfCounter;
    
    interface Handler{
        public void load(String arg) throws IOException;
        public void run() throws IOException;
    }
    
    static class WordFrequencyFramework{
        private List<Handler> load_event_handlers = new ArrayList<Handler>();
        private List<Handler> dowork_event_handlers = new ArrayList<Handler>();
        private List<Handler> end_event_handlers = new ArrayList<Handler>();
        
        public void register_for_load_event(Handler handler){
            this.load_event_handlers.add(handler);
        }
        
        public void register_for_dowork_event(Handler handler){
            this.dowork_event_handlers.add(handler);
        }
        
        public void register_for_end_event(Handler handler){
            this.end_event_handlers.add(handler);
        }
        
        public void run(String path_to_file) throws IOException{
            
            for(Handler e: this.load_event_handlers){
                e.load(path_to_file);
            }
            for(Handler e: this.dowork_event_handlers){
                e.run();
            }
            for(Handler e: this.end_event_handlers){
                e.run();
            }
        }
        
        public WordFrequencyFramework(){
            return;
        }
    }
    
    static class DataStorage implements Handler{
        private StopWordFilter stopword_filter;
        private List<String> data = new ArrayList<String>();
        private List<Handler> word_event_handlers = new ArrayList<Handler>();
        
        
        public DataStorage(WordFrequencyFramework wfapp, StopWordFilter stop_word_filter){
            this.stopword_filter = stop_word_filter;
            wfapp.register_for_load_event(this);
            wfapp.register_for_dowork_event(this);
        }
        
        @Override
        public void load(String path) throws IOException{
            try{
            	BufferedReader r = new BufferedReader(new FileReader(path));
            	String line;
            	while((line=r.readLine())!=null){
            	    line = line.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
    			    line = line.toLowerCase();
            		this.data.add(line);
            	}
            }
            catch (IOException e){
            	e.printStackTrace();
            }            
        }
        
        @Override
        public void run() throws IOException{
            List<String> data = this.data;
            for (String line: data){
                String [] split = line.split(" ");
            	for (String w: split){
            		if (!this.stopword_filter.is_stop_word(w)){
            		    for(Handler h: this.word_event_handlers){
            		        h.load(w);
            		    }
            		}
            	}
            }
        }
        
        public void register_for_word_event(Handler h){
            this.word_event_handlers.add(h);
        }
    }
    
    static class StopWordFilter implements Handler{
        private List<String> stopwords = new ArrayList<String>();
        
        public StopWordFilter(WordFrequencyFramework wfapp){
            wfapp.register_for_load_event(this);
            
        }
        
        @Override
        public void load(String ignore) throws IOException{
            String stopwords_path = "../stop_words.txt";
		    String line;
            try{
            	BufferedReader r = new BufferedReader(new FileReader(stopwords_path));
            	while((line=r.readLine())!=null){
            		String [] split_line = line.split(",");
            		for (String w: split_line){
            		     this.stopwords.add(w);
            		}
        	    }
            }
            catch (IOException e){
            	e.printStackTrace();
            }
        }
        
        @Override
        public void run() throws IOException{
            return;
        }
    
        public boolean is_stop_word(String w) {
            if (w.length()>1 && !this.stopwords.contains(w)){
            	return false;
            }
            return true;
        }   
    
    }

        
    static class WordFrequencyCounter implements Handler{
        private Map<String, Integer> termFreq = new HashMap<String, Integer>();
        
        public WordFrequencyCounter(WordFrequencyFramework wfapp, DataStorage data_storage){
            data_storage.register_for_word_event(this);
            wfapp.register_for_end_event(this);
        }
        
        @Override
        public void load(String word) throws IOException{
            if (this.termFreq.containsKey(word)){
    			int curFreq = this.termFreq.get(word)  + 1;
    			this.termFreq.put(word,curFreq);
    		}
    		else{
    			this.termFreq.put(word,1);
    		}
        }
        
        @Override
        public void run() throws IOException{
    		LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
    		this.termFreq.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));	
                    
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
    
    //Counts the words that contain z (total unique words that have the letter 'z')
    static class WordsWithZCounter implements Handler{
        private WordFrequencyCounter wf_counter;
        private Map<String, Integer> termFreq = new HashMap<String, Integer>();
        
        
        public WordsWithZCounter(WordFrequencyFramework wfapp, WordFrequencyCounter wf_counter){
            this.wf_counter = wf_counter;
            wfapp.register_for_load_event(this);
            wfapp.register_for_end_event(this);
        }
        
        @Override
        public void load(String ignore) throws IOException{
            this.termFreq = this.wf_counter.termFreq;
        }
        
        @Override
        public void run() throws IOException{
    		int counter = 0;
    		for (String key: this.termFreq.keySet()){
    			if(key.indexOf("z")!=-1){
    				counter +=1;
    			}
	       }
	       System.out.println("\nNumber of words with z: " + Integer.toString(counter));
        }
    }
    	
	public static void main(String[] args) throws IOException{
	    WordFrequencyFramework wfapp= new WordFrequencyFramework();
	    StopWordFilter stop_word_filter = new StopWordFilter(wfapp);
	    DataStorage data_storage = new DataStorage(wfapp, stop_word_filter);
	    WordFrequencyCounter word_freq_counter = new WordFrequencyCounter(wfapp, data_storage);
	    WordsWithZCounter z_words_counter = new WordsWithZCounter(wfapp, word_freq_counter);
	    wfapp.run(args[0]);
	}
}