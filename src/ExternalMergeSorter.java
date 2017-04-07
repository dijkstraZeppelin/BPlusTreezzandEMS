import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

public class ExternalMergeSorter {

	int chunkSize;
	int numRecords;
	int numFiles;
	int filesExisting;
	File mainDataFile;
	int DEGREE;
	File segmentFile;
	FileWriter segWriter;
	BufferedWriter segBuffer;

	int CompareInt(int i1, int i2) {
		return (i1 < i2) ? 1 : 0;
	}

	public ExternalMergeSorter(int chunkSize, int numRecords, File mainDataFile) {
		this.chunkSize = chunkSize;
		this.numRecords = numRecords;
		this.mainDataFile = mainDataFile;
		this.DEGREE = 4;
		// this.segmentFile=segmentFile;
		// this.segWriter=segWriter;
		// this.segmentFile=segmentFile;

	}

	ArrayList<ArrayList<String>> SortTheChunk(ArrayList<ArrayList<String>> toBeSorted) {

		Collections.sort(toBeSorted, new Comparator<ArrayList<String>>() {
			@Override
			public int compare(ArrayList<String> one, ArrayList<String> two) {

				System.out.println(one.get(0) + "  xxxxxxxxxxxxxxxxxxx  " + one.get(0).replace("\"", ""));
				int i1 = Integer.parseInt(one.get(0).replace("\"", ""));
				int i2 = Integer.parseInt(two.get(0).replace("\"", ""));

				// return compare(i1, i2);
				return (i1 < i2) ? 1 : 0;
			}
		});

		return toBeSorted;
	}

	void WriteToDisk(int iteration, ArrayList<ArrayList<String>> toBeSorted) throws IOException {

		File segmentFile = new File("segment" + iteration + ".txt");
		FileWriter segWriter = new FileWriter(segmentFile);
		BufferedWriter segBuffer = new BufferedWriter(segWriter);

		toBeSorted = SortTheChunk(toBeSorted);

		for (ArrayList<String> tokens : toBeSorted) {

			System.out.println(tokens);
			String outputString = tokens.toString();

			segBuffer.write(outputString.replaceAll("\\[", "").replaceAll("\\[", "").replace("\"", "") + "\n");
		}

		segBuffer.close();
	}

	void DeleteAllFiles(int numFiles, int iteration) {
		for (int i = 0; i < numFiles; i++) {
			File toBeDeleted = new File("segment" + i + ".txt");
			toBeDeleted.delete();
		}
		for (int j = 0; j < iteration; j++) {
			File oldName = new File("new" + j + ".txt");
			File newName = new File("segment" + j + ".txt");
			oldName.renameTo(newName);

			// if(oldName.renameTo(newName)) {
			// System.out.println("renamed");
			// } else {
			// System.out.println("Error");
			// }
		}

	}

	int BufferNotNull(String[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] != null)
				return 0;
		}

		return 1;
	}

	void BringNext(String[] filePointers, String[] Buffer, int minIndex, BufferedReader[] segBuffers)
			throws IOException {

		filePointers[minIndex] = segBuffers[minIndex].readLine();
		if (filePointers[minIndex] == (null)) {
			Buffer[minIndex] = null;
			return;
		}

		List<String> elements = Arrays.asList(filePointers[minIndex].split(","));
		Buffer[minIndex] = elements.get(0);

	}

	void MergeOnce() throws IOException {

		File[] segmentFiles = new File[chunkSize];
		FileReader[] segWriters = new FileReader[chunkSize];
		BufferedReader[] segBuffers = new BufferedReader[chunkSize];
		String[] filePointers = new String[chunkSize];
		String[] Buffer = new String[chunkSize];

		filesExisting = numFiles; // total number of files in that merge level

		int start = 0; // start will grow to accomodate all files
		int iteration = 0; // which is the id of current OUTPUT file as a result
							// of mergeonce

		while (filesExisting >= 0) {
			System.out.println("iteratrion " + iteration);
			int i = start;

			while ((i - start < chunkSize) && (i != numFiles)) {
				System.out.println("here in while " + iteration);
				segmentFiles[i - start] = new File("segment" + i + ".txt");
				segWriters[i - start] = new FileReader(segmentFiles[i - start]);
				segBuffers[i - start] = new BufferedReader(segWriters[i - start]);
				filePointers[i - start] = segBuffers[i - start].readLine();

				List<String> elements = Arrays.asList(filePointers[i - start].split(","));

				Buffer[i - start] = elements.get(0); // buffer has only primary
														// key of a data row.

				System.out.println(Buffer[i - start] + " is a buffer value");
				i++;

			}

			File outputFile = new File("new" + iteration + ".txt");
			FileWriter outputWriter = new FileWriter(outputFile);
			BufferedWriter outputBuffer = new BufferedWriter(outputWriter);

			while (BufferNotNull(Buffer) == 0)// main insertion into output file
												// is happening here
			{
				int minIndex = 0;
				int minVal = 9999;

				for (int j = 0; j < chunkSize; j++) {
					if (Buffer[j] == (null))
						continue;

					if (CompareInt(Integer.parseInt(Buffer[j]), minVal) > 0) {
						minIndex = j;
						minVal = Integer.parseInt(Buffer[j]);
					}

				}

				outputBuffer.write((filePointers[minIndex].replaceAll("\\[", "").replaceAll("\\]", "") + "\n"));
				System.out.println("inserted one" + minVal);
				BringNext(filePointers, Buffer, minIndex, segBuffers);

			}
			System.out.println("Buffer EMPTIED***********");

			outputBuffer.close();
			iteration++;
			System.out.println("iteration added***********");

			start = start + chunkSize;
			filesExisting -= chunkSize;

			System.out.println("Files existing are :" + filesExisting);

			if (filesExisting <= 0)
				break;

		}
		DeleteAllFiles(numFiles, iteration);

		this.numFiles = iteration;

	}

	void MergeFully() throws IOException {
		while (this.numFiles != 1) {
			MergeOnce();
		}
	}

	void ConditionOutputFile() throws IOException {
		BufferedReader readFinal = new BufferedReader(new FileReader(new File("segment0.txt")));
		String inputString = "", outputString = "", outputString2 = "";

		File outputFile = new File("bplustree.inp");
		FileWriter outputWriter = new FileWriter(outputFile);
		BufferedWriter outputBuffer = new BufferedWriter(outputWriter);

		// File outputFile1 = new File("bplustreeunsorted.inp");
		// FileWriter outputWriter1 = new FileWriter(outputFile1);
		// BufferedWriter outputBuffer1 = new BufferedWriter(outputWriter1);

		File outputFile2 = new File("bplustreebottomup.inp");
		FileWriter outputWriter2 = new FileWriter(outputFile2);
		BufferedWriter outputBuffer2 = new BufferedWriter(outputWriter2);

		outputBuffer.write(DEGREE + "\n");
		outputBuffer2.write(DEGREE + "\n");
		// outputBuffer1.write(DEGREE+"\n");

		while ((inputString = readFinal.readLine()) != null) {

			List<String> elements = Arrays.asList(inputString.split(","));

			String newStr = elements.get(0);
			outputString = "i " + newStr.replace("\"", "") + "\n";
			outputString2 = newStr.replace("\"", "") + "\n";
			outputBuffer.write(outputString);
			outputBuffer2.write(outputString2);
		}
		outputBuffer.write("p\n");
		outputBuffer.write("q\n");
		readFinal.close();
		outputBuffer.close();
		outputBuffer2.close();

	}

	void MakeUnsorted() throws IOException {
		BufferedReader readFromMain = new BufferedReader(new FileReader(mainDataFile));
		BufferedWriter unsortedOutput = new BufferedWriter(new FileWriter(new File("bplustreeunsorted.inp")));
		// String unsortedString = "";
		String inputString = "";
		unsortedOutput.write(DEGREE+"\n");
		inputString = readFromMain.readLine();
		while ((inputString = readFromMain.readLine()) != null) {

			List<String> elements = Arrays.asList(inputString.split(","));
			String outputString = elements.get(0);
			unsortedOutput.write(("i " + outputString.replace("\"", "").replaceAll("\\[", "").replaceAll("\\]", "") + "\n"));
		}
		unsortedOutput.write("p\n");
		unsortedOutput.write("q\n");

		unsortedOutput.close();
		readFromMain.close();
		System.out.println("success2342223252");
	}

	void FirstPass() throws IOException {

		BufferedReader readFromMain = new BufferedReader(new FileReader(mainDataFile));
		String inputString = "";

		int numScanned = 0, i = -1;
		ArrayList<ArrayList<String>> toBeSorted = new ArrayList<ArrayList<String>>();

		inputString = readFromMain.readLine();
		while ((inputString = readFromMain.readLine()) != null) {

			// System.out.println(inputString);

			// System.out.println(unsortedString);

			ArrayList<String> tokens = new ArrayList<String>();

			StringTokenizer st = new StringTokenizer(inputString, ",");

			// int firstKey=0;
			//
			// firstKey=Integer.parseInt(st.nextToken());

			while (st.hasMoreTokens()) {

				tokens.add(st.nextToken());

			}

			toBeSorted.add(tokens);
			numScanned++;

			if (numScanned == chunkSize) {
				i++;
				WriteToDisk(i, toBeSorted);
				toBeSorted = new ArrayList<ArrayList<String>>();
				numScanned = 0;

			}

		}

		readFromMain.close();

		this.numFiles = i + 1;

		System.out.println(numFiles);

	}

	void MergeThatData() throws IOException {
		MakeUnsorted();
		FirstPass();
		MergeFully();
		ConditionOutputFile();

	}

}
