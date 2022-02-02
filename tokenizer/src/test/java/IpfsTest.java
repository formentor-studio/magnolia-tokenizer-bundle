import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multiaddr.MultiAddress;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class IpfsTest {

    @Disabled // Integration test
    @Test
    void add_file_to_ipfs_has_return_cid() throws IOException {
        final String CID = "QmWndQVs7KMXMZ3kx9CEprGnyjJwjSaPtXENhq2FHCjyUE";

        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        Path path = Paths.get("src/test/resources/assets/token.pdf");
        NamedStreamable file = new NamedStreamable.FileWrapper(path.toFile());
        List<MerkleNode> tree = ipfs.add(file);

        Assert.assertEquals(1, tree.size());
        Assert.assertEquals("token.pdf", tree.get(0).name.get());
        Assert.assertEquals(CID, tree.get(0).hash.toBase58());
    }

    @Disabled // Integration test
    @Test
    void add_stream_to_ipfs() throws IOException {
        final String CID = "QmWndQVs7KMXMZ3kx9CEprGnyjJwjSaPtXENhq2FHCjyUE";

        File file = new File("src/test/resources/assets/token.pdf");
        NamedStreamable stream = new NamedStreamable.InputStreamWrapper(new FileInputStream(file));

        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        List<MerkleNode> tree = ipfs.add(stream);

        Assert.assertEquals(1, tree.size());
        Assert.assertEquals(CID, tree.get(0).name.get());
        Assert.assertEquals(CID, tree.get(0).hash.toBase58());
    }

    @Disabled // Integration test
    @Test
    void pipe_renderPdf_to_ipfs() throws IOException, ExecutionException, InterruptedException {
        String uri = "http://localhost:9080/robot";

        String ipfsuri = CompletableFuture.supplyAsync(() -> {
            File target; // new File(String.format("src/test/resources/assets/%s.pdf", System.currentTimeMillis()));
            try {
                target = File.createTempFile("magnolia-tokenizer", ".pdf");
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            try (FileOutputStream os = new FileOutputStream(target)){
                new PdfRendererBuilder()
                        .useFastMode()
                        .withUri(uri)
                        .toStream(os)
                        .run();
                return target;
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }).thenApply((file) -> {
            try {
                IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
                NamedStreamable stream = new NamedStreamable.InputStreamWrapper("/com/site/robot.pdf", new FileInputStream(file));
                stream.getName();
                // http://127.0.0.1:8080/ipfs/QmTsk58qtpz1bMqS8BCWSqDyHwFfVD6ZetnSUGr9HEqv4K
                List<MerkleNode> tree = ipfs.add(stream, true);
                return tree.get(tree.size()-1).hash.toBase58() + "/" + tree.get(0).name.get();

            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).get();

        System.out.println("IPFS uri " + ipfsuri);
    }

    @Test
    void multiAddress_http_protocol() {
        MultiAddress ma_http = new MultiAddress("/dns6/ipfs.io/tcp/5001/http");
    }
}
