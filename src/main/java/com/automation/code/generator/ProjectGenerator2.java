package com.automation.code.generator;

import java.io.File;

public class ProjectGenerator2 {

    public static void projectGenerator(String projectName, String packageName, String projectType, String outputFolder) {
        createProjectStructure(projectName, packageName, projectType, outputFolder);
    }

    private static void createProjectStructure(String projectName, String packageName, String projectType, String outputFolder) {
        String baseDir = outputFolder + File.separator + projectName;
        String srcDir = baseDir + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + packageName.replace('.', File.separatorChar);
        String resourcesDir = baseDir + File.separator + "src" + File.separator + "main" + File.separator + "resources";
        String testDir = baseDir + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + packageName.replace('.', File.separatorChar);

        createDirectory(baseDir);
        createDirectory(srcDir);
        createDirectory(resourcesDir);
        createDirectory(testDir);

        if ("web".equalsIgnoreCase(projectType)) {
            createDirectory(baseDir + File.separator + "src" + File.separator + "main" + File.separator + "webapp");
        }

        System.out.println("Project structure created successfully under the specified output directory!");
    }

    private static void createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Created directory: " + path);
            } else {
                System.out.println("Failed to create directory: " + path);
            }
        }
    }
}