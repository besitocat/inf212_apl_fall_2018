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
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Map.Entry;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.io.Reader;

//Evita Bakopoulou, UCInet: ebakopou

//Double MapReduce Style


public class ThirtyOne{
    
    public static List<String> stopwords = new ArrayList<>();
    
    
    //fetch batches of k lines:
    public static List<String> get_batch(BufferedReader br, int nlines) throws IOException {
       List<String> lines = new ArrayList<>();
       for (int i = 0; i < nlines; i++){
         try{
             String line = br.readLine();
             if (line != null){
              lines.add(line);
             }
             else{
              return lines;
             }
         }
        catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
      }
      return lines;
    }


    //get each batch of nlines and add their partitions into a list:
    public static List<List<Entry<String,Integer>>> read_partition (String filePath, int nlines){
        List<List<Entry<String,Integer>>> partitions = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            boolean yield = true;
            while (yield) {
              List<String> lines = get_batch(br, nlines);
              if (lines.size() < nlines) {
                yield = false;
               }
              String listString = String.join(" ", lines);
              partitions.add(split_words(listString));
           }
        }
        catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return partitions;
    }

    
    public static void load_stopwords(){
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
    
    
    //mapper:
    public static List<Entry<String,Integer>> split_words(String data_str){
        List<String> words = new ArrayList<>();
        data_str = data_str.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
    	data_str = data_str.toLowerCase();
        String [] split = data_str.split(" ");
        for (String w:split){
	       if (w.length()>1 && !stopwords.contains(w)){
	           words.add(w);
            }
        }
        List<Entry<String,Integer>> result= new ArrayList<>();
        for(String w: words){
            result.add(new SimpleEntry<>(w, 1));
        }
        return result;
    }
    
    
    //groupping:
    public static HashMap<String, List<Entry<String,Integer>>> regroup(List<List<Entry<String,Integer>>> pairs_list){
        HashMap<String, List<Entry<String,Integer>>> mapping = new HashMap<>();
        for (List<Entry<String,Integer>> p_list: pairs_list){
            for(Entry<String,Integer> p: p_list){
                if (mapping.containsKey(p.getKey())){
                    List<Entry<String,Integer>> cur_l = mapping.get(p.getKey());
                    cur_l.add(p);
                    mapping.put(p.getKey(), cur_l);
                }
                else{
                    List<Entry<String,Integer>> cur_l = new ArrayList<>();
                    cur_l.add(p);
                    mapping.put(p.getKey(), cur_l);
                }
            }
        }

        return mapping;
    }
    
    
    //reducer: merges all the counts for each word and places them
    //into a hashamap:
    public static HashMap<String,Integer> count_words(HashMap<String, List<Entry<String,Integer>>> mapping){
        HashMap<String,Integer> updated = new HashMap<>();
        for (String key: mapping.keySet()){
            List<Entry<String,Integer>> values = mapping.get(key);
            int count = 0;
            for (Entry<String,Integer> e: values){
                count += e.getValue();               
            }
            if (updated.containsKey(key)){
                int cur_count = updated.get(key);
                updated.put(key, cur_count+count);
            }
            else{
                updated.put(key, count);
            }
        }
        return updated;
    }
    

    public static LinkedHashMap<String, Integer> sort(HashMap<String, Integer> word_freq){
        LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
		word_freq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(25)
                .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));	
                
        return sortedTermFreq;
    }
    
    
    public static void main(String[] args) throws IOException{
        load_stopwords();
        List<List<Entry<String,Integer>>> splits;
        splits = read_partition(args[0], 200);
        HashMap<String, List<Entry<String,Integer>>> mapping;
        mapping = regroup(splits);
        HashMap<String, Integer> freqs;
        freqs = count_words(mapping);
        LinkedHashMap<String, Integer> sorted_freqs = sort(freqs);
        for (String key: sorted_freqs.keySet()){
			System.out.println(key + " - " + Integer.toString(sorted_freqs.get(key)));
	    } 
    }
}