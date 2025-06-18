package mg.itu.prom16.csv;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

public class CsvHelper<T> {

    public List<T> importFromCsv(InputStream is, Charset charset, Function<String[], T> mapper, boolean skipHeader) {
        List<T> result = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, charset))) {
            if (skipHeader) br.readLine();
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = parseCsvLine(line);
                result.add(mapper.apply(values));
            }
        } catch (IOException e) {
            throw new CsvException("CSV import error", e);
        }
        
        return result;
    }

    public void exportToCsv(OutputStream os, Charset charset, List<T> data, Function<T, String[]> converter, String... headers) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, charset))) {
            if (headers.length > 0) {
                writeCsvLine(bw, headers);
            }
            
            for (T item : data) {
                String[] values = converter.apply(item);
                writeCsvLine(bw, values);
            }
        } catch (IOException e) {
            throw new CsvException("CSV export error", e);
        }
    }

    private void writeCsvLine(BufferedWriter bw, String[] values) throws IOException {
        List<String> escapedValues = new ArrayList<>();
        for (String value : values) {
            escapedValues.add(escapeCsvValue(value));
        }
        bw.write(String.join(",", escapedValues));
        bw.newLine();
    }

    private String escapeCsvValue(String value) {
        boolean needsQuotes = value.contains(",") 
                            || value.contains("\"") 
                            || value.contains("\n") 
                            || value.contains("\r");

        String escaped = value.replace("\"", "\"\"");
        
        return needsQuotes ? "\"" + escaped + "\"" : escaped;
    }

    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        int length = line.length();
        
        for (int i = 0; i < length; i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes) {
                    // Vérifier le prochain caractère
                    if (i + 1 < length && line.charAt(i + 1) == '"') {
                        field.append('"');
                        i++; // Saute le prochain guillemet
                    } else {
                        inQuotes = false;
                    }
                } else {
                    inQuotes = true;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }

}
class CsvException extends RuntimeException {
    public CsvException(String message, Throwable cause) {
        super(message, cause);
    }
}