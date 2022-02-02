package org.formentor.tokenizer.domain.ethereum.contract;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionStepdefs implements WithWeb3jProvider, WithTransactionManagerProvider {

    private Credentials credentials;
    private Web3j web3j;
    private TransactionManager transactionManager;
    private ContractGasProvider gasProvider;
    private CMSCollection collection;
    private CMSCollection collectionMinted;

    @Before
    public void initWeb3j() {
        credentials = Credentials.create("0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63");
        web3j = getWeb3j(credentials.getAddress());
        transactionManager = getTransactionManager(web3j, credentials);
        gasProvider = new DefaultGasProvider();
    }

    @When("I create a collection named {string} with symbol {string}")
    public void i_create_collection_named_with_symbol(String _name, String _symbol) throws Exception {
        collection = CMSCollection.deploy(web3j, transactionManager, gasProvider, _name, _symbol)
                .send();
    }

    @Then("The name of new collection is {string} and symbol {string}")
    public void the_name_of_new_collection_is_and_symbol(String expectedName, String expectedSymbol) throws Exception {
        String actualName = collection.name().send();
        String actualSymbol = collection.symbol().send();

        assertEquals(expectedName, actualName);
        assertEquals(expectedSymbol, actualSymbol);
    }

    @Given("a collection of NFT")
    public void a_collection_of_nft() throws Exception {
        collection = CMSCollection.deploy(web3j, transactionManager, gasProvider, "collection", "symbol")
                .send();
    }

    @When("I mint an asset identified with the uri {string}")
    public void i_mint_an_asset_identified_with_the_uri(String uri) throws Exception {
        TransactionReceipt receipt = collection.mint(uri).send();
        List<CMSCollection.MintedEventResponse> minted = collection.getMintedEvents(receipt);
    }

    @Then("the NFT has the uri {string} in the collection")
    public void the_nft_has_the_uri_in_the_collection(String expectedUri) throws Exception {
        final String actualUri = collection.tokenURI(BigInteger.ONE).send();
        Assertions.assertEquals(expectedUri, actualUri);
    }

    @When("the private key {string} mints an NFT")
    public void the_private_key_mints_an_nft(String privateKey) throws Exception {
        TransactionManager transactionManager_ = getTransactionManager(web3j, Credentials.create(privateKey));
        collectionMinted = CMSCollection.load(collection.getContractAddress(), web3j, transactionManager_, gasProvider);
        collectionMinted.mint("ipfs://").send();
    }

    @Then("the owner of the NFT is {string}")
    public void the_owner_of_the_nft_is(String expectedOwner) throws Exception {
        String actualOwner = collectionMinted.ownerOf(BigInteger.ONE).send();
        assertEquals(expectedOwner, actualOwner);
    }
}
