import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//Evita Bakopoulou, UCInet: ebakopou

public class Words2 implements TFWords{

    public Words2(){
        return;
    }
    
    public List<String> extract_words(String path) throws IOException{
        String stopwords_path = "../stop_words.txt";
        List<String> stopwords = new ArrayList<>();
        List<String> words = new ArrayList<>();
		String line;
        try{
            BufferedReader r = new BufferedReader(new FileReader(path));
            while((line=r.readLine())!=null){
                line = line.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
			    line = line.toLowerCase();
            	String [] split_line = line.split(" ");
            	for (String w: split_line){
            	      words.add(w);
            	}
        	 }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        
        try{
            BufferedReader r = new BufferedReader(new FileReader(stopwords_path));
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
        return  (words).stream().filter(w -> !stopwords.contains(w) && w.length()>1)
                                .collect(Collectors.toList());
    }
}

 