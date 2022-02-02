package org.formentor.magnolia.tokenizer.service;

import info.magnolia.module.site.Site;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.formentor.magnolia.tokenizer.service.blockchain.OffChainIPFSService;
import org.formentor.magnolia.tokenizer.service.blockchain.TokenCollectionEthereumService;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TokenizerServiceSiteImplStepdefs {

    private TokenizerServiceSiteImpl tokenizerServiceSite;
    private TokenCollectionEthereumService tokenCollection;
    private OffChainIPFSService offChainService;
    private PdfRendererService pdfRendererService;

    @Before
    public void setUpStep() {
        tokenCollection = mock(TokenCollectionEthereumService.class);
        when(tokenCollection.mintToken(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(BigInteger.ONE));
        offChainService = mock(OffChainIPFSService.class);
        when(offChainService.add(any(Optional.class), any(InputStream.class))).thenReturn(CompletableFuture.completedFuture("ipfs://"));
        pdfRendererService = mock(PdfRendererService.class);
        when(pdfRendererService.renderPdf(any(Node.class))).thenReturn(CompletableFuture.completedFuture(new File("src/test/resources/assets/token.pdf")));

        tokenizerServiceSite = new TokenizerServiceSiteImpl(tokenCollection, offChainService, pdfRendererService);
    }

    @When("I tokenize the site {string}")
    public void i_tokenize_the_site(String siteName) {
        Site site = mock(Site.class);
        when(site.getName()).thenReturn(siteName);
        tokenizerServiceSite.tokenize(site);
    }

    @Then("It is created a token named {string}")
    public void it_is_created_a_token_named(String siteName) {
        verify(tokenCollection, times(1)).create(siteName, "");
    }

    @When("I mint the page {string} of the site {string}")
    public void i_mint_the_page_of_the_site(String pagePath, String site) throws RepositoryException, ExecutionException, InterruptedException {
        Node page = mock(Node.class);
        when(page.getPath()).thenReturn(pagePath);
        TokenizerService.NFT nft = tokenizerServiceSite.mint(page, "token-address").get();
        assertNotNull(nft);
    }

    @Then("It is minted a NFT for the pdf of the page {string}")
    public void it_is_minted_a_NFT_for_the_pdf_of_the_page(String string) {
        // TODO validate that the param of renderPdf() matches the param sent to mint()
        verify(pdfRendererService, times(1)).renderPdf(any(Node.class));
        // TODO validate that the param of add() matches the return of the mock of pdfRendererService
        verify(offChainService, times(1)).add(any(Optional.class), any(InputStream.class));
        // TODO validate that the param of mintToken() matches the param sent to mint() and the return of the mock of offChainStorage
        verify(tokenCollection, times(1)).mintToken(anyString(), anyString());
    }
}
