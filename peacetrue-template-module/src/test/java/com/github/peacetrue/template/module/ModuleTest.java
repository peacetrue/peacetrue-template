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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.github.peacetrue.template.TemplateUtils.getOptions;
import static com.github.peacetrue.test.SourcePathUtils.getCustomAbsolutePath;

/**
 * //TODO 模板不需要有太多方法，用于集成到项目中
 *
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
        // find . -name "\${repository.basePackagePath}" | xargs rename 's/repository.basePackagePath/repository.packagePath/'
        // find . -name "\${repository.packagePath}" | xargs rename 's/repository.packagePath/module.packagePath/'
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


    public static Variables LEARN_JAVA_FULL;

    static {
        Module CONFIGURATION = new Module()
                .setPackageName("com.github.peacetrue.trys.crm.modules.configuration")
                .setName("Configuration").setDialectName("配置").setPrimaryKeys(Collections.singletonList("id"))
                .setProperties(Arrays.asList(
                        new ModuleProperty("id", "主键", Long.class, false, null, null),
                        new ModuleProperty("code", "编码", String.class, false, 32, null),
                        new ModuleProperty("name", "名称", String.class, false, 255, null),
                        new ModuleProperty("value", "值", String.class, false, 4096, null),
                        new ModuleProperty("remark", "备注", String.class, false, 255, null),
                        new ModuleProperty("creatorId", "创建者主键", Long.class, false, null, null),
                        new ModuleProperty("createdTime", "创建时间", LocalDateTime.class, false, null, null),
                        new ModuleProperty("modifierId", "修改者主键", Long.class, false, null, null),
                        new ModuleProperty("modifiedTime", "修改时间", LocalDateTime.class, false, null, null)
                ));
        Module CHAT_RECORD = new Module()
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
                ));
        Module PLAIN_CHAT_RECORD = new Module()
                .setPackageName("com.github.peacetrue.trys.crm.modules.plainchatrecord")
                .setName("PlainChatRecord").setDialectName("明文聊天记录").setPrimaryKeys(Collections.singletonList("id"))
                .setProperties(Arrays.asList(
                        new ModuleProperty("id", "主键", Long.class, false, null, null),
                        new ModuleProperty("msgId", "消息id", String.class, false, 64, null),
                        new ModuleProperty("action", "消息动作", String.class, false, 16, null),
                        new ModuleProperty("from", "消息发送方id", String.class, false, 64, null),
                        new ModuleProperty("toList", "消息接收方列表", String.class, false, 1024, null),
                        new ModuleProperty("roomId", "群聊消息的群id", String.class, true, 255, null),
                        new ModuleProperty("msgTime", "消息发送时间戳(ms)", Long.class, false, null, null),
                        new ModuleProperty("msgType", "消息类型", String.class, false, 32, null),
                        new ModuleProperty("content", "消息内容", String.class, false, 4096, null),
                        new ModuleProperty("creatorId", "创建者主键", Long.class, false, null, null),
                        new ModuleProperty("createdTime", "创建时间", LocalDateTime.class, false, null, null)
                ));
        Module SYNC_USER = new Module()
                .setPackageName("com.github.peacetrue.trys.crm.modules.syncuser")
                .setName("SyncUser").setDialectName("微信同步用户").setPrimaryKeys(Collections.singletonList("id"))
                .setProperties(Arrays.asList(
                        new ModuleProperty("id", "主键", Long.class, false, null, null),
                        new ModuleProperty("userId", "微信用户标识", String.class, false, 64, null),
                        new ModuleProperty("name", "昵称", String.class, false, 255, null),
                        new ModuleProperty("creatorId", "创建者主键", Long.class, false, null, null),
                        new ModuleProperty("createdTime", "创建时间", LocalDateTime.class, false, null, null),
                        new ModuleProperty("modifierId", "修改者主键", Long.class, false, null, null),
                        new ModuleProperty("modifiedTime", "修改时间", LocalDateTime.class, false, null, null)
                ));
        Module CHAT_ROOM = new Module()
                .setPackageName("com.github.peacetrue.trys.crm.modules.chatroom")
                .setName("ChatRoom").setDialectName("聊天室").setPrimaryKeys(Collections.singletonList("id"))
                .setProperties(Arrays.asList(
                        new ModuleProperty("id", "主键", Long.class, false, null, null),
                        new ModuleProperty("wechat_room_id", "微信聊天室标识", String.class, false, 64, null),
                        new ModuleProperty("type", "类型", String.class, false, 64, "类型：single=单聊、group=群聊"),
                        new ModuleProperty("external", "是否外部", Boolean.class, false, null, null),
                        new ModuleProperty("name", "名称", String.class, false, 255, null),
                        new ModuleProperty("creatorId", "创建者主键", Long.class, false, null, null),
                        new ModuleProperty("createdTime", "创建时间", LocalDateTime.class, false, null, null),
                        new ModuleProperty("modifierId", "修改者主键", Long.class, false, null, null),
                        new ModuleProperty("modifiedTime", "修改时间", LocalDateTime.class, false, null, null)
                ));
        Module ATTACHMENT = new Module()
                .setPackageName("com.github.peacetrue.attachment")
                .setName("Attachment").setDialectName("附件").setPrimaryKeys(Collections.singletonList("id"))
                .setProperties(Arrays.asList(
                        new ModuleProperty("id", "主键", Long.class, false, null, null),
                        new ModuleProperty("name", "名称", String.class, false, 255, null),
                        new ModuleProperty("path", "路径", String.class, false, 255, "类型：single=单聊、group=群聊"),
                        new ModuleProperty("sizes", "大小（字节）", Long.class, false, null, null),
                        new ModuleProperty("stateId", "状态编码", Integer.class, false, null, "状态编码. 1、临时，2、生效、3、删除"),
                        new ModuleProperty("remark", "备注", String.class, false, 255, null),
                        new ModuleProperty("creatorId", "创建者主键", Long.class, false, null, null),
                        new ModuleProperty("createdTime", "创建时间", LocalDateTime.class, false, null, null)
                ));
        Module ADMIN = new Module()
                .setPackageName("com.github.peacetrue.admin")
                .setName("Admin").setDialectName("后台用户").setPrimaryKeys(Collections.singletonList("id"))
                .setProperties(Arrays.asList(
                        new ModuleProperty("id", "主键", Long.class, false, null, null),
                        new ModuleProperty("username", "用户名", String.class, false, 32, null),
                        new ModuleProperty("password", "密码", String.class, false, 255, null),
                        new ModuleProperty("remark", "备注", String.class, false, 255, null),
                        new ModuleProperty("creatorId", "创建者主键", Long.class, false, null, null),
                        new ModuleProperty("createdTime", "创建时间", LocalDateTime.class, false, null, null),
                        new ModuleProperty("modifierId", "修改者主键", Long.class, false, null, null),
                        new ModuleProperty("modifiedTime", "修改时间", LocalDateTime.class, false, null, null)
                ));

        Module PERSON = new Module()
                .setPackageName("com.github.peacetrue.modules.person")
                .setName("Person").setDialectName("人").setPrimaryKeys(Collections.singletonList("id"))
                .setProperties(Arrays.asList(
                        new ModuleProperty("id", "主键", Long.class, false, null, null),
                        new ModuleProperty("logo", "头像", String.class, false, 255, null),
                        new ModuleProperty("name", "姓名", String.class, false, 16, null),
                        new ModuleProperty("sex", "性别", Byte.class, false, null, "1.男、2.女、3.不确定"),
                        new ModuleProperty("birthday", "生日", Instant.class, false, null, null),
                        new ModuleProperty("deathday", "忌日", Instant.class, false, null, null),
                        new ModuleProperty("remark", "备注", String.class, false, 255, null),
                        new ModuleProperty("creatorId", "创建者主键", Long.class, false, null, null),
                        new ModuleProperty("createdTime", "创建时间", Instant.class, false, null, null),
                        new ModuleProperty("modifierId", "修改者主键", Long.class, false, null, null),
                        new ModuleProperty("modifiedTime", "修改时间", Instant.class, false, null, null)
                ));

        LEARN_JAVA_FULL = new Variables()
                .setRepository(new Repository()
                        .setName("try-crm")
                        .setTitle("尝试 CRM")
                        .setWebsite("https://peacetrue.github.io")
                        .setGroup("com.github.peacetrue.trys.crm")
                        .setDomainName("Crm")
                )
                .setModules(Collections.singletonList(
                        CHAT_ROOM
                ));

        LEARN_JAVA_FULL = new Variables()
                .setRepository(new Repository()
                        .setName("peacetrue-attachment")
                        .setTitle("附件")
                        .setWebsite("https://peacetrue.github.io")
                        .setGroup("com.github.peacetrue.attachment")
                        .setDomainName("Attachment")
                )
                .setModules(Collections.singletonList(
                        ATTACHMENT
                ));
        LEARN_JAVA_FULL = new Variables()
                .setRepository(new Repository()
                        .setName("peacetrue-admin")
                        .setTitle("后台用户")
                        .setWebsite("https://peacetrue.github.io")
                        .setGroup("com.github.peacetrue.admin")
                        .setDomainName("Admin")
                )
                .setModules(Collections.singletonList(
                        ADMIN
                ));
        LEARN_JAVA_FULL = new Variables()
                .setRepository(new Repository()
                        .setName("peacetrue-modules-person")
                        .setTitle("人")
                        .setWebsite("https://peacetrue.github.io")
                        .setGroup("com.github.peacetrue.modules")
                        .setDomainName("Person")
                )
                .setModules(Collections.singletonList(
                        PERSON
                ));
    }

    public static final Map<String, Object> LEARN_JAVA_FULL_MAP = BeanUtils.getPropertyValues(LEARN_JAVA_FULL);

    @Order(10)
    @Test
    void tryCrm() throws IOException {
        String targetPath = "/Users/xiayx/Documents/Projects/" + LEARN_JAVA_FULL.getRepository().getName();
        log.info("model: \n{}", MapUtils.prettify(LEARN_JAVA_FULL_MAP));
        DirectoryTemplateEngine templateEngine = VelocityTemplateEngine.buildVelocityDirectoryTemplateEngine();
        templateEngine.evaluate("classpath:antora/", getOptions(), LEARN_JAVA_FULL_MAP, targetPath);
        templateEngine.evaluate("classpath:gradle/", getOptions(), LEARN_JAVA_FULL_MAP, targetPath);
//        templateEngine.evaluate("classpath:webmvc/", getOptions(), LEARN_JAVA_FULL_MAP, targetPath);
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
