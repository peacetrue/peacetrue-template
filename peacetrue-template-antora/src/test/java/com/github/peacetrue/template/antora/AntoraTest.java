package com.github.peacetrue.template.antora;

import com.github.peacetrue.io.ConditionalResourceLoader;
import com.github.peacetrue.io.Resource;
import com.github.peacetrue.io.ResourceUtils;
import com.github.peacetrue.tplngn.DirectoryTemplateEngine;
import com.github.peacetrue.template.TemplateUtils;
import com.github.peacetrue.template.Variables;
import com.github.peacetrue.tplngn.VelocityTemplateEngine;
import com.github.peacetrue.test.SourcePathUtils;
import com.github.peacetrue.util.FileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.github.peacetrue.template.TemplateUtils.getOptions;
import static com.github.peacetrue.test.SourcePathUtils.getCustomAbsolutePath;

/**
 * templates 资源库。
 *
 * @author peace
 **/
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AntoraTest {
    private static List<String> templateNames = Collections.emptyList();

    @SneakyThrows
    @Test
    @Order(10)
    void template() {
        String targetPath = SourcePathUtils.getCustomAbsolutePath(false, true, "/antora");
        List<Resource> resources = ResourceUtils.getDirectoryResources(Paths.get(targetPath));
        Assertions.assertEquals(27, resources.size());

        templateNames = TemplateUtils.findTemplateNames(resources);
        Assertions.assertEquals(4, templateNames.size());
    }

    @Test
    @Order(20)
    void result() throws IOException {
        String targetName = "learn-java";
        String targetPath = SourcePathUtils.getTestResourceAbsolutePath("/" + targetName);
        Path targetPathObject = Paths.get(targetPath);
        if (Files.exists(targetPathObject)) FileUtils.deleteRecursively(targetPathObject);

        DirectoryTemplateEngine templateEngine = VelocityTemplateEngine.buildVelocityDirectoryTemplateEngine();
        templateEngine.evaluate("classpath:antora/", getOptions(), Variables.LEARN_JAVA_FULL_MAP, targetPath);
        List<Resource> resources = ConditionalResourceLoader.DEFAULT.getResources("file:" + targetPath);
        Assertions.assertEquals(22, resources.size());

        for (Resource resource : resources) {
            if (resource.isDirectory() || templateNames.stream().noneMatch(item -> resource.getPath().endsWith(item)))
                continue;
            String content = IOUtils.toString(Objects.requireNonNull(resource.getInputStream()));
            Assertions.assertFalse(content.contains("$"), content);
        }
    }

    @Test
    void storeOptions() {
        String resourcePath = getCustomAbsolutePath(false, true, "/antora-options.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        TemplateUtils.write(resourcePathObject, getOptions());
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

    @Test
    void storeRepositoryVariables() {
        String resourcePath = getCustomAbsolutePath(false, true, "/antora-variables.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        TemplateUtils.write(resourcePathObject, Variables.LEARN_JAVA_MAP);
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

}
