package org.formentor.tokenizer.domain;

import org.formentor.tokenizer.domain.ethereum.CredentialsProvider;
import org.formentor.tokenizer.domain.ethereum.TokenCollectionEthereumImpl;
import org.formentor.tokenizer.domain.ethereum.Web3jProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.EVMTest;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EVMTest
class TokenCollectionEthereumImplTest {

    private static final Map<String, String> ACCOUNT = new HashMap<>(); {
        ACCOUNT.put("privateKey", "0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63");
        ACCOUNT.put("publicKey", "0xfe3b557e8fb62b89f4916b721be55ceb828dbd73");
    }

    private Web3jProvider web3jProvider;
    private CredentialsProvider credentialsProvider;

    @BeforeEach
    void setUp(Web3j web3j) {
        web3jProvider = () -> web3j;
        credentialsProvider = () -> Credentials.create(ACCOUNT.get("privateKey"));
    }

    @Test
    void create_collection_should_deploy_contract() throws ExecutionException, InterruptedException {
        // Web3j.build(new HttpService("http://localhost:8545"));

        TokenCollectionEthereumImpl collectionsService = new TokenCollectionEthereumImpl(web3jProvider, credentialsProvider);
        CompletableFuture<String> futureCreate = collectionsService.create("Graffities", "GFT");
        final String collectionAddress = futureCreate.get();

        assertNotNull(collectionAddress);
    }

    @Test
    void mint_nft_should_add_asset_to_collection(Web3j web3j) throws ExecutionException, InterruptedException {
        TokenCollectionEthereumImpl collectionsService = new TokenCollectionEthereumImpl(web3jProvider, credentialsProvider);
        final String collectionAddress = collectionsService.create("Graffities", "GFT").get();

        final String tokenURI = "ipfs://0a5";
        final String uri = collectionsService.mintToken(collectionAddress, tokenURI)
                .thenCompose(tokenId -> collectionsService.tokenURI(collectionAddress, tokenId))
                .get();

        assertEquals(tokenURI, uri);
    }
}
