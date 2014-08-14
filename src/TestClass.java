import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class TestClass {

	public static void main(String[] args) {
		InputStream in = null;
		if (args.length > 0) {
			try {
				in = new URL(
						"http://api.goeuro.com/api/v2/position/suggest/en/"
								+ args[0]).openStream();

				InputStreamReader ina = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(ina);
				StringBuffer sb = new StringBuffer();
				while (br.ready()) {
					String next = br.readLine();
					sb.append(next);
				}
				ObjectMapper objectMapper = new ObjectMapper();

				List<Location> navigation = objectMapper.readValue(sb
						.toString(), objectMapper.getTypeFactory()
						.constructCollectionType(List.class, Location.class));
				if (navigation.size() > 0) {
					writeToCSV(navigation);
					System.out.println("CSV file was created!");
				} else {
					System.out.println("There is no match for the string "+ args[0]);
				}
			} catch (MalformedURLException e) {
					System.err.println("There is a problem with URL " + e.getMessage());
			} catch (IOException e) {
				System.err.println("There is I/O problem " + e.getMessage());
			} finally {
				IOUtils.closeQuietly(in);
			}
		} else {
			System.out.println("There is no input parameters!");
		}
	}

	private static final String CSV_SEPARATOR = ",";

	private static void writeToCSV(List<Location> locationList) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("Java_Locations.csv"), "UTF-8"));
			StringBuilder header = new StringBuilder();
			header.append("_type");
			header.append(CSV_SEPARATOR);
			header.append("_id");
			header.append(CSV_SEPARATOR);
			header.append("name");
			header.append(CSV_SEPARATOR);
			header.append("type");
			header.append(CSV_SEPARATOR);
			header.append("latitude");
			header.append(CSV_SEPARATOR);
			header.append("longitude");
			bw.write(header.toString());
			bw.newLine();
			for (Location location : locationList) {
				StringBuffer oneLine = new StringBuffer();
				oneLine.append(location.get_type() == null ? "" : location.get_type());
				oneLine.append(CSV_SEPARATOR);
				oneLine.append(location.get_id() <= 0 ? "" : location.get_id());
				oneLine.append(CSV_SEPARATOR);
				oneLine.append(location.getName());
				oneLine.append(CSV_SEPARATOR);
				oneLine.append(location.getType());
				oneLine.append(CSV_SEPARATOR);
				oneLine.append(location.getGeo_position().getLatitude());
				oneLine.append(CSV_SEPARATOR);
				oneLine.append(location.getGeo_position().getLongitude());
				bw.write(oneLine.toString());
				bw.newLine();
			}

		} catch (UnsupportedEncodingException e) {
			System.err.println("Unsupported encoding for CSV file: " + e);
		} catch (FileNotFoundException e) {
			System.err.println("File can not be found: " + e);
		} catch (IOException e) {
			System.err.println("Error writing the CSV file: " + e);
		} finally {
			if (bw != null) {
				try {
					bw.flush();
					bw.close();
				} catch (IOException e) {
					System.err.print("Cannot append, buffered writer is closed "
									+ e.getStackTrace());
				}
			}
		}
	}

}
