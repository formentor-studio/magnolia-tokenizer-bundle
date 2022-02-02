package org.formentor.tokenizer.domain;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface OffChainStorage {
    CompletableFuture<String> add(File file);
    CompletableFuture<String> add(Optional<String> name, InputStream inputStream);
}
