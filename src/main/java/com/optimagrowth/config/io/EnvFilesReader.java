package com.optimagrowth.config.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.optimagrowth.config.util.Env;

public final class EnvFilesReader {

    // Fields made package-private for unit testing
    static final String ENV_FILE_NOT_SET = "An environment variable with name '%s' must be set to a non-blank value";
    static final String ENV_FILE_EMPTY = "The file with path '%s' must contain a non-blank line";
    static final String ENCRYPT_KEY_PROP = "encrypt.key";
    static final String ENCRYPT_KEY_FILE = "ENCRYPT_KEY_FILE";
    static final String SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME_PROP = "spring.cloud.config.server.git.username";
    static final String GIT_USERNAME_FILE = "GIT_USERNAME_FILE";
    static final String SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD_PROP = "spring.cloud.config.server.git.password";
    static final String GIT_PASSWORD_FILE = "GIT_PASSWORD_FILE";

    private EnvFilesReader() {
    }

    public static void read() throws IOException {
        System.setProperty(ENCRYPT_KEY_PROP, readEnvFile(ENCRYPT_KEY_FILE, true));
        System.setProperty(SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME_PROP, readEnvFile(GIT_USERNAME_FILE, true));
        System.setProperty(SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD_PROP, readEnvFile(GIT_PASSWORD_FILE, true));
    }

    // Made package-private for unit testing
    static String readEnvFile(String envName, boolean required) throws IOException {
        String fileName = Env.get(envName);

        if (StringUtils.isBlank(fileName)) {
            if (required) {
                throw new IllegalStateException(String.format(ENV_FILE_NOT_SET, envName));
            }
            return StringUtils.EMPTY;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            Optional<String> optional = reader.lines().filter(StringUtils::isNotBlank).findFirst();
            if (optional.isPresent()) {
                return optional.get();
            }
            if (required) {
                throw new IllegalStateException(String.format(ENV_FILE_EMPTY, fileName));
            }
            return StringUtils.EMPTY;
        }
    }

}
