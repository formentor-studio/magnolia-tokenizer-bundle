package org.formentor.tokenizer.domain.ethereum.contract;

import org.web3j.NodeType;
import org.web3j.container.GenericService;
import org.web3j.container.ServiceBuilder;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Async;

public interface WithWeb3jProvider {

    default Web3j getWeb3j(String address) {
        ServiceBuilder serviceBuilder = new ServiceBuilder();
        GenericService service = serviceBuilder
                .type(NodeType.EMBEDDED)
                .version("latest")
                .withGenesis("dev")
                .withSelfAddress(address)
                .withServicePort(8545)
                .build();
        return Web3j.build(service.startService(), 500, Async.defaultExecutorService());
    }

}
