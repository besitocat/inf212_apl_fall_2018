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
//extra credit

public class Print2 implements PrintTF{
    
    public Print2(){
        return;
    }
    
    public void printtf(LinkedHashMap<String,Integer> sortedTermFreq){
        for (String key: sortedTermFreq.keySet()){
            System.out.println(key + " - " + Integer.toString(sortedTermFreq.get(key)));
   	    }  	
    }
}