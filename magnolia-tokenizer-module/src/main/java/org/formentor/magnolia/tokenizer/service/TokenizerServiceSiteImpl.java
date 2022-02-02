package org.formentor.magnolia.tokenizer.service;

import info.magnolia.module.site.Domain;
import info.magnolia.module.site.Site;
import lombok.extern.slf4j.Slf4j;
import org.formentor.magnolia.tokenizer.service.blockchain.OffChainIPFSService;
import org.formentor.magnolia.tokenizer.service.blockchain.TokenCollectionEthereumService;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
public class TokenizerServiceSiteImpl implements TokenizerService <Site, Node> {

    private final TokenCollectionEthereumService tokenCollectionService;
    private final OffChainIPFSService offChainService;
    private final PdfRendererService pdfRendererService;

    @Inject
    public TokenizerServiceSiteImpl(TokenCollectionEthereumService tokenCollectionEthereumService, OffChainIPFSService offChainService, PdfRendererService pdfRendererService) {
        this.tokenCollectionService = tokenCollectionEthereumService;
        this.offChainService = offChainService;
        this.pdfRendererService = pdfRendererService;
    }

    @Override
    public CompletableFuture<String> tokenize(Site site) {
        String tokenName = getMoreRelevantDomain(site).orElse(site.getName());
        return tokenCollectionService.create(tokenName, "");
    }

    @Override
    public CompletableFuture<NFT> mint(Node page, String tokenAddress) {
        try {
            return pdfRendererService.renderPdf(page)
                    .thenCompose(file -> {
                        try {
                            return offChainService.add(Optional.of(page.getPath()), new FileInputStream(file));
                        } catch (RepositoryException | FileNotFoundException e) {
                            log.error("Errors adding file {} to ipfs", file, e);
                            throw new CompletionException(e);
                        }
                    })
                    .thenCompose(uri -> tokenCollectionService.mintToken(tokenAddress, uri)
                            .thenApply((tokenId) -> NFT.builder().tokenId(tokenId).tokenUri(uri).build()
                            )
                    );
        } catch (Exception e) {
            log.error("Errors minting page {} in token {}", page, tokenAddress);
            throw new CompletionException(e);
        }
    }

    private Optional<String> getMoreRelevantDomain(Site site) {
        List<Domain> domains = site.getDomains();
        return domains.isEmpty()? Optional.empty(): Optional.ofNullable(domains.get(0).getName());
    }
}
