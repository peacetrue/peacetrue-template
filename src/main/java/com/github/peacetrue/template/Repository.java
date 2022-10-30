package com.github.peacetrue.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源库。
 *
 * @author peace
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Repository {

    public static final Repository LEARN_JAVA = new Repository(
            "learn-java",
            "学习 Java",
            "https://peacetrue.github.io",
            "com.github.peacetrue.learn"
    );

    /** 名称 */
    private String name;
    /** 标题 */
    private String title;
    /** 网址 */
    private String website;
    /** 组名 */
    private String group;

}
