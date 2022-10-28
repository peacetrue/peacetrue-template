package com.github.peacetrue.template.webflux;

import com.github.peacetrue.io.ConditionalResourcesLoader;
import com.github.peacetrue.io.Resource;
import com.github.peacetrue.template.DirectoryTemplateEngine;
import com.github.peacetrue.template.Options;
import com.github.peacetrue.template.TemplateUtils;
import com.github.peacetrue.template.VelocityTemplateEngine;
import com.github.peacetrue.test.SourcePathUtils;
import com.github.peacetrue.util.ArrayUtils;
import com.github.peacetrue.util.FileUtils;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.peacetrue.test.SourcePathUtils.getCustomAbsolutePath;

/**
 * @author peace
 **/
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpringBootTest {

    private static Options getOptions() {
        return Options.builder()
                .resourceActions(
                        Options.ResourcePathPatterns.builder()
                                .ignore("\\.gitkeep")
                                .build()
                )
                .build();
    }

    private static Map<String, Object> getRepositoryVariables() {
        return ImmutableMap.<String, Object>builder()
                .put("name", "learn-java")
                .put("title", "学习 Java")
                .put("website", "https://peacetrue.github.io")
                .put("group", "com.github.peacetrue.learn.java")
                .put("dependencies", Arrays.asList("com.github.peacetrue:peacetrue-beans"))
                .build();
    }

    private List<String> templateNames = Collections.emptyList();

    @SneakyThrows
    @Order(1)
    @Test
    void template() {
        List<Resource> resources = ConditionalResourcesLoader.DEFAULT.getResources("classpath:webflux");
        Assertions.assertEquals(16, resources.size());
        List<Resource> vmResources = resources.stream().filter(resource -> resource.getPath().endsWith("vm")).collect(Collectors.toList());
        Assertions.assertEquals(5, vmResources.size());

        templateNames = vmResources.stream()
                .filter(resource -> !resource.isDirectory())
                .map(Resource::getPath)
                .map(path -> ArrayUtils.lastSafely(path.split(File.separator)))
                .map(path -> StringUtils.removeEnd(path, ".vm"))
                .collect(Collectors.toList());

        log.info("templateNames: {}", templateNames);
    }

    @Order(10)
    @Test
    void result() throws IOException {
        String targetName = "learn-java";
        String targetPath = SourcePathUtils.getTestResourceAbsolutePath("/" + targetName);
        Path targetPathObject = Paths.get(targetPath);
        if (Files.exists(targetPathObject)) FileUtils.deleteRecursively(targetPathObject);
        Map<String, Map<String, Object>> repository = Collections.singletonMap("repository", getRepositoryVariables());
        DirectoryTemplateEngine templateEngine = VelocityTemplateEngine.buildVelocityDirectoryTemplateEngine();
        templateEngine.evaluate("classpath:gradle", getOptions(), repository, targetPath);
        templateEngine.evaluate("classpath:webflux", getOptions(), repository, targetPath);
        List<Resource> resources = ConditionalResourcesLoader.DEFAULT.getResources("file:" + targetPath);
        Assertions.assertEquals(32, resources.size());
        for (Resource resource : resources) {
            if (resource.isDirectory() || templateNames.stream().noneMatch(item -> resource.getPath().endsWith(item)))
                continue;
            String content = IOUtils.toString(Objects.requireNonNull(resource.getInputStream()));
            Assertions.assertFalse(content.contains("$"), content);
        }
    }

    @Test
    void storeOptions() {
        String resourcePath = getCustomAbsolutePath(false, true, "/webmvc-options.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        TemplateUtils.write(resourcePathObject, getOptions());
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

    @Test
    void storeRepositoryVariables() {
        String resourcePath = getCustomAbsolutePath(false, true, "/webmvc-variables.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        TemplateUtils.write(resourcePathObject, getRepositoryVariables());
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

}
