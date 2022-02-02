Feature: Collection of assets
  Creators add assets to a collection of NFT's

  Scenario:
    When I create a collection named "Graffities" with symbol "GFT"
    Then The name of new collection is "Graffities" and symbol "GFT"

  Scenario:
    Given a collection of NFT
    When I mint an asset identified with the uri "ipfs://bafybeidl/nft-image.png"
    Then the NFT has the uri "ipfs://bafybeidl/nft-image.png" in the collection

  Scenario:
    Given a collection of NFT
    When the private key "0xc87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3" mints an NFT
    Then the owner of the NFT is "0x627306090abab3a6e1400e9345bc60c78a8bef57"