package org.formentor.tokenizer.domain;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OffChainStorageIPFSImplTest {
    // TODO Test adding a file inside a folder

    @Test
    void add_file_should_add_asset_in_IPFS() throws IOException, ExecutionException, InterruptedException {
        final String cid = "QmWndQVs7KMXMZ3kx9CEprGnyjJwjSaPtXENhq2FHCjyUE";
        final IPFS ipfs = mockIPFS(cid);

        final OffChainStorageIPFSImpl offChainStorageIPFS = new OffChainStorageIPFSImpl(ipfs);
        CompletableFuture<String> addFuture = offChainStorageIPFS.add(new File("src/test/resources/assets/token.pdf"));
        String actual = addFuture.get();

        String expected = "ipfs://" + cid;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void add_should_return_error_when_IPFS_fails() throws IOException {
        final IPFS ipfs = mock(IPFS.class);
        when(ipfs.add(any(NamedStreamable.class), anyBoolean())).thenThrow(new IOException("fake-error"));
        final OffChainStorageIPFSImpl offChainStorageIPFS = new OffChainStorageIPFSImpl(ipfs);

        CompletableFuture<String> addFuture = offChainStorageIPFS.add(new File("src/test/resources/assets/token.pdf"));
        CompletionException completionException = Assertions.assertThrows(CompletionException.class, addFuture::join);
        assertTrue(completionException.getCause() instanceof IOException);
    }

    @Test
    void add_stream_should_add_asset_in_IPFS() throws IOException, ExecutionException, InterruptedException {
        final String cid = "QmWndQVs7KMXMZ3kx9CEprGnyjJwjSaPtXENhq2FHCjyUE";
        final IPFS ipfs = mockIPFS(cid);

        final OffChainStorageIPFSImpl offChainStorageIPFS = new OffChainStorageIPFSImpl(ipfs);
        CompletableFuture<String> addFuture = offChainStorageIPFS.add(Optional.of("token.pdf"), new FileInputStream("src/test/resources/assets/token.pdf"));
        String actual = addFuture.get();

        String expected = "ipfs://" + cid;
        Assertions.assertEquals(expected, actual);
    }

    private IPFS mockIPFS(String cid) throws IOException {
        IPFS ipfs = mock(IPFS.class);
        if (cid != null) {
            final MerkleNode merkleNode = new MerkleNode(cid);
            when(ipfs.add(any(NamedStreamable.class), anyBoolean())).thenReturn(List.of(merkleNode));
        }
        return ipfs;
    }
}
