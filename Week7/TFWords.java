import java.util.List;
import java.io.IOException; 

public interface TFWords {
	public List<String> extract_words(String path) throws IOException;
}
