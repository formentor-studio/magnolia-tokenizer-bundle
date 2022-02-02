// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

import "./ERC721.sol";
import "./Counters.sol";

contract CMSCollection is ERC721 {

    /// @dev This emits when an NFT is minted.
    event Minted(uint256 indexed _tokenId, string uri);

    using Counters for Counters.Counter;
    Counters.Counter private _tokenIds;

    constructor(string memory name_, string memory symbol_) ERC721(name_, symbol_) {
    }

    function mint(string memory uri) public {
        _tokenIds.increment();
        uint256 id = _tokenIds.current();
        _mint(msg.sender, id);
        _setTokenUri(id, uri);

        emit Minted(id, uri);
    }
}