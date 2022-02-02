package org.formentor.magnolia.tokenizer.command;

import info.magnolia.commands.impl.BaseRepositoryCommand;
import info.magnolia.context.Context;
import info.magnolia.context.SystemContext;
import info.magnolia.dam.api.Folder;
import info.magnolia.dam.jcr.JcrAssetProvider;
import info.magnolia.dam.jcr.JcrFolder;
import info.magnolia.ui.api.message.Message;
import info.magnolia.ui.api.message.MessageType;
import info.magnolia.ui.framework.message.MessagesManager;
import org.formentor.magnolia.tokenizer.service.TokenizerServiceAssetImpl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.concurrent.ExecutionException;

import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ADDRESS_PROPERTY;

public class CreateTokenCommand extends BaseRepositoryCommand {
    private final TokenizerServiceAssetImpl tokenizerServiceAssetImpl;
    private final JcrAssetProvider jcrAssetProvider;
    private final Provider<SystemContext> systemContextProvider;
    private final MessagesManager messagesManager;

    @Inject
    public CreateTokenCommand(TokenizerServiceAssetImpl tokenizerServiceAssetImpl, JcrAssetProvider jcrAssetProvider, Provider<SystemContext> systemContextProvider, MessagesManager messagesManager) {
        this.tokenizerServiceAssetImpl = tokenizerServiceAssetImpl;
        this.jcrAssetProvider = jcrAssetProvider;
        this.systemContextProvider = systemContextProvider;
        this.messagesManager = messagesManager;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        Node node = getJCRNode(ctx);
        if (isTokenized(node)) {
            String body = String.format("The token address of the folder \"%s\" is %s ", node.getName(), node.getProperty(TOKEN_ADDRESS_PROPERTY).getString());
            Message message = new Message(MessageType.INFO, "Folder already tokenized", body);
            messagesManager.sendLocalMessage(message);
            return true;
        }

        try {
            Folder folder = new JcrFolder(jcrAssetProvider, node);
            String tokenAddress = tokenizerServiceAssetImpl.tokenize(folder).get();

            Node systemSessionNode = systemContextProvider.get().getJCRSession(getRepository()).getNode(getPath());
            systemSessionNode.setProperty(TOKEN_ADDRESS_PROPERTY, tokenAddress);
            systemSessionNode.getSession().save();

            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Errors tokenizing folder of assets {}", node, e);
            throw new Exception("Errors tokenizing folder of assets " + node, e);
        }
    }

    private boolean isTokenized(Node node) {
        try {
            return node.hasProperty(TOKEN_ADDRESS_PROPERTY);
        } catch (RepositoryException e) {
            log.error("Errors checking {}", node, e);
            return false;
        }
    }
}
