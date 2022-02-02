package org.formentor.tokenizer.domain.ethereum.contract;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.web3j.EVMTest;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@EVMTest
class CMSCollectionTest {

    @Test
    void create_collection_should_deploy_smart_contract(Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) throws Exception {
        CMSCollection cmsCollection = CMSCollection.deploy(web3j, transactionManager, gasProvider, "Lion", "L")
                .send();

        assertNotNull(cmsCollection);
    }

    @Test
    void mint_nft_should_add_asset_to_collection(Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) throws Exception {
        CMSCollection cmsCollection = CMSCollection.deploy(web3j, transactionManager, gasProvider, "Lion", "L")
                .send();
        cmsCollection.mint("ipfs//33333").send();

        final String tokenURI = cmsCollection.tokenURI(BigInteger.ONE).send();
        Assertions.assertEquals("ipfs//33333", tokenURI);
    }

    @Disabled // Integration test
    @Test
    void integration_create_collection_should_deploy_smart_contract() throws Exception {
        CMSCollection cmsCollection = CMSCollection.deploy(
                        Web3j.build(new HttpService("http://localhost:8545")),
                        Credentials.create("0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"),
                        new DefaultGasProvider(), "Lion", "L")
                .send();

        assertNotNull(cmsCollection);
    }
}
