package com.optimagrowth.configserver.io;

import static com.optimagrowth.configserver.io.EnvFilesReader.ENCRYPT_KEY_FILE;
import static com.optimagrowth.configserver.io.EnvFilesReader.ENV_FILE_EMPTY;
import static com.optimagrowth.configserver.io.EnvFilesReader.ENV_FILE_NOT_SET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.optimagrowth.configserver.util.Env;

class EnvFilesReaderTest {

    @Test
    void readEnvFile_EnvFileIsNotSetAndRequiredIsTrue_ThrowIllegalStateException() throws IOException {
        try (var envMockedStatic = mockStatic(Env.class)) {
            // Emulate unset environment variable by returning null
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(null);

            var e = assertThrows(IllegalStateException.class, () -> EnvFilesReader.readEnvFile(ENCRYPT_KEY_FILE, true));

            assertEquals(String.format(ENV_FILE_NOT_SET, ENCRYPT_KEY_FILE), e.getMessage());
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

    @ParameterizedTest
    @ValueSource(ints = { 1, 2 })
    void readEnvFile_EnvFileIsNotEmpty_ReturnFirstNonBlankLine(int numberOfLines) throws IOException {
        var file = createTempFile();
        var strings = generateRandomStrings(numberOfLines);

        write(file, strings);

        try (var envMockedStatic = mockStatic(Env.class)) {
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(file.getPath());

            var content = EnvFilesReader.readEnvFile(ENCRYPT_KEY_FILE, true);

            assertEquals(strings.get(0), content);
        }
    }

    @Test
    void readEnvFile_FirstLineIsBlankAndSecondLineIsNotBlank_ReturnSecondLine() throws IOException {
        var file = createTempFile();
        var strings = generateRandomStrings(1);
        // Make the first line blank
        strings.add(0, StringUtils.EMPTY);

        write(file, strings);

        try (var envMockedStatic = mockStatic(Env.class)) {
            envMockedStatic.when(() -> Env.get(ENCRYPT_KEY_FILE)).thenReturn(file.getPath());

            var content = EnvFilesReader.readEnvFile(ENCRYPT_KEY_FILE, true);

            assertEquals(strings.get(1), content);
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
}
