package com.github.peacetrue.template;

import com.github.peacetrue.spring.beans.BeanUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author peace
 **/
@Data
@Accessors(chain = true)
public class Variables implements Cloneable {

    public static final Variables LEARN_JAVA = new Variables()
            .setRepository(Repository.LEARN_JAVA);
    public static final Map<String, Object> LEARN_JAVA_MAP = BeanUtils.getPropertyValues(LEARN_JAVA);

    public static final Variables LEARN_JAVA_FULL = LEARN_JAVA.clone()
            .setDependencies(Arrays.asList(
                    "com.github.peacetrue.dictionary:peacetrue-dictionary-service-impl",
                    "com.github.peacetrue.dictionary:peacetrue-dictionary-service-controller"
            ))
            .setModules(Collections.singletonList(
                    new Module().setName("User").setDialectName("用户").setPrimaryKeys(Collections.singletonList("id"))
                            .setProperties(Arrays.asList(
                                    new ModuleProperty("id", "主键", Long.class, false, null, null),
                                    new ModuleProperty("username", "用户名", String.class, false, 32, null),
                                    new ModuleProperty("password", "密码", String.class, false, 255, null),
                                    new ModuleProperty("creatorId", "创建者主键", Long.class, false, null, null),
                                    new ModuleProperty("createdTime", "创建时间", LocalDateTime.class, false, null, null),
                                    new ModuleProperty("modifierId", "修改者主键", Long.class, false, null, null),
                                    new ModuleProperty("modifiedTime", "修改时间", LocalDateTime.class, false, null, null)
                            ))
            ));
    public static final Map<String, Object> LEARN_JAVA_FULL_MAP = BeanUtils.getPropertyValues(LEARN_JAVA_FULL);

    private Repository repository;
    private List<Object> dependencies;
    private List<Module> modules;

    @Override
    public Variables clone() {
        try {
            return (Variables) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    public Module getModule() {
        return modules == null ? null : modules.get(0);
    }
}
