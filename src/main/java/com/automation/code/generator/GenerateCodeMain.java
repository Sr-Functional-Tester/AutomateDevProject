package com.automation.code.generator;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GenerateCodeMain {
    public static void main(String[] args){
        String currentDir = System.getProperty("user.dir");
        String filePath = currentDir+"\\input.yaml";
        GetInputDataFromYAMLFile1 getInputMapFromYAMLFile = new GetInputDataFromYAMLFile1();

        Map<String, String> inputDataMap = getInputMapFromYAMLFile.getInputData(filePath);

        //Generate Project Code base
        String projectName = inputDataMap.get("project-name");
        String packageName = inputDataMap.get("package");
        String projectType = inputDataMap.get("project-type");
        String outputPath= inputDataMap.get("outputPath");
        
        ProjectGenerator2.projectGenerator(projectName, packageName, projectType, outputPath);

        CopyPomFileToProject(projectName, packageName, outputPath, inputDataMap);
        CopyPropertiesFileToProject(inputDataMap, projectName, outputPath);

    }

    private static void CopyPropertiesFileToProject(Map<String, String> inputDataMap, String projectName, String outputPath) {
        String dsUrl= inputDataMap.get("datasource.url");
        String dsUserName= inputDataMap.get("datasource.username");
        String dsPassword= inputDataMap.get("datasource.password");
        String dsDriverClass= inputDataMap.get("datasource.driver-class");
        Map<String, String> replacements = new HashMap<>();
        replacements.put(ConstantStrings4.dsUrl, dsUrl);
        replacements.put(ConstantStrings4.dsUserName, dsUserName);
        replacements.put(ConstantStrings4.dsPassword, dsPassword);
        replacements.put(ConstantStrings4.dsDriver, dsDriverClass);
        String outputFolder = outputPath + File.separator + projectName + File.separator + ConstantStrings4.applicationPropertiesOutPath;
        CopyFilesToProject3.copyFile(ConstantStrings4.applicationPropertiesPath, replacements, outputFolder);


    }

    private static void CopyPomFileToProject(String projectName, String packageName, String outputPath, Map<String, String> inputDataMap) {
        String javaVersion= inputDataMap.get("java-version");
        String springBootVersion= inputDataMap.get("spring-boot-version");
        String projectDesc= inputDataMap.get("project-description");
        Map<String, String> replacements = new HashMap<>();
        replacements.put(ConstantStrings4.groupId, "<groupId>"+ packageName +"</groupId>");
        replacements.put(ConstantStrings4.artifactId, "<artifactId>"+ projectName +"</artifactId>");
        replacements.put(ConstantStrings4.springBootVersion, "<version>"+ springBootVersion +"</version>");
        replacements.put(ConstantStrings4.name, "<name>"+ projectName +"</name>");
        replacements.put(ConstantStrings4.description, "<description>"+ projectDesc +"</description>");
        replacements.put(ConstantStrings4.javaVersion, "<java.version>"+ javaVersion +"</java.version>");
        String outputFolder = outputPath + File.separator + projectName;
        CopyFilesToProject3.copyFile(ConstantStrings4.pomFilePath, replacements, outputFolder);
    }

}
