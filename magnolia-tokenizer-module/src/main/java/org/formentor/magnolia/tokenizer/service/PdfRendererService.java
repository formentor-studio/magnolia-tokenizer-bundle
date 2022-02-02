package org.formentor.magnolia.tokenizer.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import info.magnolia.init.MagnoliaConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
public class PdfRendererService {

    private final MagnoliaConfigurationProperties magnoliaConfigurationProperties;
    protected Integer port;

    @Inject
    public PdfRendererService(MagnoliaConfigurationProperties magnoliaConfigurationProperties) {
        this.magnoliaConfigurationProperties = magnoliaConfigurationProperties;
        port = getPort();
    }

    public CompletableFuture<File> renderPdf(Node page) {
        try {
            return renderPdf(getPageUri(page));
        } catch (URISyntaxException | RepositoryException e) {
            log.error("Errors rendering page to pdf", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private CompletableFuture renderPdf(URI uri) {
        return CompletableFuture.supplyAsync(() -> {
            File file;
            try {
                file = File.createTempFile("magnolia-tokenizer", ".pdf");
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            try (OutputStream os = new FileOutputStream(file)) {
                new PdfRendererBuilder()
                        .useFastMode()
                        .withUri(uri.toString())
                        .toStream(os)
                        .run();
                os.close();
                return file;
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }

    private URI getPageUri(Node page) throws URISyntaxException, RepositoryException {
        String webapp = magnoliaConfigurationProperties.getProperty(MagnoliaConfigurationProperties.MAGNOLIA_WEBAPP);
        webapp = webapp.equals("ROOT")?"": "/" + webapp;
        return new URI(String.format("http://%s:%s%s%s", "127.0.0.1", port, webapp, page.getPath()));
    }

    /**
     * Returns the Port of the server
     */
    private Integer getPort() {
        try {
            MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
            Set<ObjectName> objectNames;
            objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
            final Integer port = Integer.valueOf(objectNames.iterator().next().getKeyProperty("port"));

            return port;
        } catch (MalformedObjectNameException | NoSuchElementException e) {
            log.error("Errors getting port of the server, using port 80", e);
            return 80;
        }
    }
}
