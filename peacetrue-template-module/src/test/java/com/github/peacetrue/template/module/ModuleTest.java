package com.github.peacetrue.template.module;

import com.github.peacetrue.io.ConditionalResourceLoader;
import com.github.peacetrue.io.Resource;
import com.github.peacetrue.io.ResourceUtils;
import com.github.peacetrue.spring.beans.BeanUtils;
import com.github.peacetrue.template.*;
import com.github.peacetrue.test.SourcePathUtils;
import com.github.peacetrue.tplngn.DirectoryTemplateEngine;
import com.github.peacetrue.tplngn.VelocityTemplateEngine;
import com.github.peacetrue.util.MapUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.github.peacetrue.template.TemplateUtils.getOptions;
import static com.github.peacetrue.test.SourcePathUtils.getCustomAbsolutePath;

/**
 * @author peace
 **/
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Disabled
class ModuleTest {

    private static List<String> templateNames = Collections.emptyList();

    @SneakyThrows
    @Order(1)
    @Test
    void template() {
        String targetPath = SourcePathUtils.getCustomAbsolutePath(false, true, "/module");
        List<Resource> resources = ResourceUtils.getDirectoryResources(Paths.get(targetPath));
        Assertions.assertEquals(17, resources.size());
        templateNames = TemplateUtils.findTemplateNames(resources);
        Assertions.assertEquals(6, templateNames.size());
    }

    @Order(10)
    @Test
    void result() throws IOException {
        // find . -name "${repository.basePackagePath}" | xargs rename 's/DomainName/repository.domainName/'
        // find . -name "*.vm" | xargs rename 's/DomainName/repository.domainName/'
        // find . -name "*.vm" | xargs rename 's/domain-name/repository.domainName/'
        // find . -name "*.java.vm" | grep ModuleName | xargs rename 's/ModuleName/module.name/'
        // find . -name "*.sql.vm" | grep module-name | xargs rename 's/module-name/module.name/'
        // find . -name "*\${basePackagePath}" | xargs rename 's/basePackagePath/repository.basePackagePath/'
        // find . -name "*\${clazz.path(\${repository.group})}" | xargs rename 's/clazz.path(.*)/repository.basePackagePath/'
        // find . -name "*\${clazz.path(\${repository.group})}" | xargs rename 's/clazz.path(.*)/repository.basePackagePath/'
        String targetName = "learn-java";
        String targetPath = SourcePathUtils.getTestResourceAbsolutePath("/" + targetName);
        Path targetPathObject = Paths.get(targetPath);
//        if (Files.exists(targetPathObject)) FileUtils.deleteRecursively(targetPathObject);
        log.info("model: \n{}", MapUtils.prettify(LEARN_JAVA_FULL_MAP));
        DirectoryTemplateEngine templateEngine = VelocityTemplateEngine.buildVelocityDirectoryTemplateEngine();
        templateEngine.evaluate("classpath:module-structure/", getOptions(), LEARN_JAVA_FULL_MAP, targetPath);
        templateEngine.evaluate("classpath:module-content/", getOptions(), LEARN_JAVA_FULL_MAP, targetPath);
        List<Resource> resources = ConditionalResourceLoader.DEFAULT.getResources("file:" + targetPath);
//        Assertions.assertEquals(22, resources.size());
    }


    public static final Variables LEARN_JAVA_FULL = new Variables()
            .setRepository(new Repository()
                    .setName("try-crm")
                    .setTitle("尝试 CRM")
                    .setWebsite("https://peacetrue.github.io")
                    .setGroup("com.github.peacetrue.trys.crm")
                    .setDomainName("Crm")
            )
            .setModules(Collections.singletonList(
                    new Module()
                            .setPackageName("com.github.peacetrue.trys.crm.modules.chatrecord")
                            .setName("ChatRecord").setDialectName("聊天记录").setPrimaryKeys(Collections.singletonList("id"))
                            .setProperties(Arrays.asList(
                                    new ModuleProperty("id", "主键", Long.class, false, null, null),
                                    new ModuleProperty("seq", "序号", Integer.class, false, 32, null),
                                    new ModuleProperty("msgId", "消息主键", String.class, false, 255, null),
                                    new ModuleProperty("publickeyVer", "公钥版本", Integer.class, false, null, null),
                                    new ModuleProperty("encryptRandomKey", "加密随机码", String.class, false, 255, null),
                                    new ModuleProperty("encryptChatMsg", "加密聊天消息", String.class, false, 4096, null),
                                    new ModuleProperty("creatorId", "创建者主键", Long.class, false, null, null),
                                    new ModuleProperty("createdTime", "创建时间", LocalDateTime.class, false, null, null)
                            ))
            ));
    public static final Map<String, Object> LEARN_JAVA_FULL_MAP = BeanUtils.getPropertyValues(LEARN_JAVA_FULL);

    @Order(10)
    @Test
    void tryCrm() throws IOException {
        String targetName = "try-crm";
        String targetPath = "/Users/xiayx/Documents/Projects/" + targetName;
        log.info("model: \n{}", MapUtils.prettify(LEARN_JAVA_FULL_MAP));
        DirectoryTemplateEngine templateEngine = VelocityTemplateEngine.buildVelocityDirectoryTemplateEngine();
        templateEngine.evaluate("classpath:module-structure/", getOptions(), LEARN_JAVA_FULL_MAP, targetPath);
        templateEngine.evaluate("classpath:module-content/", getOptions(), LEARN_JAVA_FULL_MAP, targetPath);
        List<Resource> resources = ConditionalResourceLoader.DEFAULT.getResources("file:" + targetPath);
//        Assertions.assertEquals(22, resources.size());
    }

    @Test
    void storeOptions() {
        String resourcePath = getCustomAbsolutePath(false, true, "/module-options.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        TemplateUtils.write(resourcePathObject, getOptions());
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

    @Test
    void storeRepositoryVariables() {
        String resourcePath = getCustomAbsolutePath(false, true, "/module-variables.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        TemplateUtils.write(resourcePathObject, LEARN_JAVA_FULL_MAP);
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

}
