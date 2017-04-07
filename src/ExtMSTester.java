import java.io.File;
import java.io.IOException;

public class ExtMSTester {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		File mainDataFile= new File("sunspot.month.csv");
		//sunspot.month
		ExternalMergeSorter e = new ExternalMergeSorter(30,150,mainDataFile);
		long start = System.nanoTime();    
		e.MergeThatData();
		double elapsedTime = (System.nanoTime() - start)/1000000;
		
		System.out.println("elapsed time is"+elapsedTime);
		

	}

}
