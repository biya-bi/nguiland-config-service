package com.optimagrowth.config.io;

import static com.optimagrowth.config.io.EnvFilesReader.ENCRYPT_KEY_FILE;
import static com.optimagrowth.config.io.EnvFilesReader.ENCRYPT_KEY_PROP;
import static com.optimagrowth.config.io.EnvFilesReader.ENV_FILE_EMPTY;
import static com.optimagrowth.config.io.EnvFilesReader.ENV_FILE_NOT_SET;
import static com.optimagrowth.config.io.EnvFilesReader.GIT_PRIVATE_KEY_FILE;
import static com.optimagrowth.config.io.EnvFilesReader.SPRING_CLOUD_CONFIG_SERVER_GIT_IGNORE_LOCAL_SSH_SETTINGS;
import static com.optimagrowth.config.io.EnvFilesReader.SPRING_CLOUD_CONFIG_SERVER_GIT_PRIVATE_KEY_PROP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.optimagrowth.config.util.Env;

class EnvFilesReaderTest {

    @Test
    void read_AllEnvironmentVariablePropertiesAreSet_SetSystemProperties() throws IOException {
        var encryptKeyFile = createTempFile();
        var encryptKeyFileContent = generateRandomStrings(1);
        write(encryptKeyFile, encryptKeyFileContent);

        var gitPrivateKeyFile = createTempFile();
        var gitPrivateKeyFileContent = generateRandomStrings(5);
        write(gitPrivateKeyFile, gitPrivateKeyFileContent);

        try (var envMockedStatic = mockStatic(Env.class)) {
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(encryptKeyFile.getPath());
            envMockedStatic.when(() -> Env.get(GIT_PRIVATE_KEY_FILE)).thenReturn(gitPrivateKeyFile.getPath());

            EnvFilesReader.read();

            assertEquals(join(encryptKeyFileContent), System.getProperty(ENCRYPT_KEY_PROP));
            assertEquals(String.valueOf(true),
                    System.getProperty(SPRING_CLOUD_CONFIG_SERVER_GIT_IGNORE_LOCAL_SSH_SETTINGS));
            assertEquals(join(gitPrivateKeyFileContent), System.getProperty(SPRING_CLOUD_CONFIG_SERVER_GIT_PRIVATE_KEY_PROP));
        }
    }

    @Test
    void readEnvFile_EnvFileIsNotSetAndRequiredIsTrue_ThrowIllegalStateException() {
        try (var envMockedStatic = mockStatic(Env.class)) {
            // Emulate unset environment variable by returning null
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(null);

            var e = assertThrows(IllegalStateException.class, () -> EnvFilesReader.readEnvFile(ENCRYPT_KEY_FILE, true));

            assertEquals(String.format(ENV_FILE_NOT_SET, ENCRYPT_KEY_FILE), e.getMessage());
        }
    }

    @Test
    void readEnvFile_EnvFileOnlyContainsBlankLines_ThrowIllegalStateException() throws IOException {
        var file = createTempFile();
        var strings = new ArrayList<String>();
        // Add only blank lines
        strings.add(StringUtils.EMPTY);
        strings.add(System.lineSeparator());
        strings.add("    ");

        write(file, strings);

        try (var envMockedStatic = mockStatic(Env.class)) {
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(file.getPath());

            var e = assertThrows(IllegalStateException.class, () -> EnvFilesReader.readEnvFile(ENCRYPT_KEY_FILE, true));

            assertEquals(String.format(ENV_FILE_EMPTY, file.getPath()), e.getMessage());
        }
    }

    @Test
    void readEnvFile_EnvFileIsNotSetAndRequiredIsFalse_ReturnEmptyString() throws IOException {
        try (var envMockedStatic = mockStatic(Env.class)) {
            // Emulate unset environment variable by returning null
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(null);

            var content = EnvFilesReader.readEnvFile(ENCRYPT_KEY_FILE, false);

            assertEquals(StringUtils.EMPTY, content);
        }
    }

    @Test
    void readEnvFile_EnvFileIsNotEmpty_ReturnContent() throws IOException {
        var file = createTempFile();
        var strings = generateRandomStrings(2);

        write(file, strings);

        try (var envMockedStatic = mockStatic(Env.class)) {
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(file.getPath());

            var content = EnvFilesReader.readEnvFile(ENCRYPT_KEY_FILE, true);

            assertEquals(join(strings), content);
        }
    }

    @Test
    void readEnvFile_FirstLineIsBlankAndSecondLineIsNotBlank_ReturnContent() throws IOException {
        var file = createTempFile();
        var strings = generateRandomStrings(1);
        // Make the first line blank
        strings.add(0, StringUtils.EMPTY);

        write(file, strings);

        try (var envMockedStatic = mockStatic(Env.class)) {
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(file.getPath());

            var content = EnvFilesReader.readEnvFile(ENCRYPT_KEY_FILE, true);

            assertEquals(join(strings), content);
        }
    }

    @Test
    void readEnvFile_EnvFileIsEmptyAndRequiredIsTrue_ThrowIllegalStateException() throws IOException {
        var file = createTempFile();

        try (var envMockedStatic = mockStatic(Env.class)) {
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(file.getPath());

            var e = assertThrows(IllegalStateException.class, () -> EnvFilesReader.readEnvFile(ENCRYPT_KEY_FILE, true));

            assertEquals(String.format(ENV_FILE_EMPTY, file.getPath()), e.getMessage());
        }
    }

    @Test
    void readEnvFile_EnvFileIsEmptyAndRequiredIsFalse_ReturnEmptyString() throws IOException {
        var file = createTempFile();

        try (var envMockedStatic = mockStatic(Env.class)) {
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(file.getPath());

            var content = EnvFilesReader.readEnvFile(ENCRYPT_KEY_FILE, false);

            assertEquals(StringUtils.EMPTY, content);
        }
    }

    private File createTempFile() throws IOException {
        var file = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".tmp");
        file.deleteOnExit();
        return file;
    }

    private void write(File file, List<String> strings) throws IOException {
        try (var writer = new BufferedWriter(new FileWriter(file, true))) {
            for (var string : strings) {
                writer.append(string);
                writer.newLine();
            }
            writer.flush();
        }
    }

    private List<String> generateRandomStrings(int count) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            strings.add(RandomStringUtils.random(10, true, true));
        }
        return strings;
    }

    private String join(List<String> lines) {
        return lines.stream().collect(Collectors.joining(System.lineSeparator()));
    }
}
