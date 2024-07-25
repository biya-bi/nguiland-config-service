package com.optimagrowth.config.io;

import static com.optimagrowth.config.io.EnvReader.ENCRYPT_KEY_FILE;
import static com.optimagrowth.config.io.EnvReader.ENCRYPT_KEY_PROP;
import static com.optimagrowth.config.io.EnvReader.GIT_PRIVATE_KEY_FILE;
import static com.optimagrowth.config.io.EnvReader.SPRING_CLOUD_CONFIG_SERVER_GIT_IGNORE_LOCAL_SSH_SETTINGS;
import static com.optimagrowth.config.io.EnvReader.SPRING_CLOUD_CONFIG_SERVER_GIT_PRIVATE_KEY_PROP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.rainbow.io.EnvFileReader;

class EnvReaderTest {

    @Test
    void read_AllEnvironmentVariablePropertiesAreSet_SetSystemProperties() throws IOException {
        var encryptKey = RandomStringUtils.random(10);
        var gitPrivateKey = RandomStringUtils.random(10);

        try (var envFileReaderMockedStatic = mockStatic(EnvFileReader.class)) {
            envFileReaderMockedStatic.when(() -> EnvFileReader.read(ENCRYPT_KEY_FILE)).thenReturn(encryptKey);
            envFileReaderMockedStatic.when(() -> EnvFileReader.read(GIT_PRIVATE_KEY_FILE))
                    .thenReturn(gitPrivateKey);

            EnvReader.read();

            assertEquals(encryptKey, System.getProperty(ENCRYPT_KEY_PROP));
            assertEquals(String.valueOf(true),
                    System.getProperty(SPRING_CLOUD_CONFIG_SERVER_GIT_IGNORE_LOCAL_SSH_SETTINGS));
            assertEquals(gitPrivateKey,
                    System.getProperty(SPRING_CLOUD_CONFIG_SERVER_GIT_PRIVATE_KEY_PROP));
        }
    }

}
