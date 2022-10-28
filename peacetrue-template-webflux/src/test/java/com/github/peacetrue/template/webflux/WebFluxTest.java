package com.github.peacetrue.template.webflux;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.github.peacetrue.template.Repository.LEARN_JAVA_ROOT;
import static com.github.peacetrue.template.TemplateUtils.getOptions;
import static com.github.peacetrue.test.SourcePathUtils.getCustomAbsolutePath;

/**
 * @author peace
 **/
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WebFluxTest {

    private List<String> templateNames = Collections.emptyList();

    @SneakyThrows
    @Order(1)
    @Test
    void template() {
        String targetPath = SourcePathUtils.getCustomAbsolutePath(false, true, "/webflux");
        List<Resource> resources = ResourcesUtils.getDirectoryResources(Paths.get(targetPath));
        Assertions.assertEquals(16, resources.size());
        templateNames = TemplateUtils.findTemplateNames(resources);
        Assertions.assertEquals(6, templateNames.size());
    }

    @Order(10)
    @Test
    void result() throws IOException {
        String targetName = "learn-java";
        String targetPath = SourcePathUtils.getTestResourceAbsolutePath("/" + targetName);
        Path targetPathObject = Paths.get(targetPath);
        if (Files.exists(targetPathObject)) FileUtils.deleteRecursively(targetPathObject);

        DirectoryTemplateEngine templateEngine = VelocityTemplateEngine.buildVelocityDirectoryTemplateEngine();
        templateEngine.evaluate("classpath:webflux", getOptions(), LEARN_JAVA_ROOT, targetPath);
        List<Resource> resources = ConditionalResourcesLoader.DEFAULT.getResources("file:" + targetPath);
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
        String resourcePath = getCustomAbsolutePath(false, true, "/webflux-options.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        TemplateUtils.write(resourcePathObject, getOptions());
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

    @Test
    void storeRepositoryVariables() {
        String resourcePath = getCustomAbsolutePath(false, true, "/webflux-variables.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        TemplateUtils.write(resourcePathObject, LEARN_JAVA_ROOT);
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

}
