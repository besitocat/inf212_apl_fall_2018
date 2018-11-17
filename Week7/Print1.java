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

public class Print1 implements PrintTF{
    
    public Print1(){
        return;
    }
    
    public void printtf(LinkedHashMap<String,Integer> sorted_map){
        sorted_map.forEach((k,v)->System.out.println(k + " - " + v));
    }
}