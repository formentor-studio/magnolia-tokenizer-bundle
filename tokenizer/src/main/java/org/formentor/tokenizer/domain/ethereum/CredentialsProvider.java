package org.formentor.tokenizer.domain.ethereum;

import org.web3j.crypto.Credentials;

public interface CredentialsProvider {
    Credentials get();
}
