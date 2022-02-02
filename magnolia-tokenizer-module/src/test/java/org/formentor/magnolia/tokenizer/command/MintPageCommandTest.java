package org.formentor.magnolia.tokenizer.command;

import info.magnolia.context.Context;
import info.magnolia.ui.framework.message.MessagesManager;
import org.formentor.magnolia.tokenizer.service.TokenizerServiceSiteImpl;
import org.junit.Test;

import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.Optional;

import static org.formentor.magnolia.tokenizer.TokenizerModule.TOKEN_ADDRESS_PROPERTY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MintPageCommandTest {

    @Test
    public void execute_has_to_mint_page_in_token_contract_of_the_site() {
        // TODO this test requires to mock MgnlContext, please use magnolia-core:text-jar
    }

    @Test
    public void minting_not_allowed_for_not_tokenized_sites() throws Exception {
        TokenizerServiceSiteImpl tokenizerServiceSite = mock(TokenizerServiceSiteImpl.class);
        MessagesManager messagesManager = mock(MessagesManager.class);

        MintPageCommand mintPageCommand = new MintPageCommand(tokenizerServiceSite, messagesManager, mock(Provider.class));
        boolean result = mintPageCommand.execute(mockContext(Optional.empty()));

        assertTrue(result);
        verify(tokenizerServiceSite, times(0)).mint(any(), any());
        verify(messagesManager, times(1)).sendLocalMessage(any());
    }

    @Test
    public void minting_not_allowed_for_already_minted_pages() {
        // TODO this test requires to mock MgnlContext, please use magnolia-core:text-jar
    }

    @Test
    public void minting_not_allowed_when_item_is_not_a_page() {
        // TODO this test requires to mock MgnlContext, please use magnolia-core:text-jar
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
