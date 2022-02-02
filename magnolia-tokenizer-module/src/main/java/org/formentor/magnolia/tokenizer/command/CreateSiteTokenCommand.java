package org.formentor.magnolia.tokenizer.command;

import info.magnolia.commands.MgnlCommand;
import info.magnolia.context.Context;
import info.magnolia.context.SystemContext;
import info.magnolia.module.site.Site;
import info.magnolia.module.site.SiteManager;
import info.magnolia.ui.api.message.Message;
import info.magnolia.ui.api.message.MessageType;
import info.magnolia.ui.framework.message.MessagesManager;
import lombok.extern.slf4j.Slf4j;
import org.formentor.magnolia.tokenizer.service.TokenizerServiceSiteImpl;

import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ADDRESS_PROPERTY;

@Slf4j
public class CreateSiteTokenCommand extends MgnlCommand implements WithSiteSupport {

    private final SiteManager siteManager;
    private final TokenizerServiceSiteImpl tokenizerServiceSite;
    private final Provider<SystemContext> systemContextProvider;
    private final MessagesManager messagesManager;

    public CreateSiteTokenCommand(SiteManager siteManager, TokenizerServiceSiteImpl tokenizerServiceSite, Provider<SystemContext> systemContextProvider, MessagesManager messagesManager) {
        this.siteManager = siteManager;
        this.tokenizerServiceSite = tokenizerServiceSite;
        this.systemContextProvider = systemContextProvider;
        this.messagesManager = messagesManager;
    }

    @Override
    public boolean execute(Context context) throws Exception {
        Site site = siteManager.getDefaultSite();
        if (isSiteTokenized(getSiteNode(context))) {
            String body = String.format("The token address of the sites is %s ", getSiteTokenAddress(getSiteNode(context)).orElse("UNDEFINED"));
            Message message = new Message(MessageType.INFO, "Site already tokenized", body);
            messagesManager.sendLocalMessage(message);
            return true;
        }

        String tokenAddress = tokenizerServiceSite.tokenize(site).get();
        addTokenInfoToSite(getSiteNode(systemContextProvider.get()), tokenAddress);

        return true;
    }

    private void addTokenInfoToSite(Node siteNode, String tokenAddress) throws RepositoryException {
        siteNode.setProperty(TOKEN_ADDRESS_PROPERTY, tokenAddress);
        siteNode.getSession().save();
    }
}
