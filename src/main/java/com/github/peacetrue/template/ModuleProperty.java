package com.github.peacetrue.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 模型属性。
 *
 * @author peace
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleProperty {

    /** 名称，lowerCamel格式，例如：personName */
    private String name;
    /** 本国 name */
    private String nationalName;
    /** 类型 */
    private Class<?> type;
    /** 可否为空 */
    private Boolean nullable;
    /** 属性长度 */
    private Integer size;
    /** 注释 */
    private String comment;

    public String getComment() {
        return Objects.toString(comment, nationalName);
    }
}
