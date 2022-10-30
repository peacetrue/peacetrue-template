package com.github.peacetrue.template.gradle;

import com.github.peacetrue.io.ConditionalResourcesLoader;
import com.github.peacetrue.io.Resource;
import com.github.peacetrue.io.ResourcesUtils;
import com.github.peacetrue.template.DirectoryTemplateEngine;
import com.github.peacetrue.template.TemplateUtils;
import com.github.peacetrue.template.VelocityTemplateEngine;
import com.github.peacetrue.test.SourcePathUtils;
import com.github.peacetrue.util.FileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.github.peacetrue.template.Variables.LEARN_JAVA_MAP;
import static com.github.peacetrue.template.TemplateUtils.getOptions;
import static com.github.peacetrue.test.SourcePathUtils.getCustomAbsolutePath;

/**
 * @author peace
 **/
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GradleTest {

    private static List<String> templateNames = Collections.emptyList();

    @SneakyThrows
    @Order(10)
    @Test
    void template() {
        String targetPath = SourcePathUtils.getCustomAbsolutePath(false, true, "/gradle");
        List<Resource> resources = ResourcesUtils.getDirectoryResources(Paths.get(targetPath));
        Assertions.assertEquals(23, resources.size());
        templateNames = TemplateUtils.findTemplateNames(resources);
        Assertions.assertEquals(4, templateNames.size());
    }

    @SneakyThrows
    @Order(20)
    @Test
    void result() {
        String targetName = "learn-java";
        String targetPath = SourcePathUtils.getTestResourceAbsolutePath("/" + targetName);
        Path targetPathObject = Paths.get(targetPath);
        if (Files.exists(targetPathObject)) FileUtils.deleteRecursively(targetPathObject);

        DirectoryTemplateEngine templateEngine = VelocityTemplateEngine.buildVelocityDirectoryTemplateEngine();
        templateEngine.evaluate("classpath:gradle", getOptions(), LEARN_JAVA_MAP, targetPath);

        List<Resource> resources = ConditionalResourcesLoader.DEFAULT.getResources("file:" + targetPath);
        Assertions.assertEquals(26, resources.size());
        for (Resource resource : resources) {
            if (resource.isDirectory() || templateNames.stream().noneMatch(item -> resource.getPath().endsWith(item)))
                continue;
            String content = IOUtils.toString(Objects.requireNonNull(resource.getInputStream()));
            Assertions.assertFalse(content.contains("$"), content);
        }
    }

    @Test
    void storeOptions() {
        String resourcePath = getCustomAbsolutePath(false, true, "/gradle-options.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        TemplateUtils.write(resourcePathObject, getOptions());
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

    @Test
    void storeRepositoryVariables() {
        String resourcePath = getCustomAbsolutePath(false, true, "/gradle-variables.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        TemplateUtils.write(resourcePathObject, LEARN_JAVA_MAP);
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

}
