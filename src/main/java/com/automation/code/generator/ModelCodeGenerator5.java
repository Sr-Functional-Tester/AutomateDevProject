package com.automation.code.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ModelCodeGenerator5 {

    public static void generateEntities(String outputFolder, List<EntityDefinition> entities) {
        for (EntityDefinition entity : entities) {
            try {
                generateEntity(outputFolder, entity);
            } catch (IOException e) {
                System.out.println("An error occurred while generating entity: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void generateEntity(String outputFolder, EntityDefinition entity) throws IOException {
        String className = entity.getClassName();
        File file = new File(outputFolder + File.separator + className + ".java");
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("package " + entity.getPackageName() + ";\n\n");
            writer.write("import javax.persistence.*;\n");
            writer.write("import java.util.*;\n\n");

            writer.write("@Entity\n");
            writer.write("public class " + className + " {\n\n");

            boolean hasIdField = entity.getFields().stream().anyMatch(field -> field.toLowerCase().contains("id"));

            if (!hasIdField) {
                writer.write("    @Id\n");
                writer.write("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
                writer.write("    private Long id;\n\n");
            } else {
                for (String field : entity.getFields()) {
                    if (field.toLowerCase().contains("id")) {
                        writer.write("    @Id\n");
                        writer.write("    private " + field + ";\n\n");
                    }
                }
            }

            for (String field : entity.getFields()) {
                if (!field.toLowerCase().contains("id")) {
                    writer.write("    private " + field + ";\n\n");
                }
            }

            for (Relationship relationship : entity.getRelationships()) {
                if (relationship.getType() == RelationshipType.ONE_TO_MANY) {
                    writer.write("    @OneToMany(mappedBy = \"" + entity.getClassName().toLowerCase() + "\")\n");
                    writer.write("    private List<" + relationship.getTargetEntity() + "> " + relationship.getFieldName() + ";\n\n");
                } else if (relationship.getType() == RelationshipType.MANY_TO_ONE) {
                    writer.write("    @ManyToOne\n");
                    writer.write("    @JoinColumn(name = \"" + relationship.getFieldName() + "_id\")\n");
                    writer.write("    private " + relationship.getTargetEntity() + " " + relationship.getFieldName() + ";\n\n");
                } else if (relationship.getType() == RelationshipType.MANY_TO_MANY) {
                    writer.write("    @ManyToMany\n");
                    writer.write("    @JoinTable(\n");
                    writer.write("        name = \"" + entity.getClassName().toLowerCase() + "_" + relationship.getTargetEntity().toLowerCase() + "\",\n");
                    writer.write("        joinColumns = @JoinColumn(name = \"" + entity.getClassName().toLowerCase() + "_id\"),\n");
                    writer.write("        inverseJoinColumns = @JoinColumn(name = \"" + relationship.getTargetEntity().toLowerCase() + "_id\")\n");
                    writer.write("    )\n");
                    writer.write("    private Set<" + relationship.getTargetEntity() + "> " + relationship.getFieldName() + " = new HashSet<>();\n\n");
                }
            }

            writer.write("    // Getters and Setters\n\n");

            if (!hasIdField) {
                writer.write("    public Long getId() {\n");
                writer.write("        return id;\n");
                writer.write("    }\n\n");

                writer.write("    public void setId(Long id) {\n");
                writer.write("        this.id = id;\n");
                writer.write("    }\n\n");
            }

            for (String field : entity.getFields()) {
                String fieldName = field.split(" ")[1];
                String fieldType = field.split(" ")[0];

                writer.write("    public " + fieldType + " get" + capitalize(fieldName) + "() {\n");
                writer.write("        return " + fieldName + ";\n");
                writer.write("    }\n\n");

                writer.write("    public void set" + capitalize(fieldName) + "(" + fieldType + " " + fieldName + ") {\n");
                writer.write("        this." + fieldName + " = " + fieldName + ";\n");
                writer.write("    }\n\n");
            }

            for (Relationship relationship : entity.getRelationships()) {
                String fieldName = relationship.getFieldName();
                String fieldType = "List<" + relationship.getTargetEntity() + ">";

                if (relationship.getType() == RelationshipType.MANY_TO_ONE) {
                    fieldType = relationship.getTargetEntity();
                } else if (relationship.getType() == RelationshipType.MANY_TO_MANY) {
                    fieldType = "Set<" + relationship.getTargetEntity() + ">";
                }

                writer.write("    public " + fieldType + " get" + capitalize(fieldName) + "() {\n");
                writer.write("        return " + fieldName + ";\n");
                writer.write("    }\n\n");

                writer.write("    public void set" + capitalize(fieldName) + "(" + fieldType + " " + fieldName + ") {\n");
                writer.write("        this." + fieldName + " = " + fieldName + ";\n");
                writer.write("    }\n\n");
            }

            writer.write("}\n");
        }
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void main(String[] args) {
        List<String> userEntityFieldsList = Arrays.asList("long userId", "String userName", "String userDescription");
        List<String> postEntityFieldsList = Arrays.asList("long postId", "String postName", "String postDescription");
        List<String> tagEntityFieldsList = Arrays.asList("long tagId", "String tagName", "String tagDescription");

        Map<String, List<String>> entityFieldsMap = new HashMap<>();
        entityFieldsMap.put("User", userEntityFieldsList);
        entityFieldsMap.put("Post", postEntityFieldsList);
        entityFieldsMap.put("Tag", tagEntityFieldsList);

        List<String> relationshipsList = Arrays.asList("User ONE_TO_MANY Post", "Post MANY_TO_MANY Tag");

        String outputFolder = "src/main/java/com/example/demo";
        List<EntityDefinition> entities = new ArrayList<>();
        Map<String, EntityDefinition> entityMap = new HashMap<>();

        for (String value : relationshipsList) {
            String[] arr = value.split(" ");
            String entityName = arr[0];
            String relationshipTypeStr = arr[1];
            String targetEntityName = arr[2];

            RelationshipType relationshipType = RelationshipType.valueOf(relationshipTypeStr.toUpperCase());

            List<String> entityFields = entityFieldsMap.getOrDefault(entityName, new ArrayList<>());
            List<String> targetEntityFields = entityFieldsMap.getOrDefault(targetEntityName, new ArrayList<>());

            EntityDefinition entity = entityMap.getOrDefault(entityName, new EntityDefinition(entityName, "com.example.demo", entityFields, new ArrayList<>()));
            EntityDefinition targetEntity = entityMap.getOrDefault(targetEntityName, new EntityDefinition(targetEntityName, "com.example.demo", targetEntityFields, new ArrayList<>()));

            if (relationshipType == RelationshipType.ONE_TO_MANY) {
                entity.getRelationships().add(new Relationship(targetEntityName.toLowerCase() + "s", targetEntityName, relationshipType));
                targetEntity.getRelationships().add(new Relationship(entityName.toLowerCase(), entityName, RelationshipType.MANY_TO_ONE));
            } else if (relationshipType == RelationshipType.MANY_TO_MANY) {
                entity.getRelationships().add(new Relationship(targetEntityName.toLowerCase() + "s", targetEntityName, relationshipType));
                targetEntity.getRelationships().add(new Relationship(entityName.toLowerCase() + "s", entityName, relationshipType));
            }

            entityMap.put(entityName, entity);
            entityMap.put(targetEntityName, targetEntity);
        }

        entities.addAll(entityMap.values());

        generateEntities(outputFolder, entities);
    }
}

class EntityDefinition {
    private String className;
    private String packageName;
    private List<String> fields;
    private List<Relationship> relationships;

    public EntityDefinition(String className, String packageName, List<String> fields, List<Relationship> relationships) {
        this.className = className;
        this.packageName = packageName;
        this.fields = fields;
        this.relationships = relationships;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }
}

class Relationship {
    private String fieldName;
    private String targetEntity;
    private RelationshipType type;

    public Relationship(String fieldName, String targetEntity, RelationshipType type) {
        this.fieldName = fieldName;
        this.targetEntity = targetEntity;
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public RelationshipType getType() {
        return type;
    }
}

enum RelationshipType {
    ONE_TO_MANY,
    MANY_TO_ONE,
    MANY_TO_MANY
}


