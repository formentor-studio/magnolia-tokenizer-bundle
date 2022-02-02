package org.formentor.magnolia.tokenizer.service;

import info.magnolia.init.MagnoliaConfigurationProperties;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.File;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PdfRendererServiceTest {

    @Test
    public void renderPdf_has_to_create_pdf_of_the_page() throws RepositoryException, ExecutionException, InterruptedException {
        final String nodePath = "/robot";
        final String assetPage = "<html><head></head><body><h1>I am an happy asset</h1></body></html>";
        MockWebServer server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                if (recordedRequest.getPath().equals(nodePath)) {
                    return new MockResponse().setResponseCode(200).setBody(assetPage);
                }
                return new MockResponse().setResponseCode(404);
            }
        });

        MagnoliaConfigurationProperties magnoliaConfigurationProperties = mock(MagnoliaConfigurationProperties.class);
        when(magnoliaConfigurationProperties.getProperty(MagnoliaConfigurationProperties.MAGNOLIA_WEBAPP)).thenReturn("ROOT");

        PdfRendererService pdfRendererService = new PdfRendererService(magnoliaConfigurationProperties);
        pdfRendererService.port = server.getPort();

        Node page = mock(Node.class);
        when(page.getPath()).thenReturn(nodePath);
        File file = pdfRendererService.renderPdf(page).get();
        assertTrue(file.exists());
    }
}
