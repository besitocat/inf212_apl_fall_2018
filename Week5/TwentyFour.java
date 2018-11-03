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
import java.lang.StringBuilder;

//Evita Bakopoulou, UCInet: ebakopou

public class TwentyFour{
    
    static interface IFunction<T1,T2>{
	   T2 call(T1 args);
	}
	
    static class Value<T> implements IFunction<Void, T>{
	    T value;
	    
	    public Value(T value){
	        this.value = value;
	    }
	    
	    public T call(Void arg){
	        return this.value;
	    }
	}
	
	static interface IO<T>{
	    T execute() throws IOException;
	}
    
    //nomad:
    static class TFQuarantine{
        List<IFunction> funcs = new ArrayList<IFunction>();
        
        public <T> TFQuarantine(T arg){
            this.funcs.add(new Value<T>(arg));
        }
        
        public TFQuarantine bind(IFunction func){
            this.funcs.add(func);
            return this;
        }
        
        public Object GuardCallable(Object v){
            if(v instanceof IO){//if it is an IO operation
                try{
                    return ((IO) v).execute();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            return v;
        }
        
        public void execute(){
            Object value = null;
            for(IFunction func: this.funcs){
                value = func.call(GuardCallable(value));
            }
            GuardCallable(value);
        }        
    }
    
    //reads a file and performs the isolated IO operation:
    static IFunction get_input = new IFunction<String,IO>(){
        public IO call(final String path_to_file){
            return new IO(){ 
                public String execute() throws IOException{//isolated IO operation
                    StringBuilder file_content = new StringBuilder();
                    try{
                        BufferedReader r = new BufferedReader(new FileReader(path_to_file));
                    	String line;
                    	while((line=r.readLine())!=null){
                    	    line = line.replaceAll("[\\W]|_", " ");//replace non-alphanumeric characters with a space
                    	    line = line.toLowerCase();
                			file_content.append(line).append(" ");
                        }
                     }
                     catch (IOException e){
                         e.printStackTrace();
                    }
                    return file_content.toString();
                }
            };
        }
    };
    
    
    //extracts words:
    static IFunction extract_words = new IFunction<String,List<String>>(){
        public List<String> call(String fileContent){
            List<String> words = new ArrayList<String>();
        	String [] split = fileContent.split(" ");
    	    for (String w:split){
    	         words.add(w);
            }
            return words;
        }
    };
    
    
    //reads the stopword file, extracts the stopwords and removes them from the list of original data:
    static IFunction remove_stop_words = new IFunction<List<String>, IO>(){
        public IO call(List<String> word_list){
            return new IO<List<String>>(){
                public List<String> execute() throws IOException{//isolated IO operation
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
                        for (String w:word_list){
                        	if (w.length()>1 && !stopword_list.contains(w)){
                        		new_data.add(w);
                        	}
                        }
            	        return new_data;
                }
            };
        }
    }; 
    
    //gets the counts per word:
    static IFunction frequencies = new IFunction<List<String>, Map<String, Integer>>(){
        public Map<String,Integer> call(List<String> data){
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
    };
	
	//sorts the hashmap in descreasing order:
	static IFunction sort = new IFunction<Map<String,Integer>, LinkedHashMap<String,Integer>>(){
	    public LinkedHashMap<String,Integer> call(Map<String,Integer>termFreq){
	        LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
		    termFreq.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));	
            return sortedTermFreq;

	    }
	};
	
	//prints the first 25 entries in the sorted HashMap of Term Frequencies:
	static IFunction top25_freqs = new IFunction<LinkedHashMap<String,Integer>, IO>(){
	    public IO<Void> call(LinkedHashMap<String,Integer> word_freqs){
	        return new IO<Void>(){
	            public Void execute(){//isolated IO operation
	                int k = 25;
            		int counter = 0;
            		for (String key: word_freqs.keySet()){
            			if(counter<k){
            				System.out.println(key + " - " + Integer.toString(word_freqs.get(key)));
            				counter +=1;
            			}
            	    } 
            	    return null;
	            }//execute
	        };
	    }
	};
	
   public static void main(String args[]) throws IOException{
       
       new TFQuarantine(args[0]).bind(get_input).bind(extract_words)
                                .bind(remove_stop_words).bind(frequencies)
                                .bind(sort).bind(top25_freqs).execute(); 
   }
}