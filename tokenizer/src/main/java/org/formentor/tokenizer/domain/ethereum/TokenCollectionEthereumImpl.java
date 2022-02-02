package org.formentor.tokenizer.domain.ethereum;

import org.formentor.tokenizer.domain.TokenCollection;
import org.formentor.tokenizer.domain.ethereum.CredentialsProvider;
import org.formentor.tokenizer.domain.ethereum.Web3jProvider;
import org.formentor.tokenizer.domain.ethereum.contract.CMSCollection;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

public class TokenCollectionEthereumImpl implements TokenCollection {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;

    public TokenCollectionEthereumImpl(Web3jProvider web3jProvider, CredentialsProvider credentialsProvider) {
        this.web3j = web3jProvider.get();
        this.credentials = credentialsProvider.get();
        this.gasProvider = new DefaultGasProvider();
    }

    @Override
    public CompletableFuture<String> create(String name, String symbol) {
        return CMSCollection.deploy(web3j, credentials, gasProvider, name, symbol)
                .sendAsync()
                .thenApply(Contract::getContractAddress);
    }

    @Override
    public CompletableFuture<BigInteger> mintToken(String collectionAddress, String uri) {
        final CMSCollection collection = CMSCollection.load(collectionAddress, web3j, credentials, gasProvider);
        return collection.mint(uri)
                .sendAsync()
                .thenApply(transactionReceipt -> collection.getMintedEvents(transactionReceipt)
                        .stream()
                        .findFirst()
                        .map(mintedEventResponse -> mintedEventResponse._tokenId)
                        .get() // The tokenId will always be returned by the event, it is not necessary to add a condition or return an Optional.
                );
    }

    @Override
    public CompletableFuture<String> tokenURI(String collectionAddress, BigInteger tokenId) {
        final CMSCollection collection = CMSCollection.load(collectionAddress, web3j, credentials, gasProvider);
        return collection.tokenURI(tokenId).sendAsync();
    }

}
