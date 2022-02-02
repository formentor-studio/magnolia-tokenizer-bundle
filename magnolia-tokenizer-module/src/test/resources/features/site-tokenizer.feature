Feature: Site tokenizer
  Content managers tokenizes sites
  Scenario: Tokenize site
    When I tokenize the site "travel-demo.formentor.com"
    Then It is created a token named "travel-demo.formentor.com"

  Scenario: Mint page of site
    When I mint the page "/travel-of-my-life" of the site "travel-demo.formentor.com"
    Then It is minted a NFT for the pdf of the page "travel-of-my-life"