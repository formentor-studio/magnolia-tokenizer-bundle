package org.formentor.magnolia.tokenizer.command;

import info.magnolia.commands.impl.BaseRepositoryCommand;
import info.magnolia.context.Context;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.ui.api.message.Message;
import info.magnolia.ui.api.message.MessageType;
import info.magnolia.ui.framework.message.MessagesManager;
import org.formentor.magnolia.tokenizer.model.MintableNode;
import org.formentor.magnolia.tokenizer.service.TokenizerService;
import org.formentor.magnolia.tokenizer.service.TokenizerServiceSiteImpl;

import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ADDRESS_PROPERTY;
import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ID_PROPERTY;
import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_NFT_URI_PROPERTY;

public class MintPageCommand extends BaseRepositoryCommand implements WithSiteSupport {

    private final TokenizerServiceSiteImpl tokenizerServiceSite;
    private final MessagesManager messagesManager;
    private final Provider<SystemContext> systemContextProvider;

    public MintPageCommand(TokenizerServiceSiteImpl tokenizerServiceSite, MessagesManager messagesManager, Provider<SystemContext> systemContextProvider) {
        this.tokenizerServiceSite = tokenizerServiceSite;
        this.messagesManager = messagesManager;
        this.systemContextProvider = systemContextProvider;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        Node siteNode = getSiteNode(ctx);
        if (!isSiteTokenized(siteNode)) {
            String body = String.format("Before minting pages it is necessary to tokenize the site.");
            Message message = new Message(MessageType.INFO, "Site not tokenized", body);
            messagesManager.sendLocalMessage(message);

            return true;
        }

        MintableNode mintableNode = new MintableNode(getJCRNode(ctx));
        if (mintableNode.isMinted()) {
            String body = String.format("Page %s already minted in the token \"%s\" with id \"%s\" and uri \"%s\"."
                    , mintableNode
                    , mintableNode.getTokenAddress().orElse("")
                    , mintableNode.getTokenId().orElse("")
                    , mintableNode.getTokenUri().orElse("")
            );
            Message message = new Message(MessageType.INFO, "Page already minted", body);
            messagesManager.sendLocalMessage(message);

            return true;
        }

        if (!isPage(mintableNode.getWrappedNode())) {
            String body = String.format("The selected item %s is not a page.", mintableNode.getWrappedNode());
            Message message = new Message(MessageType.INFO, "Item is not a page", body);
            messagesManager.sendLocalMessage(message);

            return true;
        }

        Optional<String> tokenAddress = getSiteTokenAddress(siteNode);
        TokenizerService.NFT nft = tokenizerServiceSite
                .mint(mintableNode.getWrappedNode(), tokenAddress.get())
                .get();

        addTokenInfoToPage(mintableNode.getWrappedNode(), tokenAddress.get(), nft.getTokenId().toString(), nft.getTokenUri());

        return true;
    }

    private boolean isPage(Node node) {
        try {
            return node.isNodeType(NodeTypes.Page.NAME);
        } catch (RepositoryException e) {
            log.error("Error checking if node {} is a page", node);
            return false;
        }
    }

    private void addTokenInfoToPage(Node node, String tokenAddress, String tokenId, String tokenUri) throws RepositoryException {
        String workspace = node.getSession().getWorkspace().getName();
        Node systemSessionNode = systemContextProvider.get().getJCRSession(workspace).getNode(node.getPath());
        systemSessionNode.setProperty(TOKEN_ADDRESS_PROPERTY, tokenAddress);
        systemSessionNode.setProperty(TOKEN_ID_PROPERTY, tokenId);
        systemSessionNode.setProperty(TOKEN_NFT_URI_PROPERTY, tokenUri);
        systemSessionNode.getSession().save();
    }

}
