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


class DataStorageManager{
    private List<String> data = new ArrayList<String>();
    
    public List<String> dispatch(String[] message) throws Exception{
        if(message[0].equals("init")){
            return this.init(message[1]);
        }
        else if (message[0].equals("getWords")){
            return this.getWords();
        }
        else{
            throw new Exception("Message not understood " + message[0]); 
        }
    }
    
    public DataStorageManager(){
        return;
    }
    
    private List<String> init(String filePath) throws IOException{
        try{
        	BufferedReader r = new BufferedReader(new FileReader(filePath));
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

        return this.data;
    }
    
    private List<String> getWords(){
        List<String> words = new ArrayList<String>();
		for (String line: this.data){
			String [] split = line.split(" ");
        	for (String w:split){
        		words.add(w);
        	}
        }
        return words;
    }
}

class StopWordManager{
    private List<String> stopwords = new ArrayList<String>();
    
    public Boolean dispatch(String[] message) throws Exception{
        if(message[0].equals("init")){
            return this.init();
        }
        else if (message[0].equals("is_stopword")){
            return this.is_stopword(message[1]);
        }
        else{
            throw new Exception("Message not understood " + message[0]); 
        }
    }
    
    public StopWordManager(){
        return;
    }
    
    private Boolean init() throws IOException{
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
        return true;
    }
    
    private Boolean is_stopword(String w) {
        if (w.length()>1 && !this.stopwords.contains(w)){
        	return false;
        }
        return true;
    }   
}
    
class WordFrequencyManager{
    private Map<String, Integer> termFreq = new HashMap<String, Integer>();
    
    public  LinkedHashMap<String, Integer> dispatch(String[] message) throws Exception{
        if(message[0].equals("increment_count")){
            return this.increment_count(message[1]);
        }
        else if (message[0].equals("sorted")){
            return this.sorted();
        }
        else{
            throw new Exception("Message not understood " + message[0]); 
        }
    }
    
    public WordFrequencyManager(){
        return;
    }
    
    
    private LinkedHashMap<String, Integer> increment_count(String word){
        if (this.termFreq.containsKey(word)){
			int curFreq = this.termFreq.get(word)  + 1;
			this.termFreq.put(word,curFreq);
		}
		else{
			this.termFreq.put(word,1);
		}
		return null;
    }
    
    private LinkedHashMap<String, Integer> sorted(){
		LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
		this.termFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));	
    return sortedTermFreq;
    }
}

class WordFrequencyController{
    private DataStorageManager storage_manager;
    private StopWordManager stop_word_manager;
    private WordFrequencyManager word_freq_manager;
    
    public void dispatch(String[] message) throws Exception{
        if(message[0].equals("init")){
            this.init(message[1]);
        }
        else if (message[0].equals("run")){
            this.run();
        }
        else{
            throw new Exception("Message not understood " + message[0]); 
        }
    }
    
    public WordFrequencyController(){
        return;
    }
    
    private void init(String path_to_file) throws Exception{
        this.storage_manager = new DataStorageManager();
        this.stop_word_manager = new StopWordManager();
        this.word_freq_manager = new WordFrequencyManager();
        this.storage_manager.dispatch(new String[]{"init",path_to_file});
        this.stop_word_manager.dispatch(new String[]{"init"});
    }
    
    private void run() throws Exception{
        List<String> words = this.storage_manager.dispatch(new String[]{"getWords"});
        for(String w: words){
            if(!this.stop_word_manager.dispatch(new String[]{"is_stopword",w})){
                this.word_freq_manager.dispatch(new String[]{"increment_count", w});
            }
        }
        LinkedHashMap<String, Integer> word_freqs = this.word_freq_manager.dispatch(new String[]{"sorted"});
        int k = 25;
		int counter = 0;
		for (String key: word_freqs.keySet()){
			if(counter<k){
				System.out.println(key + " - " + Integer.toString(word_freqs.get(key)));
				counter +=1;
			}
	   }    
    }
}

public class Eleven{	
	
	public static void main(String[] args) throws Exception{
	    WordFrequencyController wfcontroller = new WordFrequencyController();
	    wfcontroller.dispatch(new String[]{"init", args[0]});
	    wfcontroller.dispatch(new String[]{"run"});
	}
}