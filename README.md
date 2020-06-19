[![](https://jitpack.io/v/psychogen-labs/spring-rest-over-ws.svg)](https://jitpack.io/#psychogen-labs/spring-rest-over-ws)

# Rest Over Websocket | Springboot starter
ROW (Rest Over Websocket) | Spring boot websocket that wrapps rest apis

## Description
As the name suggests, ROW creates a websocket endpoint and provides json based protocol that wraps springboot rest controllers. Therefore, with a simple configuration and some annotations, your application will be served on websocket alongside already defined http endpoints.

## Documentations
Everything you need to know is covered in [wiki pages](https://github.com/psychogen-labs/spring-rest-over-ws/wiki).

## Demo
A demo of this project is available [here](https://github.com/psychogen-labs/row-demo).

## TODO

- [x] Filter chain for request handling (So developers can add their own filters)
- [X] Add support for spring security and `@PreAuthorize` annotations
- [X] Create row context so current user information is accessible
- [ ] Add metrics
- [X] Add annotation support to register filters more easily
- [ ] Add publisher and subscriber structure
- [ ] Add a way to set order filters
