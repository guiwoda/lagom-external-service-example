# Lagom external service example
Small example of how Lagom allows the definition of an external API and how it can be used from a Lagom service.

## Architecture
`ESCO` is an external service. the `esco-api` module defines this service through the `EscoService#descriptor`.
The `esco-gateway` service is our integration layer service. In this example, we are making a proxy-like call from the `esco-gateway` GET endpoint to one of the `ESCO` endpoints, while also doing authentication and token handling logic.

## The specifics of the demo
What's interesting about Lagom is that it produces a client to any defined service descriptor automatically. It also provides configurable circuit breakers and allows for easy-to-code middleware by decorating (or otherwise intercepting) the client calls. An example of this is the `EscoAuthenticatedClient` implementation, that wraps around the `EscoService`client provided by Lagom to guarantee a valid authentication token every time a request is issued.

## Disclaimer
This repo is not production code. It was built specifically to provide an example in an ongoing conversation and should not be used as a foundation for production applications in any way. Please read Lightbend's documentation for any and every question you may have.