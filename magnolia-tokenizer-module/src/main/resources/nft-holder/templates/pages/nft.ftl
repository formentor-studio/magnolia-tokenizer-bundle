<!DOCTYPE html>
[#assign asset = damfn.getAsset(content.asset)!]

<html xml:lang="${cmsfn.language()}" lang="${cmsfn.language()}">
  <head>
    [@cms.page /]
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    ${resfn.css(["/nft-holder/.*css"])!}
    <title>${content.title!}</title>
  </head>
  <body>
    <div class="header">
        <h1>${content.title!}</h1>
        <span class="author">${content.author!}</span>
    </div>
    <div class="asset">
    [#if asset?has_content]
        <img src="${asset.getLink()!}" />
    [/#if]
    </div>
    
    <div class="description">
        <span class="subject">${content.subject!}</span>
        <p>${content.description}</p>
    </div>
  </body>
</html>
