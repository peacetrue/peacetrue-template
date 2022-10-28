package com.github.peacetrue.template;

import com.github.peacetrue.test.SourcePathUtils;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author peace
 **/
class RepositoryTest {

    /** 输出到 examples 中。 */
    @Test
    void output() {
        String pathString = "/../docs/antora/modules/ROOT/examples/learn-java.properties";
        Path path = Paths.get(SourcePathUtils.getProjectAbsolutePath() + pathString);
        TemplateUtils.write(path, ImmutableMap.of("repository", Repository.LEARN_JAVA));
        Assertions.assertTrue(Files.exists(path));
    }
}
