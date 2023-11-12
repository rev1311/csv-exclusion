import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class CsvExclusion {

    private static File file1;
    private static File file2;
    private static File outputFile = new File("C:/Hub/CleanedEmailList.csv");
    public static void main(String[] args) throws Exception {
        System.out.println("");
        System.out.println("********** Choose Mailing list for first file, choose Exclusion list second file. **********");
        System.out.println("");
        showFileChooser();

        try {
            removeEntriesFromFirstFile(file1, file2, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION && file1 == null) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected Mailing List: " + selectedFile.getAbsolutePath());
            file1 = selectedFile;
            showFileChooser();
        } else if (result == JFileChooser.APPROVE_OPTION && file1 != null) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected Exclusion List: " + selectedFile.getAbsolutePath());
            file2 = selectedFile;
        } else {
            System.out.println("No file selected.");
        }
    }

    public static void removeEntriesFromFirstFile(File file1, File file2, File outputFile) throws IOException {
        System.out.println("mapping entries...");
        Set<String> emailSet = getEmailSetFromFile(file2);
        
        try (CSVParser parser = new CSVParser(new FileReader(file1), CSVFormat.EXCEL.withHeader())) {

            try (Writer writer = new FileWriter(outputFile);
                StringReader reader = new StringReader("Email Adress,First Name,Last Name,City,State,Zip,Date Opted,Loc");
                CSVParser headParser = CSVParser.parse(reader, CSVFormat.EXCEL.withHeader())) {

                // Write the header to the output file
                System.out.println("adding headers...");
                headParser.getHeaderMap().forEach((key, value) -> {
                    try {
                        writer.append(key).append(",");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                writer.append(System.lineSeparator());

                // Iterate over records in file1
                System.out.println("finalizing data...");
                for (CSVRecord record : parser) {
                    String email = record.get("Email Address");

                    // Check if the email exists in file2
                    if (!emailSet.contains(email)) {
                        // Write the record to the output file
                        for (String field : record) {
                            writer.append(field).append(",");
                        }
                        writer.append(System.lineSeparator());
                    }
                }
            }
        }
    }

    private static Set<String> getEmailSetFromFile(File file) throws IOException {
        Set<String> emailSet = new HashSet<>();

        try (CSVParser parser = new CSVParser(new FileReader(file), CSVFormat.EXCEL.withHeader())) {
            System.out.println("filtering exclusion data...");
            for (CSVRecord record : parser) {
                String email = record.get("Email Address");
                emailSet.add(email);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emailSet;
    }
    
}

