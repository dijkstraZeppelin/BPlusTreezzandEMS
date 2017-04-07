import java.io.File;
import java.io.IOException;

public class ExtMSTester {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		File mainDataFile= new File("sunspot.month.csv");
		
		ExternalMergeSorter e = new ExternalMergeSorter(30,150,mainDataFile);
		
		e.MergeThatData();

	}

}
