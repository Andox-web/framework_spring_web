package mg.itu.prom16.servlet;

import jakarta.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MultipartFile {
    private Part part;

    public MultipartFile(Part part) {
        this.part = part;
    }

    public String saveFile(String uploadDirectory, String fileName) throws IOException {
        // Si fileName est vide ou nul, utiliser le nom de fichier original
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = getOriginalFileName();
        }

        String filePath = uploadDirectory + File.separator + fileName;
        Files.createDirectories(Paths.get(uploadDirectory));
        
        try (InputStream inputStream = part.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return filePath;
    }


    public byte[] getBytes() throws IOException {
        try (InputStream inputStream = part.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }

    public String getFileType() {
        return part.getContentType();
    }

    public long getFileLength() {
        return part.getSize();
    }

    public String getOriginalFileName() {
        String contentDisposition = part.getHeader("content-disposition");
        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown";
    }

    public boolean isEmpty() {
        return getFileLength() == 0;
    }
}
