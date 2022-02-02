package org.formentor.magnolia.tokenizer.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

public interface TokenizerService<T, M> {
    CompletableFuture<String> tokenize(T content);
    CompletableFuture<NFT> mint(M content, String tokenAddress);

    @Builder
    @Getter
    @Setter
    class NFT {
        private BigInteger tokenId;
        private String tokenUri;
    }
}
