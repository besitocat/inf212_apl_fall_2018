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
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


//Evita Bakopoulou, UCInet: ebakopou

//Dataspaces Style 29.1 & 29.2



public class TwentyNine{
    public final BlockingQueue<String> word_space = new LinkedBlockingQueue<>();
    public final BlockingQueue<HashMap<String, Integer>> freq_space = new LinkedBlockingQueue<>();
    public final List<String> stopwords = new ArrayList<>();
    public final Map<String, Integer> freq_map = new ConcurrentHashMap<>();
    
    public TwentyNine(){}
    
    static class Consumer extends Thread{
        public Consumer(Runnable r){
            super(r);
        }
        
        @Override
        public void run(){
            try{
                super.run();
            }
            catch(Exception e){
                throw new RuntimeException(e); 
            }
        }
    }//Consumer

    public void load_stopwords() throws IOException{
        String stopwords_path = "../stop_words.txt";
        try{
        	BufferedReader r = new BufferedReader(new FileReader(stopwords_path));
        	String line;
        	while((line=r.readLine())!=null){
        		String [] split_line = line.split(",");
        		for (String w: split_line){
        			stopwords.add(w);
        		}
        	}
        }
        catch (IOException e){
        	e.printStackTrace();
        }
    }
    
    public void load_words_to_queue(String filePath) throws IOException{
        String line;
        try{
            BufferedReader r = new BufferedReader(new FileReader(filePath));
            while((line=r.readLine())!=null){
               line = line.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
    	       line = line.toLowerCase();
    		   String [] split = line.split(" ");
        	   for (String w:split){
        	       if (w.length()>1){
        	           try{
        	               this.word_space.put(w);
        	           }
        	           catch (Exception e){
        	               e.printStackTrace();
        	               throw new RuntimeException("Couldn't add item to queue.");
        	           }
            	   }//if
                }//for
            }//while
        }//try
        catch(IOException e){
            e.printStackTrace();
        }

    }
    
    public void process_words(){
        HashMap<String, Integer> word_freqs = new HashMap<>();
        String word;
        while(true){
            try{
                word = this.word_space.poll(1, TimeUnit.SECONDS);
                if(word==null){
                    break;
                }

           }
            catch(InterruptedException e){
                break;
            }
            if(!stopwords.contains(word)){
                if(!word_freqs.containsKey(word)){
                   word_freqs.put(word, 1); 
                }
                else{
                    int count = word_freqs.get(word);
                    word_freqs.put(word, count + 1);
                }
            }
        }
        try{
            this.freq_space.put(word_freqs);
        }
        catch (InterruptedException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
    }
    
    public void merge_freqs(){
        while(true){
            try{
                HashMap<String,Integer> map = this.freq_space.poll(1, TimeUnit.SECONDS);
                if(map==null){ 
                    break;
                }
                 for(String key: map.keySet()){
                     freq_map.merge(key, map.get(key), (existingValue, newValue) -> existingValue + newValue); 
                 }
             }

            catch(InterruptedException e){
                break;
            }
        }
    }
    
    
	public static void main(String[] args) throws IOException{
	    TwentyNine tn = new TwentyNine();
	    tn.load_stopwords();
	    tn.load_words_to_queue(args[0]);
	    
	    List<Consumer> workers = new ArrayList<>();

	    for(int i=0; i<5; i++){
	        Consumer c = new Consumer(tn::process_words);
	        workers.add(c);
	    }
	    
	    for(Thread w: workers){
	       try{
	           w.start();
	        }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }
	    }
	    
	   for(Thread w: workers){
	       try{
	           w.join();
	        }
            catch (InterruptedException e){
                e.printStackTrace();
            }
	    }
	    
       //workers for merging the HashMap
	   workers = new ArrayList<>();

	    for(int i=0; i<5; i++){
	        Consumer c = new Consumer(tn::merge_freqs);
	        workers.add(c);
	    }
	    
	    for(Thread w: workers){
	       try{
	           w.start();
	        }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }
	    }
	    
	   for(Thread w: workers){
	       try{
	           w.join();
	        }
            catch (InterruptedException e){
                e.printStackTrace();
            }
	    }

	    LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
		tn.freq_map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(25)
                .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));	
	    
	    for (String key: sortedTermFreq.keySet()){
			System.out.println(key + " - " + Integer.toString(sortedTermFreq.get(key)));
	    }
	}
}