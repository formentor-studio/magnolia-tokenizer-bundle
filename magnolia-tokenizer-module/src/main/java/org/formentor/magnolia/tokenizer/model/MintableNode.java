package org.formentor.magnolia.tokenizer.model;

import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ADDRESS_PROPERTY;
import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ID_PROPERTY;
import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_NFT_URI_PROPERTY;

@Slf4j
public class MintableNode {
    
    private final Node wrappedNode;
    
    public MintableNode(Node node) {
        this.wrappedNode = node;
    }

    public Node getWrappedNode() {
        return wrappedNode;
    }

    public boolean isMinted() {
        try {
            return (wrappedNode.hasProperty(TOKEN_ADDRESS_PROPERTY) && wrappedNode.hasProperty(TOKEN_ID_PROPERTY) && wrappedNode.hasProperty(TOKEN_NFT_URI_PROPERTY));
        } catch (RepositoryException e) {
            log.error("Errors calling isMinted({})", wrappedNode, e);
            return false;
        }
    }

    public Optional<String> getTokenAddress() {
        return getPropertyString(TOKEN_ADDRESS_PROPERTY, wrappedNode);
    }

    public Optional<String> getTokenId() {
        return getPropertyString(TOKEN_ID_PROPERTY, wrappedNode);
    }

    public Optional<String> getTokenUri() {
        return getPropertyString(TOKEN_NFT_URI_PROPERTY, wrappedNode);
    }

    public Optional<String> getPropertyString(String property, Node node) {
        try {
            return (node.hasProperty(property))? Optional.of(node.getProperty(property).getString()):  Optional.empty();
        } catch (RepositoryException e) {
            log.error("Errors getting property {} of {}", property, node);
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return wrappedNode.toString();
    }
}
