package org.formentor.magnolia.tokenizer.service.blockchain.ethereum;

import org.formentor.magnolia.tokenizer.TokenizerModule;
import org.formentor.tokenizer.domain.ethereum.Web3jProvider;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.inject.Inject;
import java.util.Optional;

public class Web3jProviderImpl implements Web3jProvider {

    private final Web3j web3j;

    @Inject
    public Web3jProviderImpl(TokenizerModule definition) {
        web3j = Web3j.build(new HttpService(Optional.ofNullable(definition.getEthereumAddress()).orElse("http://localhost:8545")));
    }

    @Override
    public Web3j get() {
        return web3j;
    }
}
