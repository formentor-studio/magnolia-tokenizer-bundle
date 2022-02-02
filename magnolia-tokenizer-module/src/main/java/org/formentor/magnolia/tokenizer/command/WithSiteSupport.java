package org.formentor.magnolia.tokenizer.command;

import info.magnolia.context.Context;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

import static info.magnolia.module.site.SiteModule.SITE_MODULE_PATH;
import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ADDRESS_PROPERTY;

public interface WithSiteSupport {

    default Optional<String> getSiteTokenAddress(Node siteNode) throws RepositoryException {
        return siteNode.hasProperty(TOKEN_ADDRESS_PROPERTY)? Optional.of(siteNode.getProperty(TOKEN_ADDRESS_PROPERTY).getString()): Optional.empty();
    }

    default Node getSiteNode(Context ctx) throws RepositoryException {
        return ctx.getJCRSession(RepositoryConstants.CONFIG).getNode(SITE_MODULE_PATH);
    }

    default boolean isSiteTokenized(Node siteNode) throws RepositoryException {
        return getSiteTokenAddress(siteNode).isPresent();
    }
}
