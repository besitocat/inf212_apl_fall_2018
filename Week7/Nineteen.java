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
import java.lang.reflect.Method; 
import java.lang.reflect.Field; 
import java.lang.reflect.Constructor; 
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
  
//Evita Bakopoulou, UCInet: ebakopou

public class Nineteen {
    
    public Nineteen(){
        return;
    }
    
    public static void create_config_file(){
        
        //inspired by https://www.mkyong.com/java/java-properties-file-examples/
        Properties prop = new Properties();
    	OutputStream output = null;
    
    	try {
    
    		output = new FileOutputStream("config.properties");
    
    		// set the properties value
    		prop.setProperty("words", "Words2.class");
    		prop.setProperty("frequencies", "Frequencies2.class");
            prop.setProperty("print", "Print2.class");    
    		// save properties to project root folder
    		prop.store(output, null);
    
    	} catch (IOException io) {
    		io.printStackTrace();
    	} finally {
    		if (output != null) {
    			try {
    				output.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }

    
    public static void main(String[] args) throws Exception{
        
        //create_config_file(); #run this once and then just modify it
        
        Properties prop = new Properties();
    	InputStream input = null;
        String words_version = "";
        String frequencies_version = "";
        String print_version = "";
    	try {
    
    		input = new FileInputStream("config.properties");
    		prop.load(input);
    		words_version = prop.getProperty("words");
            frequencies_version = prop.getProperty("frequencies");
    		print_version = prop.getProperty("print");
    
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} finally {
    		if (input != null) {
    			try {
    				input.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	
    	List<String> data = new ArrayList<>();
    	LinkedHashMap<String,Integer> sortedTermFreq = new LinkedHashMap<>();
        words_version = words_version.substring(0, words_version.length() - 6);
        frequencies_version = frequencies_version.substring(0, frequencies_version.length() - 6);
        print_version = print_version.substring(0, print_version.length() - 6);
        Method methodcall1;
        Class<?> c;
        Constructor<?> cons;
        Object obj;
        Class cls;
        
        obj = Class.forName(words_version).getConstructor().newInstance();
        cls = obj.getClass(); 
    
        methodcall1 = cls.getDeclaredMethod("extract_words", String.class);
        data = (ArrayList)methodcall1.invoke(obj, args[0]); 
        
        obj = Class.forName(frequencies_version).getConstructor().newInstance();
        cls = obj.getClass();

        methodcall1 = cls.getDeclaredMethod("top25", List.class); 
        sortedTermFreq = (LinkedHashMap)methodcall1.invoke(obj, data); 
	
	    obj = Class.forName(print_version).getConstructor().newInstance();
        cls = obj.getClass();

        methodcall1 = cls.getDeclaredMethod("printtf", LinkedHashMap.class); 
        methodcall1.invoke(obj, sortedTermFreq); 
    }

}