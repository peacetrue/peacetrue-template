package com.github.peacetrue.template;

import com.github.peacetrue.spring.beans.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author peace
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Variables {

    public static final Variables LEARN_JAVA = new Variables(Repository.LEARN_JAVA, Arrays.asList(
            "com.github.peacetrue.dictionary:peacetrue-dictionary-service-impl",
            "com.github.peacetrue.dictionary:peacetrue-dictionary-service-controller"
    ));
    public static final Map<String, Object> LEARN_JAVA_MAP = BeanUtils.getPropertyValues(LEARN_JAVA);


    private Repository repository;
    private List<Object> dependencies;
}
