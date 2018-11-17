import java.util.List;
import java.util.ArrayList;
import java.util.Collections; 
import java.util.Comparator; 
import java.util.HashMap; 
import java.util.LinkedHashMap; 
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
  
//Evita Bakopoulou, UCInet: ebakopou

public class Frequencies1 implements TFFreqs{
    
    public Frequencies1(){
        return;
    }
    
    public LinkedHashMap<String, Integer> top25(List<String> word_list){
        Map<String, Integer> termFreq = word_list.parallelStream()
                                            .collect(Collectors.toConcurrentMap(w -> w, w -> 1, Integer::sum));
        LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
		termFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(25)
                .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));		
        
        return sortedTermFreq;
    }

}