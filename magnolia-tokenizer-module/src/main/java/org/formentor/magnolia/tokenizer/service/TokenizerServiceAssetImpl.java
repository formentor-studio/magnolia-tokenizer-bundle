package org.formentor.magnolia.tokenizer.service;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.Folder;
import lombok.extern.slf4j.Slf4j;
import org.formentor.magnolia.tokenizer.service.blockchain.OffChainIPFSService;
import org.formentor.magnolia.tokenizer.service.blockchain.TokenCollectionEthereumService;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class TokenizerServiceAssetImpl implements TokenizerService<Folder, Asset> {
    private final TokenCollectionEthereumService tokenCollectionService;
    private final OffChainIPFSService offChainService;

    @Inject
    public TokenizerServiceAssetImpl(TokenCollectionEthereumService tokenCollectionService, OffChainIPFSService offChainService) {
        this.tokenCollectionService = tokenCollectionService;
        this.offChainService = offChainService;
    }

    public CompletableFuture<String> tokenize(Folder assetFolder) {
        final String name = assetFolder.getName();
        return tokenCollectionService.create(name, "");
    }

    public CompletableFuture<NFT> mint(Asset asset, String tokenAddress) {
        return offChainService.add(Optional.ofNullable(asset.getFileName()), asset.getContentStream())
                .thenCompose(uri -> tokenCollectionService
                        .mintToken(tokenAddress, uri)
                        .thenApply(tokenId -> NFT.builder()
                                .tokenId(tokenId)
                                .tokenUri(uri)
                                .build()
                        )
                );
    }
}
