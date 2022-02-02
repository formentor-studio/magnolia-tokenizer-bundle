package org.formentor.tokenizer.domain;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

public interface TokenCollection {
    CompletableFuture<String> create(String name, String symbol);
    CompletableFuture<BigInteger> mintToken(String collectionAddress, String uri);
    CompletableFuture<String> tokenURI(String collectionAddress, BigInteger tokenId);
}