package org.formentor.magnolia.tokenizer.service.blockchain;

import org.formentor.magnolia.tokenizer.TokenizerModule;
import org.formentor.tokenizer.domain.OffChainStorageIPFSImpl;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Injects the implementation of OffChainStorage for Ipfs.
 */

public class OffChainIPFSService extends OffChainStorageIPFSImpl{

    @Inject
    public OffChainIPFSService(TokenizerModule definition) {
        super(Optional.ofNullable(definition.getIpfsAddress()).orElse("/ip4/127.0.0.1/tcp/5001"));
    }
}