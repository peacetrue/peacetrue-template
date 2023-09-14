package com.github.peacetrue.template;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模型
 *
 * @author peace
 */
@Data
@Accessors(chain = true)
public class Module {

    /** 名称，UpperCamel 格式，例如：UpperCamel */
    private String name;
    /** 本国 name */
    private String nationalName;
    /** 属性 */
    private List<ModuleProperty> properties;
    /** 主键属性名 */
    private List<String> primaryKeys;
    /** 注释 */
    private String comment;

    private static final List<String> adds = Arrays.asList("id", "creatorId", "createdTime", "modifierId", "modifiedTime");
    private static final List<String> modifies = Arrays.asList("creatorId", "createdTime", "modifierId", "modifiedTime");

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
        return properties.stream().filter(property -> property.getName().equals(getPrimaryKey())).findAny().orElse(null);
    }

}
