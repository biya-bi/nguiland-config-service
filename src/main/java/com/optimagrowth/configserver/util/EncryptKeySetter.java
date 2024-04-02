package com.optimagrowth.configserver.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public final class EncryptKeySetter {

    private static final String ENCRYPT_KEY_PROP = "encrypt.key";
    private static final String ENCRYPT_KEY_FILE = "ENCRYPT_KEY_FILE";
    private static final String ENCRYPT_KEY_FILE_NOT_SET = "An environment variable with name '%s' indicating the path of a file containing the encrypt key must be set to a non-blank value";
    private static final String ENCRYPT_KEY_FILE_EMPTY = "The file with path '%s' must contain a non-blank line representing an encrypt key";

    private EncryptKeySetter() {
    }

    public static void set() throws IOException {
        System.setProperty(ENCRYPT_KEY_PROP, get());
    }

    private static String get() throws IOException {
        String encryptKeyFile = System.getenv(ENCRYPT_KEY_FILE);

        if (StringUtils.isBlank(encryptKeyFile)) {
            throw new IllegalStateException(String.format(ENCRYPT_KEY_FILE_NOT_SET, ENCRYPT_KEY_FILE));
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(encryptKeyFile))) {
            Optional<String> optional = reader.lines().filter(StringUtils::isNotBlank).findFirst();
            if (optional.isEmpty()) {
                throw new IllegalStateException(String.format(ENCRYPT_KEY_FILE_EMPTY, encryptKeyFile));
            }
            return optional.get();
        }
    }

}
