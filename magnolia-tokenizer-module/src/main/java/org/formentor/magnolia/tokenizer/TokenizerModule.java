package org.formentor.magnolia.tokenizer;

import info.magnolia.init.MagnoliaConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is optional and represents the configuration for the magnolia-tokenizer-module module.
 * By exposing simple getter/setter/adder methods, this bean can be configured via content2bean
 * using the properties and node from <tt>config:/modules/magnolia-tokenizer-module</tt>.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 * See https://documentation.magnolia-cms.com/display/DOCS/Module+configuration for information about module configuration.
 */
public class TokenizerModule {
    /* you can optionally implement info.magnolia.module.ModuleLifecycle */

    public final static String TOKEN_ADDRESS_PROPERTY = "tokenAddress";
    public static final String TOKEN_ID_PROPERTY = "tokenId";
    public static final String TOKEN_NFT_URI_PROPERTY = "tokenUri";

    public TokenizerModule(MagnoliaConfigurationProperties configurationProperties) {
        ethereumAddress = configurationProperties.getProperty("ETHEREUM_ADDRESS");
        ipfsAddress = configurationProperties.getProperty("IPFS_ADDRESS");
    }

    @Setter
    @Getter
    private String ethereumAddress;

    @Setter
    @Getter
    private String ipfsAddress;
}
