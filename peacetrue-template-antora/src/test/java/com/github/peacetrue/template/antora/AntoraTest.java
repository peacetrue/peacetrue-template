package com.github.peacetrue.template.antora;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import com.github.peacetrue.io.ConditionalResourcesLoader;
import com.github.peacetrue.io.Resource;
import com.github.peacetrue.template.DirectoryTemplateEngine;
import com.github.peacetrue.template.Options;
import com.github.peacetrue.template.VelocityTemplateEngine;
import com.github.peacetrue.test.SourcePathUtils;
import com.github.peacetrue.util.ArrayUtils;
import com.github.peacetrue.util.FileUtils;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.peacetrue.test.SourcePathUtils.getCustomAbsolutePath;

/**
 * templates 资源库。
 *
 * @author peace
 **/
@Slf4j
class AntoraTest {
    private final int resourceCount = 24;
    private List<String> vmNames = Collections.emptyList();

    @SneakyThrows
    @Test
    void template() {
        List<Resource> resources = ConditionalResourcesLoader.DEFAULT.getResources("classpath:antora");
        Assertions.assertEquals(resourceCount, resources.size());
        List<Resource> vmResources = resources.stream().filter(resource -> resource.getPath().endsWith("vm")).collect(Collectors.toList());
        Assertions.assertEquals(2, vmResources.size());

        vmNames = vmResources.stream()
                .filter(resource -> !resource.isDirectory())
                .map(Resource::getPath)
                .map(path -> ArrayUtils.lastSafely(path.split(File.separator)))
                .map(path -> StringUtils.removeEnd(path, ".vm"))
                .collect(Collectors.toList());

        log.info("vmNames: {}", vmNames);
    }


    @Test
    void result() throws IOException {
        String targetName = "learn-java";
        String targetPath = SourcePathUtils.getTestResourceAbsolutePath("/" + targetName);
        Path targetPathObject = Paths.get(targetPath);
        if (Files.exists(targetPathObject)) FileUtils.deleteRecursively(targetPathObject);
        Map<String, Map<String, Object>> repository = Collections.singletonMap("repository", getRepositoryVariables());
        DirectoryTemplateEngine templateEngine = VelocityTemplateEngine.buildVelocityDirectoryTemplateEngine();
        templateEngine.evaluate("classpath:antora", getOptions(), repository, targetPath);
        List<Resource> resources = ConditionalResourcesLoader.DEFAULT.getResources("file:" + targetPath);
        Assertions.assertEquals(resourceCount, resources.size());
        for (Resource resource : resources) {
            if (resource.isDirectory() || vmNames.stream().noneMatch(item -> resource.getPath().endsWith(item))) continue;
            String content = IOUtils.toString(Objects.requireNonNull(resource.getInputStream()));
            Assertions.assertFalse(content.contains("$"), content);
        }
    }

    private static Options getOptions() {
        return Options.builder()
                .resourceActions(
                        Options.ResourcePathPatterns.builder()
//                                .ignore("Placeholder\\.txt")
                                .build()
                )
                .build();
    }

    private static Map<String, Object> getRepositoryVariables() {
        return ImmutableMap.<String, Object>builder()
                .put("name", "learn-java")
                .put("title", "学习 Java")
                .put("description", "记录学习 Java 的过程。")
                .put("website", "https://peacetrue.github.io")
                .build();
    }

    @Test
    void storeOptions() {
        String resourcePath = getCustomAbsolutePath(false, true, "/antora-options.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        write(resourcePathObject, getOptions());
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

    @Test
    void storeRepositoryVariables() {
        String resourcePath = getCustomAbsolutePath(false, true, "/antora-variables.properties");
        Path resourcePathObject = Paths.get(resourcePath);
        write(resourcePathObject, getRepositoryVariables());
        Assertions.assertTrue(Files.exists(resourcePathObject));
    }

    private static final JavaPropsSchema SCHEMA = JavaPropsSchema.emptySchema()
            .withWriteIndexUsingMarkers(true)
            .withFirstArrayOffset(0);
    private static final JavaPropsMapper javaPropsMapper = new JavaPropsMapper();

    @SneakyThrows
    private static void write(Path path, Object value) {
        javaPropsMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String text = javaPropsMapper.writer(SCHEMA).writeValueAsString(value);
        text = StringEscapeUtils.unescapeJava(text);
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
    }

}
