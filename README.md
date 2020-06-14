[![](https://jitpack.io/v/psychogen-labs/spring-rest-over-ws.svg)](https://jitpack.io/#psychogen-labs/spring-rest-over-ws)

# Rest Over WS | Springboot starter
ROW (Rest Over Websocket) | Spring boot websocket that wrapps rest apis

## Description
As the name suggests, ROW creates an websocket endpoint and provides json based protocol that wraps springboot rest controllers. Therefore, with a simple configuration and some annotations, your application will be served on websocket alongside already defined http endpoints.


## TODO

- [x] Filter chain for request handling (So developers can add their own filters)
- [ ] Session authentication to work with `@PreAuthorize`
- [ ] Pass current user session to controller methods as argument if an annotation is present
- [ ] Add metrics
