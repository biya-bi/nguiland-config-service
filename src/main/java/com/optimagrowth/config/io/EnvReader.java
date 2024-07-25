package com.optimagrowth.config.io;

import java.io.IOException;

import org.rainbow.io.EnvFileReader;

public final class EnvReader {

    // Fields made package-private for unit testing
    static final String ENCRYPT_KEY_PROP = "encrypt.key";
    static final String ENCRYPT_KEY_FILE = "ENCRYPT_KEY_FILE";
    static final String SPRING_CLOUD_CONFIG_SERVER_GIT_IGNORE_LOCAL_SSH_SETTINGS = "spring.cloud.config.server.git.ignoreLocalSshSettings";
    static final String SPRING_CLOUD_CONFIG_SERVER_GIT_PRIVATE_KEY_PROP = "spring.cloud.config.server.git.privateKey";
    static final String GIT_PRIVATE_KEY_FILE = "GIT_PRIVATE_KEY_FILE";

    private EnvReader() {
    }

    public static void read() throws IOException {
        System.setProperty(ENCRYPT_KEY_PROP, EnvFileReader.read(ENCRYPT_KEY_FILE));
        System.setProperty(SPRING_CLOUD_CONFIG_SERVER_GIT_IGNORE_LOCAL_SSH_SETTINGS, String.valueOf(true));
        System.setProperty(SPRING_CLOUD_CONFIG_SERVER_GIT_PRIVATE_KEY_PROP, EnvFileReader.read(GIT_PRIVATE_KEY_FILE));
    }

}
