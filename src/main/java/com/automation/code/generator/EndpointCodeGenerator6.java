package com.automation.code.generator;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class EndpointCodeGenerator6 {

    public static void main(String[] args) {
        String currentDir = System.getProperty("user.dir");
        String filePath = currentDir + "\\input.yaml";
        GetInputDataFromYAMLFile1 getInputMapFromYAMLFile = new GetInputDataFromYAMLFile1();
        Map<String, String> inputDataMap = getInputMapFromYAMLFile.getInputData(filePath);
        String projectName = inputDataMap.get("project-name");
        String outputPath = inputDataMap.get("outputPath");
        String baseDir = outputPath + File.separator + projectName;
        String sourceFilePath = baseDir + "/src/main/java/com/auto/dev/model/Order.java";
        String outputFolder = baseDir + "/src/main/java/com/auto/dev"; // Replace with your desired output folder

        try {
            String entityClassName = getEntityClassName(sourceFilePath);
            String packageName = getPackageName(sourceFilePath);

            // Generate controller
            generateController(outputFolder, packageName, entityClassName);

            // Generate service
            generateService(outputFolder, packageName, entityClassName);

            // Generate repository
            generateRepository(outputFolder, packageName, entityClassName);

            System.out.println("Code generation completed successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getEntityClassName(String filePath) throws IOException {
        File file = new File(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("class ")) {
                    return line.substring(line.indexOf("class ") + 6, line.indexOf("{")).trim();
                }
            }
        }
        throw new IOException("Entity class name not found in the file: " + filePath);
    }

    private static String getPackageName(String filePath) throws IOException {
        File file = new File(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("package ")) {
                    return line.substring(line.indexOf("package ") + 8, line.indexOf(";")).trim();
                }
            }
        }
        throw new IOException("Package name not found in the file: " + filePath);
    }

    private static void generateController(String outputFolder, String packageName, String entityClassName) throws IOException {
        StringBuilder content = new StringBuilder();

        content.append("package ").append(packageName).append(".controller;\n\n");
        content.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        content.append("import org.springframework.web.bind.annotation.*;\n");
        content.append("import ").append(packageName).append(".model.").append(entityClassName).append(";\n");
        content.append("import ").append(packageName).append(".service.").append(entityClassName).append("Service;\n\n");
        content.append("@RestController\n");
        content.append("@RequestMapping(\"/").append(entityClassName.toLowerCase()).append("\")\n");
        content.append("public class ").append(entityClassName).append("Controller {\n\n");
        content.append("    @Autowired\n");
        content.append("    private ").append(entityClassName).append("Service ").append(entityClassName.toLowerCase()).append("Service;\n\n");

        // GET method
        content.append("    @GetMapping\n");
        content.append("    public Iterable<").append(entityClassName).append("> getAll").append(entityClassName).append("() {\n");
        content.append("        return ").append(entityClassName.toLowerCase()).append("Service.findAll();\n");
        content.append("    }\n\n");

        // GET by id method
        content.append("    @GetMapping(\"/{id}\")\n");
        content.append("    public ").append(entityClassName).append(" get").append(entityClassName).append("ById(@PathVariable Long id) {\n");
        content.append("        return ").append(entityClassName.toLowerCase()).append("Service.findById(id);\n");
        content.append("    }\n\n");

        // POST method
        content.append("    @PostMapping\n");
        content.append("    public ").append(entityClassName).append(" create").append(entityClassName).append("(@RequestBody ").append(entityClassName).append(" ").append(entityClassName.toLowerCase()).append(") {\n");
        content.append("        return ").append(entityClassName.toLowerCase()).append("Service.save(").append(entityClassName.toLowerCase()).append(");\n");
        content.append("    }\n\n");

        // PUT method
        content.append("    @PutMapping(\"/{id}\")\n");
        content.append("    public ").append(entityClassName).append(" update").append(entityClassName).append("(@PathVariable Long id, @RequestBody ").append(entityClassName).append(" ").append(entityClassName.toLowerCase()).append(") {\n");
        content.append("        return ").append(entityClassName.toLowerCase()).append("Service.update(id, ").append(entityClassName.toLowerCase()).append(");\n");
        content.append("    }\n\n");

        // DELETE method
        content.append("    @DeleteMapping(\"/{id}\")\n");
        content.append("    public void delete").append(entityClassName).append("(@PathVariable Long id) {\n");
        content.append("        ").append(entityClassName.toLowerCase()).append("Service.deleteById(id);\n");
        content.append("    }\n");

        content.append("}\n");

        writeToFile(outputFolder + "/controller/" + entityClassName + "Controller.java", content.toString());
    }

    private static void generateService(String outputFolder, String packageName, String entityClassName) throws IOException {
        StringBuilder content = new StringBuilder();

        content.append("package ").append(packageName).append(".service;\n\n");
        content.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        content.append("import org.springframework.stereotype.Service;\n");
        content.append("import ").append(packageName).append(".model.").append(entityClassName).append(";\n");
        content.append("import ").append(packageName).append(".repository.").append(entityClassName).append("Repository;\n\n");
        content.append("@Service\n");
        content.append("public class ").append(entityClassName).append("Service {\n\n");
        content.append("    @Autowired\n");
        content.append("    private ").append(entityClassName).append("Repository ").append(entityClassName.toLowerCase()).append("Repository;\n\n");

        // Find all method
        content.append("    public Iterable<").append(entityClassName).append("> findAll() {\n");
        content.append("        return ").append(entityClassName.toLowerCase()).append("Repository.findAll();\n");
        content.append("    }\n\n");

        // Find by id method
        content.append("    public ").append(entityClassName).append(" findById(Long id) {\n");
        content.append("        return ").append(entityClassName.toLowerCase()).append("Repository.findById(id).orElse(null);\n");
        content.append("    }\n\n");

        // Save method
        content.append("    public ").append(entityClassName).append(" save(").append(entityClassName).append(" ").append(entityClassName.toLowerCase()).append(") {\n");
        content.append("        return ").append(entityClassName.toLowerCase()).append("Repository.save(").append(entityClassName.toLowerCase()).append(");\n");
        content.append("    }\n\n");

        // Update method
        content.append("    public ").append(entityClassName).append(" update(Long id, ").append(entityClassName).append(" ").append(entityClassName.toLowerCase()).append(") {\n");
        content.append("        if (").append(entityClassName.toLowerCase()).append("Repository.existsById(id)) {\n");
        content.append("            ").append(entityClassName.toLowerCase()).append(".setId(id);\n");
        content.append("            return ").append(entityClassName.toLowerCase()).append("Repository.save(").append(entityClassName.toLowerCase()).append(");\n");
        content.append("        }\n");
        content.append("        return null; // Handle not found case\n");
        content.append("    }\n\n");

        // Delete method
        content.append("    public void deleteById(Long id) {\n");
        content.append("        ").append(entityClassName.toLowerCase()).append("Repository.deleteById(id);\n");
        content.append("    }\n");

        content.append("}\n");

        writeToFile(outputFolder + "/service/" + entityClassName + "Service.java", content.toString());
    }

    private static void generateRepository(String outputFolder, String packageName, String entityClassName) throws IOException {
        StringBuilder content = new StringBuilder();

        content.append("package ").append(packageName).append(".repository;\n\n");
        content.append("import ").append(packageName).append(".model.").append(entityClassName).append(";\n");
        content.append("import org.springframework.data.jpa.repository.JpaRepository;\n\n");
        content.append("public interface ").append(entityClassName).append("Repository extends JpaRepository<").append(entityClassName).append(", Long> {\n\n");
        content.append("    // Add custom repository methods if needed\n\n");
        content.append("}\n");

        writeToFile(outputFolder + "/repository/" + entityClassName + "Repository.java", content.toString());
    }

    private static void writeToFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }
}
