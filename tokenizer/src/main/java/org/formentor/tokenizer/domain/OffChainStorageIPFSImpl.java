package org.formentor.tokenizer.domain;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class OffChainStorageIPFSImpl implements OffChainStorage {

    private final IPFS ipfs;

    public OffChainStorageIPFSImpl(String address) {
        ipfs = new IPFS(address);
    }

    public OffChainStorageIPFSImpl(IPFS ipfs) {
        this.ipfs = ipfs;
    }

    @Override
    public CompletableFuture<String> add(File file) {
        NamedStreamable asset = new NamedStreamable.FileWrapper(file);
        try {
            return CompletableFuture.completedFuture(getUri(addToIpfs(asset)));
        } catch (IOException e) {
            log.error("Errors adding file {}", file, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public CompletableFuture<String> add(Optional<String> name, InputStream inputStream) {
        NamedStreamable asset = new NamedStreamable.InputStreamWrapper(name, inputStream);
        try {
            return CompletableFuture.completedFuture(getUri(addToIpfs(asset)));
        } catch (IOException e) {
            log.error("Errors adding inputStream", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private List<MerkleNode> addToIpfs(NamedStreamable asset) throws IOException {
        return ipfs.add(asset, true); // wrap with directory to allow fetching asset by name, see getUri()
    }

    private String getUri(List<MerkleNode> tree) {
        /**
         * It requires the request param "wrap-with-directory" of "POST /api/v0/add" to be true.
         * ipfs will create a directory for the file which allows to fetch the file with the name.
         *
         * e.g.
         * POST /api/v0/add?wrap-with-directory=true Content-Disposition: file; filename=robots.jpg
         * http://127.0.0.1:8080/ipfs/<cid>/robots.jpg
         *
         * POST /api/v0/add?wrap-with-directory=true Content-Disposition: file; filename=/site/robots.jpg
         * http://127.0.0.1:8080/ipfs/<cid>/site/robots.jpg
         *
         * Where <cid> is the cid of the directory containing the file
         */
        MerkleNode leaf = tree.get(0);
        if (tree.size() > 1) {
            MerkleNode root = tree.get(tree.size()-1);
            return "ipfs://" + root.hash.toBase58() + "/" + leaf.name.get();
        } else {
            return "ipfs://" + leaf.hash.toBase58();
        }
    }
}
