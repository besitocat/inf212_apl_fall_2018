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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.Paths;
import java.nio.file.Files;

//Evita Bakopoulou, UCInet: ebakopou

//Lazy Rivers with Actors instead of yield

abstract class ActiveWFObject extends Thread{
    BlockingQueue<Object[]> queue = new LinkedBlockingQueue<>();
    boolean stop;
    
    public ActiveWFObject(){
        this.stop = false;
        this.start();
    }
    
    public void run(){
        try{
            while(!this.stop){
                Object[] message = this.queue.take();
                this.dispatch(message);
                if((String)message[0] == "die"){
                    this.stop = true;
                }
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static void send(ActiveWFObject receiver, Object[] message){
        try{
            receiver.queue.put(message);
        }
        catch (InterruptedException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public abstract void dispatch(Object[] message);

}

class DataStorageManager extends ActiveWFObject{
    private ActiveWFObject stopwordManager;
    private String filePath;
    
    @Override
    public void dispatch(Object[] message){
        if(((String)message[0]).equals("init")){
            this.init(message);
        }
        else if (((String)message[0]).equals("send_word_freqs")){
            this.process_words2(message);
        }
        else{
            send(this.stopwordManager, message); //foward the "die" message
        }
    }
    
    private void init(Object[] message){
        this.filePath = (String)message[1];
        ActiveWFObject stopwordManager = (ActiveWFObject)message[2];
        this.stopwordManager = stopwordManager;
    }
    
    //process line by line, does not load all the content of the file at once:
    private void process_words2(Object[] message){
        ActiveWFObject recipient = (ActiveWFObject)message[1];  
        try(Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(line -> process_line(line));
        }
        catch (IOException e){
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
        send(this.stopwordManager, new Object[]{"top25", recipient});
    }
    
    private void process_line(String line){
        line = line.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
        line = line.toLowerCase();
		String [] split = line.split(" ");
		for (String w: split){
            send(this.stopwordManager, new Object[]{"filter", w});
        }
    }
    
}
    
class StopWordManager extends ActiveWFObject{
    List<String> stopwords = new ArrayList<>();
    ActiveWFObject wordFreqManager;
    
    @Override
    public void dispatch(Object[] message){
        if(((String)message[0]).equals("init")){
            this.init((ActiveWFObject)message[1]);
        }
        else if (((String)message[0]).equals("filter")){
            this.filter((String)message[1]);
        }
        else{
            send(wordFreqManager, message);
        }
    }
    
    private void filter(String w){
        if (w.length()>1 && !this.stopwords.contains(w)){
            send(this.wordFreqManager, new Object[]{"word", w});
        }
    }
    
    private void init(ActiveWFObject wordFreqManager){
        this.wordFreqManager = wordFreqManager;
		try(BufferedReader br = Files.newBufferedReader(Paths.get("../stop_words.txt"))) {
			for (String line: br.lines().collect(Collectors.toList())){
			    String[] split = line.split(",");
			    for(String w: split){
			        this.stopwords.add(w);
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
}    


class WordFrequencyManager extends ActiveWFObject{
    private Map<String, Integer> termFreq = new HashMap<String, Integer>();
    
    @Override
    public void dispatch(Object[] message){
        if(((String)message[0]).equals("word")){
            this.increment_count((String)message[1]);
        }
        else if (((String)message[0]).equals("top25")){
            this.top25((ActiveWFObject)message[1]);
        }
        else if (((String)message[0]).equals("die")){
             this.stop = true;
        }
        else{
             throw new RuntimeException("no such message found: " + (String)message[0]);
        }
    }
    
    private void increment_count(String word){
        if (this.termFreq.containsKey(word)){
			int curFreq = this.termFreq.get(word)  + 1;
			this.termFreq.put(word,curFreq);
		}
		else{
			this.termFreq.put(word,1);
		}
    }
    
    private void top25(ActiveWFObject recipient){
		LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
		this.termFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(25)
                .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));	
	   send(recipient, new Object[]{"top25", sortedTermFreq});
    }
}

class WordFrequencyController extends ActiveWFObject{
    private ActiveWFObject storage_manager;

    @Override
    public void dispatch(Object[] message){
        if(((String)message[0]).equals("run")){
            run((ActiveWFObject)message[1]);
            
        }
        else if (((String)message[0]).equals("top25")){
            display((LinkedHashMap<String, Integer>)message[1]);
        }
        else{
            throw new RuntimeException("no such message found: " + (String)message[0]);
        }
    }
    
    private void run(ActiveWFObject storage_manager){
        this.storage_manager = storage_manager;
        send(storage_manager, new Object[] {"send_word_freqs", this});
    }
    
    private void display(LinkedHashMap<String, Integer> word_freqs){
	   for (String key: word_freqs.keySet()){
			System.out.println(key + " - " + Integer.toString(word_freqs.get(key)));
	   }
	   send(this.storage_manager, new Object[]{"die"}); //pass around the "die" message
	   this.stop = true;
    }
}

public class TwentyEightThree{	
	
	public static void main(String[] args){
	    ActiveWFObject stopwordManager = new StopWordManager();
	    ActiveWFObject wordFreqManager = new WordFrequencyManager();
	    
	    ActiveWFObject.send(stopwordManager,new Object[]{"init", wordFreqManager});
        ActiveWFObject dataManager = new DataStorageManager();	   
	    ActiveWFObject.send(dataManager, new Object[]{"init", args[0], stopwordManager});
	    
	    ActiveWFObject wordFreqController = new WordFrequencyController();
	    ActiveWFObject.send(wordFreqController, new Object[]{"run", dataManager});
	    
	    List<ActiveWFObject> activeObjects = new ArrayList<>();
	    activeObjects.add(wordFreqManager);
	    activeObjects.add(stopwordManager);
	    activeObjects.add(dataManager);
	    activeObjects.add(wordFreqController);
	    
	    for(ActiveWFObject t: activeObjects){
	        try{
	           t.join(); 
	        }
	        catch (InterruptedException e) {
			e.printStackTrace();
	        }
	    }
	}
}