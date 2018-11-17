import java.util.LinkedHashMap;
import java.util.List;

public interface TFFreqs {
	public LinkedHashMap<String, Integer> top25(List<String> words);
}