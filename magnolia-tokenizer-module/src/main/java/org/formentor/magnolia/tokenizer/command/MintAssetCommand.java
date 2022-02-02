package org.formentor.magnolia.tokenizer.command;

import info.magnolia.commands.impl.BaseRepositoryCommand;
import info.magnolia.context.Context;
import info.magnolia.context.SystemContext;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.jcr.JcrAsset;
import info.magnolia.dam.jcr.JcrAssetProvider;
import info.magnolia.ui.api.message.Message;
import info.magnolia.ui.api.message.MessageType;
import info.magnolia.ui.framework.message.MessagesManager;
import org.formentor.magnolia.tokenizer.model.MintableNode;
import org.formentor.magnolia.tokenizer.service.TokenizerServiceAssetImpl;
import org.formentor.magnolia.tokenizer.service.TokenizerService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ADDRESS_PROPERTY;
import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ID_PROPERTY;
import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_NFT_URI_PROPERTY;

public class MintAssetCommand extends BaseRepositoryCommand {

    private final TokenizerServiceAssetImpl tokenizerServiceAssetImpl;
    private final JcrAssetProvider jcrAssetProvider;
    private final Provider<SystemContext> systemContextProvider;
    private final MessagesManager messagesManager;

    @Inject
    public MintAssetCommand(TokenizerServiceAssetImpl tokenizerServiceAssetImpl, JcrAssetProvider jcrAssetProvider, Provider<SystemContext> systemContextProvider, MessagesManager messagesManager) {
        this.tokenizerServiceAssetImpl = tokenizerServiceAssetImpl;
        this.jcrAssetProvider = jcrAssetProvider;
        this.systemContextProvider = systemContextProvider;
        this.messagesManager = messagesManager;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        MintableNode mintableNode = new MintableNode(getJCRNode(ctx));
        Optional<String> tokenAddress = getTokenAddressFromParent(mintableNode.getWrappedNode());

        if (tokenAddress.isEmpty()) {
            String body = String.format("The folder of the asset %s is not tokenized and it is required to mint assets.", mintableNode);
            Message message = new Message(MessageType.INFO, "Asset folder not tokenized", body);
            messagesManager.sendLocalMessage(message);
            return true;
        }

        if (mintableNode.isMinted()) {
            String body = String.format("Asset %s already minted in the token \"%s\" with id \"%s\" and uri \"%s\"."
                    , mintableNode
                    , mintableNode.getTokenAddress().orElse("")
                    , mintableNode.getTokenId().orElse("")
                    , mintableNode.getTokenUri().orElse("")
            );
            Message message = new Message(MessageType.INFO, "Asset already minted", body);
            messagesManager.sendLocalMessage(message);
            return true;
        }

        try {
            Asset asset = new JcrAsset(jcrAssetProvider, mintableNode.getWrappedNode());
            TokenizerService.NFT nft = tokenizerServiceAssetImpl.mint(asset, tokenAddress.get()).get();
            addTokenInfoToAsset(mintableNode.getWrappedNode(), tokenAddress.get(), nft.getTokenId().toString(), nft.getTokenUri()); // TODO use CompletableFuture.thenAccept() instead of CompletableFuture.get()

            return true;
        } catch (CompletionException e) {
            log.error("Errors minting asset {}", mintableNode, e);
            throw new Exception("Errors minting asset " + mintableNode, e);
        }
    }

    private Optional<String> getTokenAddressFromParent(Node node) throws Exception {
        Node folder = node.getParent();
        if (folder.getPath().equals("/") || !folder.hasProperty(TOKEN_ADDRESS_PROPERTY)) {
            return Optional.empty();
        } else {
            return Optional.of(folder.getProperty(TOKEN_ADDRESS_PROPERTY).getString());
        }
    }

    private void addTokenInfoToAsset(Node node, String tokenAddress, String tokenId, String tokenUri) throws RepositoryException {
        String workspace = node.getSession().getWorkspace().getName();
        Node systemSessionNode = systemContextProvider.get().getJCRSession(workspace).getNode(node.getPath());
        systemSessionNode.setProperty(TOKEN_ADDRESS_PROPERTY, tokenAddress);
        systemSessionNode.setProperty(TOKEN_ID_PROPERTY, tokenId);
        systemSessionNode.setProperty(TOKEN_NFT_URI_PROPERTY, tokenUri);
        systemSessionNode.getSession().save();
    }
}
