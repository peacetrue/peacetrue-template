package com.github.peacetrue.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import com.github.peacetrue.io.Resource;
import com.github.peacetrue.util.ArrayUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author peace
 **/
public class TemplateUtils {
    private static final JavaPropsSchema SCHEMA = JavaPropsSchema.emptySchema()
            .withWriteIndexUsingMarkers(true)
            .withFirstArrayOffset(0);
    private static final JavaPropsMapper javaPropsMapper = new JavaPropsMapper();

    private TemplateUtils() {
    }

    @SneakyThrows
    public static void write(Path path, Object value) {
        javaPropsMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String text = javaPropsMapper.writer(SCHEMA).writeValueAsString(value);
        text = StringEscapeUtils.unescapeJava(text);
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
    }

    public static Options getOptions() {
        return Options.builder()
                .resourceActions(
                        Options.ResourcePathPatterns.builder()
                                .ignore("\\.gitkeep")
                                .build()
                )
                .build();
    }

    public static List<String> findTemplateNames(List<Resource> resources) {
        return resources.stream()
                .filter(resource -> !resource.isDirectory())
                .map(Resource::getPath)
                .filter(resource -> resource.endsWith("vm"))
                .map(path -> ArrayUtils.lastSafely(path.split(File.separator)))
                .map(path -> StringUtils.removeEnd(path, ".vm"))
                .collect(Collectors.toList());
    }
}
