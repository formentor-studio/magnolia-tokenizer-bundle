package org.formentor.tokenizer.domain.ethereum.contract;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;

public interface WithTransactionManagerProvider {
    default TransactionManager getTransactionManager(Web3j web3j, Credentials credentials) {
        return new FastRawTransactionManager(
                web3j,
                credentials,
                new PollingTransactionReceiptProcessor(
                        web3j,
                        1000,
                        30));
    }
}
