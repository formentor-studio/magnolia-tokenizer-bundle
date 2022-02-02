package org.formentor.magnolia.tokenizer.service;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.Folder;
import org.formentor.magnolia.tokenizer.service.blockchain.OffChainIPFSService;
import org.formentor.magnolia.tokenizer.service.blockchain.TokenCollectionEthereumService;
import org.junit.Test;

import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TokenizerServiceAssetImplTest {

    @Test
    public void tokenize_should_create_collection() {
        TokenCollectionEthereumService tokenCollectionService = mock(TokenCollectionEthereumService.class);
        OffChainIPFSService offChainService = mock(OffChainIPFSService.class);

        TokenizerServiceAssetImpl assetTokenizer = new TokenizerServiceAssetImpl(tokenCollectionService, offChainService);
        Folder assetFolder = mock(Folder.class);
        final String folderName="robots";
        when(assetFolder.getName()).thenReturn(folderName);
        assetTokenizer.tokenize(assetFolder);

        verify(tokenCollectionService, times(1)).create(folderName, "");
    }

    @Test
    public void mint_should_upload_asset_to_ipfs_and_mint_NFT() {
        TokenCollectionEthereumService tokenCollectionService = mock(TokenCollectionEthereumService.class);
        OffChainIPFSService offChainService = mock(OffChainIPFSService.class);
        String uri = "ipfs://bafybeihhii26gwp4w7b7w7d57nuuqeexau4pnnhrmckikaukjuei2dl3fq/file.png";
        when(offChainService.add(any(), any(InputStream.class))).thenReturn(CompletableFuture.completedFuture(uri));
        TokenizerServiceAssetImpl assetTokenizer = new TokenizerServiceAssetImpl(tokenCollectionService, offChainService);

        String tokenAddress = "0xe135783649bfa7c9c4c6f8e528c7f56166efc8a6";
        assetTokenizer.mint(mock(Asset.class), tokenAddress);

        verify(offChainService, times(1)).add(any(Optional.class), any(InputStream.class));
        verify(tokenCollectionService, times(1)).mintToken(tokenAddress, uri);
    }

}
