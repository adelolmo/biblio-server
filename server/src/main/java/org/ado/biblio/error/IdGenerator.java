package org.ado.biblio.error;

import java.util.concurrent.ThreadLocalRandom;

public class IdGenerator {

    public long random() {
        return ThreadLocalRandom.current().nextLong();
    }

}