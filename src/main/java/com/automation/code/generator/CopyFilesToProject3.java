package com.automation.code.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class CopyFilesToProject3 {

    public static void copyFile(String filePath, Map<String, String> replacements, String outputFolder) {
        String currentDir = System.getProperty("user.dir");
        String fullFilePath = currentDir + File.separator + filePath;
        try {
            String tempFilePath = createTempFile(fullFilePath, replacements);
            copyFileToNewLocation(tempFilePath, outputFolder, new File(fullFilePath).getName());
            deleteTempFile(tempFilePath);
            System.out.println("Text replacement and file copy completed successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String createTempFile(String filePath, Map<String, String> replacements) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("The specified file does not exist: " + filePath);
        }
        File tempFile = File.createTempFile("temp", ".file");

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    line = line.replace(entry.getKey(), entry.getValue());
                }
                writer.write(line);
                writer.write(System.lineSeparator());
            }
        }

        return tempFile.getAbsolutePath();
    }

    private static void copyFileToNewLocation(String tempFilePath, String baseDir, String fileName) throws IOException {
        File destinationDir = new File(baseDir);
        if (!destinationDir.exists()) {
            if (!destinationDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + baseDir);
            }
        }

        File sourceFile = new File(tempFilePath);
        File destinationFile = new File(baseDir, fileName);

        Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void deleteTempFile(String tempFilePath) throws IOException {
        File tempFile = new File(tempFilePath);
        if (!tempFile.delete()) {
            throw new IOException("Failed to delete temporary file: " + tempFilePath);
        }
    }
}
