package com.github.peacetrue.template;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 模型。
 *
 * @author peace
 */
@Data
@Accessors(chain = true)
public class Module {

    /** 包名 */
    private String packageName;
    /** 包路径 */
    private String packagePath;
    /** 名称，UpperCamel 格式，例如：UpperCamel */
    private String name;
    /** 方言名称 */
    private String dialectName;
    /** 属性 */
    private List<ModuleProperty> properties;
    /** 主键属性名 */
    private List<String> primaryKeys;
    /** 注释 */
    private String comment;


    public String getPackagePath() {
        return Objects.toString(packagePath, packageName.replace('.', File.separatorChar));
    }

    public String getComment() {
        return Objects.toString(comment, dialectName);
    }

    private static final List<String> adds = Arrays.asList("id", "creatorId", "createdTime", "modifierId", "modifiedTime");
    private static final List<String> modifies = Arrays.asList("creatorId", "createdTime", "modifierId", "modifiedTime");

    public ModuleProperty getId() {
        return getModuleProperty("id");
    }

    public ModuleProperty getCreatorId() {
        return getModuleProperty("creatorId");
    }

    private ModuleProperty getModuleProperty(String creatorId) {
        return properties.stream().filter(property -> property.getName().equals(creatorId)).findAny().orElse(null);
    }

    public List<ModuleProperty> getNonIdAuditProperties() {
        return properties.stream().filter(property -> !primaryKeys.contains(property.getName())).collect(Collectors.toList());
    }

    public List<ModuleProperty> getAddProperties() {
        return properties.stream().filter(property -> !adds.contains(property.getName())).collect(Collectors.toList());
    }

    public List<ModuleProperty> getModifyProperties() {
        return properties.stream().filter(property -> !modifies.contains(property.getName())).collect(Collectors.toList());
    }

    public List<ModuleProperty> getPrimaryKeyProperties() {
        return properties.stream().filter(property -> primaryKeys.contains(property.getName())).collect(Collectors.toList());
    }

    public String getPrimaryKey() {
        return primaryKeys.get(0);
    }

    public ModuleProperty getPrimaryKeyProperty() {
        return getModuleProperty(getPrimaryKey());
    }

}
