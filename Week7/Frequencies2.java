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

public class Frequencies2 implements TFFreqs{
    
    public Frequencies2(){
        return;
    }
    
    public LinkedHashMap<String, Integer> top25(List<String> word_list){
        Map<String, Integer> termFreq = new HashMap<>();
		for (String w: word_list){
			if (termFreq.containsKey(w)){
				int curFreq = termFreq.get(w)  + 1;
				termFreq.put(w,curFreq);
			}
			else{
				termFreq.put(w,1);
			}
        }
        
        LinkedHashMap<String, Integer> sortedTermFreq = new LinkedHashMap<>();
		termFreq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(25)
                .forEachOrdered(x -> sortedTermFreq.put(x.getKey(), x.getValue()));		
        return sortedTermFreq;
    }

}