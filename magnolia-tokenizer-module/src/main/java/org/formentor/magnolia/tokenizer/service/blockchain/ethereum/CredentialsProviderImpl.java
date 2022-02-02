package org.formentor.magnolia.tokenizer.service.blockchain.ethereum;

import info.magnolia.init.MagnoliaConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.formentor.tokenizer.domain.ethereum.CredentialsProvider;
import org.web3j.crypto.Credentials;

import javax.inject.Inject;

@Slf4j
public class CredentialsProviderImpl implements CredentialsProvider {

    private Credentials credentials;

    @Inject
    public CredentialsProviderImpl(MagnoliaConfigurationProperties configurationProperties) {
        String privateKey = configurationProperties.getProperty("ETHEREUM_ACCOUNT");
        if (privateKey != null) {
            credentials = Credentials.create(privateKey);
        } else {
            log.error("Missing Ethereum account, please set the account in the property ETHEREUM_ACCOUNT");
        }

    }

    @Override
    public Credentials get() {
        return credentials;
    }
}
