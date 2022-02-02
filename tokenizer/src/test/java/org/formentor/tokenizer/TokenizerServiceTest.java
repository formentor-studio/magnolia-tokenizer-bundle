package org.formentor.tokenizer;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.formentor.tokenizer.domain.OffChainStorage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenizerServiceTest {

    private static final File SAMPLE_FILE = new File("src/test/resources/assets/token.pdf");

    @Disabled // Integration test
    @Test
    void renderPdf_has_to_generate_pdf() throws IOException {
        final String assetPage = "<html><head></head><body><h1>I am an happy asset</h1></body></html>";
        MockWebServer server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                return new MockResponse().setResponseCode(200).setBody(assetPage);
            }
        });

        TokenizerService tokenizer = new TokenizerService("/ip4/127.0.0.1/tcp/5001");
        final File target = new File(String.format("src/test/resources/assets/%s.pdf", System.currentTimeMillis()));

        tokenizer.renderPdf(server.url("asset.html").toString(), target).join();

        assertTrue(target.exists());

        // After test
        target.deleteOnExit();
        server.shutdown();
    }

    @Test
    void addFile_has_to_add_file_to_Ipfs() throws ExecutionException, InterruptedException {
        final String cid = "QmWndQVs7KMXMZ3kx9CEprGnyjJwjSaPtXENhq2FHCjyUE";
        final OffChainStorage offChainStorage = mock(OffChainStorage.class);
        when(offChainStorage.add(any(File.class))).thenReturn(CompletableFuture.completedFuture(cid));
        TokenizerService tokenizer = new TokenizerService(offChainStorage);

        String id = tokenizer.addFile(SAMPLE_FILE).get();

        assertEquals(cid, id);
    }

}
