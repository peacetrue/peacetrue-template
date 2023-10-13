package com.github.peacetrue.template;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * 资源库。
 *
 * @author peace
 **/
@Data
@Accessors(chain = true)
public class Repository {

    public static final Repository LEARN_JAVA = new Repository()
            .setName("learn-java")
            .setTitle("学习 Java")
            .setWebsite("https://peacetrue.github.io")
            .setGroup("com.github.peacetrue.learn")
            .setDomainName("Java");

    /** 名称 */
    private String name;
    /** 标题 */
    private String title;
    /** 网址 */
    private String website;
    /** 组名 */
    private String group;
    /** 领域名称 */
    private String domainName;
    /** 包名 */
    private String packageName;
    /** 基础包路径 */
    private String basePackagePath;

    public String getBasePackagePath() {
        return Objects.toString(basePackagePath, group.replace('.', '/'));
    }
}
