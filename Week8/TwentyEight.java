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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


//Evita Bakopoulou, UCInet: ebakopou

//Actors Style 28.1 & 28.2

abstract class ActiveWFObject extends Thread{
    BlockingQueue<Object[]> queue = new ArrayBlockingQueue(1024);
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
    private ActiveWFObject wordFreqsManager;
    private List<String> data = new ArrayList<String>();
    private List<String> stopwords = new ArrayList<String>();
    
    @Override
    public void dispatch(Object[] message){
        if(((String)message[0]).equals("init")){
            this.init(message);
        }
        else if (((String)message[0]).equals("send_word_freqs")){
            this.process_words(message);
        }
        else{
            send(this.wordFreqsManager, message); //foward the "die" message
        }
    }
    
    private void init(Object[] message){
        String filePath = (String)message[1];
        ActiveWFObject wordFreqsManager = (ActiveWFObject)message[2];
        this.wordFreqsManager = wordFreqsManager;
        
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
        	throw new RuntimeException(e);
        }
        
        try{
        	BufferedReader r = new BufferedReader(new FileReader(filePath));
        	while((line=r.readLine())!=null){
        	    line = line.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
			    line = line.toLowerCase();
        		this.data.add(line);
        	}
        }
        catch (IOException e){
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
    }
    
    private void process_words(Object[] message){
        ActiveWFObject recipient = (ActiveWFObject)message[1];
        List<String> words = new ArrayList<String>();
		for (String line: this.data){
			String [] split = line.split(" ");
        	for (String w:split){
        		if (w.length()>1 && !this.stopwords.contains(w)){
        	       send(this.wordFreqsManager, new Object[]{"word", w});
                }
        	}
        }
        send(this.wordFreqsManager, new Object[]{"top25", recipient});
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

public class TwentyEight{	
	
	public static void main(String[] args){
	    
	    ActiveWFObject wordFreqManager = new WordFrequencyManager();
	    ActiveWFObject dataManager = new DataStorageManager();
	    
	    ActiveWFObject.send(dataManager, new Object[]{"init", args[0], wordFreqManager});
	    
	    ActiveWFObject wordFreqController = new WordFrequencyController();
	    ActiveWFObject.send(wordFreqController, new Object[]{"run", dataManager});
	    
	    List<ActiveWFObject> activeObjects = new ArrayList<>();
	    activeObjects.add(wordFreqManager);
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