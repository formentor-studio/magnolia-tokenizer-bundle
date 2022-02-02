package org.formentor.magnolia.tokenizer.command;

import info.magnolia.context.Context;
import info.magnolia.context.SystemContext;
import info.magnolia.module.site.ConfiguredSite;
import info.magnolia.module.site.Site;
import info.magnolia.module.site.SiteManager;
import info.magnolia.ui.framework.message.MessagesManager;
import org.formentor.magnolia.tokenizer.service.TokenizerServiceSiteImpl;
import org.junit.Test;

import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ADDRESS_PROPERTY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateSiteTokenCommandTest {

    @Test
    public void execute_has_to_deploy_contract_of_collections_and_set_the_token_address() throws Exception {
        final Site site = new ConfiguredSite();
        final String tokenAddress = "0xabc";
        SiteManager siteManager = mockSiteManager(site);
        TokenizerServiceSiteImpl tokenizerServiceSite = mockTokenizerServiceSiteImpl(site, tokenAddress);
        Provider<SystemContext> systemContextProvider = () -> mockSystemContext(Optional.empty());
        CreateSiteTokenCommand createSiteTokenCommand = new CreateSiteTokenCommand(siteManager, tokenizerServiceSite, systemContextProvider, mock(MessagesManager.class));

        boolean result = createSiteTokenCommand.execute(mockContext(Optional.empty()));

        assertTrue(result);
        verify(tokenizerServiceSite, times(1)).tokenize(site);
        // TODO check that property "tokenAddress" is set.
    }

    @Test
    public void tokenization_not_allowed_for_already_tokenized_sites() throws Exception {
        TokenizerServiceSiteImpl tokenizerServiceSite = mock(TokenizerServiceSiteImpl.class);
        MessagesManager messagesManager = mock(MessagesManager.class);
        CreateSiteTokenCommand createSiteTokenCommand = new CreateSiteTokenCommand(mock(SiteManager.class), tokenizerServiceSite, mock(Provider.class), messagesManager);

        boolean result = createSiteTokenCommand.execute(mockContext(Optional.of("0xabc")));

        assertTrue(result);
        verify(tokenizerServiceSite, times(0)).tokenize(any());
        verify(messagesManager, times(1)).sendLocalMessage(any());
    }

    private SiteManager mockSiteManager(Site site) {
        SiteManager siteManager = mock(SiteManager.class);
        when(siteManager.getDefaultSite()).thenReturn(site);

        return siteManager;
    }

    private TokenizerServiceSiteImpl mockTokenizerServiceSiteImpl(Site site, String tokenAddress) {
        TokenizerServiceSiteImpl tokenizerServiceSite = mock(TokenizerServiceSiteImpl.class);
        when(tokenizerServiceSite.tokenize(site)).thenReturn(CompletableFuture.supplyAsync(()->tokenAddress));

        return tokenizerServiceSite;
    }

    private SystemContext mockSystemContext(Optional<String> tokenAddress) {
        try {
            SystemContext systemContext = mock(SystemContext.class);
            Session session = mockSession(tokenAddress);
            when(systemContext.getJCRSession(anyString())).thenReturn(session);

            return systemContext;
        } catch (RepositoryException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Context mockContext(Optional<String> tokenAddress) {
        try {
            Context context = mock(Context.class);
            Session session = mockSession(tokenAddress);
            when(context.getJCRSession(anyString())).thenReturn(session);

            return context;
        } catch (RepositoryException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Session mockSession(Optional<String> tokenAddress) throws RepositoryException {
        Session session = mock(Session.class);
        Node siteNode = mock(Node.class);
        when(siteNode.getSession()).thenReturn(session);
        if (tokenAddress.isPresent()) {
            when(siteNode.hasProperty(TOKEN_ADDRESS_PROPERTY)).thenReturn(true);
            Property propertyTokenAddress = mock(Property.class);
            when(propertyTokenAddress.getString()).thenReturn(tokenAddress.get());
            when(siteNode.getProperty(TOKEN_ADDRESS_PROPERTY)).thenReturn(propertyTokenAddress);
        }
        when(session.getNode(anyString())).thenReturn(siteNode);

        return session;
    }
}
