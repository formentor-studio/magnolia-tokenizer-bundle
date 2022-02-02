package org.formentor.magnolia.tokenizer.service.blockchain;

import org.formentor.tokenizer.domain.ethereum.TokenCollectionEthereumImpl;
import org.formentor.tokenizer.domain.ethereum.CredentialsProvider;
import org.formentor.tokenizer.domain.ethereum.Web3jProvider;

import javax.inject.Inject;

/**
 * Injects the implementation of TokenCollection for Ethereum.
 */
public class TokenCollectionEthereumService extends TokenCollectionEthereumImpl{

    @Inject
    public TokenCollectionEthereumService(Web3jProvider web3jProvider, CredentialsProvider credentialsProvider) {
        super(web3jProvider, credentialsProvider);
    }
}
