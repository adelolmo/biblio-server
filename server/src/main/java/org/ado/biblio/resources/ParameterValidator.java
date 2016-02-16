package org.ado.biblio.resources;

import org.apache.commons.lang3.StringUtils;

public class ParameterValidator {

    public static void checkNotEmpty(String key, String message) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException(message);
        }
    }
}