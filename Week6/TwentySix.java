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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//Evita Bakopoulou, UCInet: ebakopou


    interface Column{
        List<String> get_formula();
        void apply_formula(List<String> formula);
        List<String> get_words();
    }

    
    interface ColumnNumeric{ //interface for the counts column, which contains integers
        List<Integer> get_formula();
        void apply_formula(List<Integer> formula);
    }
    
    class AllWordsColumn implements Column{
        private List<String> words = new ArrayList<>();
        private List<String> formula = null;
        
        public void update_words(List<String> words){
            this.words = words;
        }
        
        @Override
        public List<String> get_formula(){
            return this.formula;
        }
        
        @Override
        public List<String> get_words(){
            return words;
        }
        
        @Override
        public void apply_formula(List<String> formula){
            
        }
        
        public AllWordsColumn(){
            return;
        }
    }
    
    class StopWordsColumn implements Column{
        private List<String> words = new ArrayList<>();
        private List<String> formula = null;
        
        public void update_words(List<String> words){
            this.words = words;
        }
        
        @Override
        public List<String> get_formula(){
            return this.formula;
        }
        
        @Override
        public void apply_formula(List<String> formula){
            
        }
        
        @Override
        public List<String> get_words(){
            return words;
        }
        
        public StopWordsColumn(){
            return;
        }
        
    }
    
    
    class NonStopWordsColumn implements Column{
        private List<String> words = new ArrayList<>();
        private List<String> formula = new ArrayList<>();
        
        public void update_words(List<String> words){
            this.words = words;
        }
        
        public void set_formula(List<String> formula){
            this.formula = formula;
        }
        
        @Override
        public List<String> get_formula(){
            return this.formula;
        }
        
        @Override
        public void apply_formula(List<String> formula){
            this.words = this.formula;
        }
        
        @Override
        public List<String> get_words(){
            return words;
        }
        
        public NonStopWordsColumn(){
            return;
        }
        
    }
    
    class UniqueWordsColumn implements Column{
        private List<String> words = new ArrayList<>();
        private List<String> formula = new ArrayList<>();
        
        public void update_words(List<String> words){
            this.words = words;
        }
        
        public void set_formula(List<String> formula){
            this.formula = formula;
        }
        
        @Override
        public List<String> get_formula(){
            return this.formula;
        }
        
        @Override
        public void apply_formula(List<String> formula){
            this.words = this.formula;
        }
        
        @Override
        public List<String> get_words(){
            return words;
        }
        
        public UniqueWordsColumn(){
            return;
        }
    }
    
    class CountsColumn implements ColumnNumeric{
        private List<Integer> words = new ArrayList<>();
        private List<Integer> formula = new ArrayList<>();
        
        public void update_words(List<Integer> words){
            this.words = words;
        }
        
        public void set_formula(List<Integer> formula){
            this.formula = formula;
        }
        
        @Override
        public List<Integer> get_formula(){
            return this.formula;
        }
        
        @Override
        public void apply_formula(List<Integer> formula){
            this.words = this.formula;
        }
        
        public List<Integer> get_words(){
            return words;
        }
        public CountsColumn(){
            return;
        }
    }
    
    class SortedDataColumn implements Column{
        private List<String> words = new ArrayList<>();
        private List<String> formula = new ArrayList<>();
        
        public void update_words(List<String> words){
            this.words = words;
        }
        
        public void set_formula(List<String> formula){
            this.formula = formula;
        }
        
        @Override
        public List<String> get_formula(){
            return this.formula;
        }
        
        @Override
        public void apply_formula(List<String> formula){
            this.words = this.formula;
        }
        
        @Override
        public List<String> get_words(){
            return words;
        }
        
        public SortedDataColumn(){
            return;
        }
    }
    
 public class TwentySix{
    
    public static List<Column> all_columns = new ArrayList<>();
    public static CountsColumn counts = new CountsColumn();
    public static AllWordsColumn all_words = new AllWordsColumn();
    public static StopWordsColumn stop_words = new StopWordsColumn();
    public static NonStopWordsColumn non_stop_words = new NonStopWordsColumn();
    public static UniqueWordsColumn unique_words = new UniqueWordsColumn();
    public static SortedDataColumn sorted_data = new SortedDataColumn();
     
    public static void update(){
        List<String> formula;
        Column c;
        for (int i = 0; i < all_columns.size(); i++) {
            c = all_columns.get(i);
            formula = c.get_formula();
            if(formula!=null && !formula.isEmpty()){
                c.apply_formula(formula);
            }
            if (c instanceof SortedDataColumn){
                sorted_data = (SortedDataColumn)c;
            }
            all_columns.set(i, c);
            if(c instanceof UniqueWordsColumn){
                List<Integer> new_formula = counts.get_formula();
                counts.apply_formula(new_formula);
            }
        }
    }
    
    public static Column update_column(Column col){
        List<String> formula = col.get_formula();
        col.apply_formula(formula);
        return col;
    }
    
    public static void main(String[] args){
        
        List<String> words = new ArrayList<String>();
        try{
        	BufferedReader r = new BufferedReader(new FileReader(args[0]));
        	String line;
        	while((line=r.readLine())!=null){
    			line = line.replaceAll("[\\W]|_", " "); //replace non-alphanumeric characters with a space
    			line = line.toLowerCase();
    			String [] split = line.split(" ");
            	for(String w:split){
                	words.add(w);
        	     }
             }   
        }
        catch (IOException e){
        	e.printStackTrace();
        }
    
    all_words.update_words(words);
    
    String stopwords_path = "../stop_words.txt";
    List<String> stopword_list = new ArrayList<String>();
    try{
        BufferedReader r = new BufferedReader(new FileReader(stopwords_path));
        String line;
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

    stop_words.update_words(stopword_list);
    
    List<String> formula_stopwords = (all_words.get_words()).stream()
                                    .filter(w -> !stop_words.get_words().contains(w) && w.length()>1)
                                    .collect(Collectors.toList());
    non_stop_words.set_formula(formula_stopwords);

    non_stop_words = (NonStopWordsColumn)update_column(non_stop_words);
    
    List<String> unique_words_formula = new ArrayList<>();
    unique_words_formula = (non_stop_words.get_words()).stream().distinct().collect(Collectors.toList());
    unique_words.set_formula(unique_words_formula);
    
    unique_words = (UniqueWordsColumn)update_column(unique_words); 
    
    List<Integer> count_formula = new ArrayList<>();
    Map<String, Integer> freq_counts_map = non_stop_words.get_words().parallelStream()
                                            .collect(Collectors.toConcurrentMap(w -> w, w -> 1, Integer::sum));
    
    for(String w: unique_words.get_words()){
        count_formula.add(freq_counts_map.get(w));
    }
    counts.set_formula(count_formula);
    
    List<String> sort_formula = new ArrayList<>();
    LinkedHashMap<String, Integer> sorted_map = new LinkedHashMap<>(); freq_counts_map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(x -> sorted_map.put(x.getKey(), x.getValue()));	
                
    for(String k: sorted_map.keySet()){
        sort_formula.add(k + " - " + Integer.toString(sorted_map.get(k)));
    }
    
    sorted_data.set_formula(sort_formula);

    all_columns.add(all_words);
    all_columns.add(stop_words);
    all_columns.add(non_stop_words);
    all_columns.add(unique_words);
    all_columns.add(sorted_data);
    
    update();
    
    for (String w: sorted_data.get_words().stream().limit(25).collect(Collectors.toList())){
        System.out.println(w);
    }
    }
}