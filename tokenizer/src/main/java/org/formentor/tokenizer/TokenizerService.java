package org.formentor.tokenizer;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.formentor.tokenizer.domain.OffChainStorage;
import org.formentor.tokenizer.domain.OffChainStorageIPFSImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class TokenizerService {
    private final OffChainStorage offChainStorage;

    public TokenizerService(String addressIPFS) {
        this.offChainStorage = new OffChainStorageIPFSImpl(addressIPFS);
    }

    public TokenizerService(OffChainStorage offChainStorage) {
        this.offChainStorage = offChainStorage;
    }

    public CompletableFuture renderPdf(String uri, File target) {
        return CompletableFuture.runAsync(() -> {
            try (OutputStream os = new FileOutputStream(target)) {
                new PdfRendererBuilder()
                        .useFastMode()
                        .withUri(uri)
                        .toStream(os)
                        .run();
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<String> addFile(File file) {
        return offChainStorage.add(file); // addFile returns the id of the asset in off chain storage
    }
}
