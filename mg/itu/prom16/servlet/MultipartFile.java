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
    private String racine;

    public MultipartFile(Part part,String racine) {
        this.part = part;
        this.racine = racine.substring(1);
    }

    public String saveFile(String uploadDirectory, String fileName) throws IOException {
        // Si fileName est vide ou nul, utiliser le nom de fichier original
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = getOriginalFileName();
        }

        // Si le chemin commence par "/", on le considère relatif à la racine de l'application web
        uploadDirectory = uploadDirectory.trim();
        if (uploadDirectory.startsWith("/")) {
            uploadDirectory = racine + uploadDirectory;
        }
        uploadDirectory = uploadDirectory.replace("\\", File.separator);
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

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
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

    public Long getFileLength() {
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
        return part==null;
    }
}
