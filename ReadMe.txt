HttpRestServer - Based on NIOTcpServer
======================================

Note: Compile with Java 8. Run on Java 8 or above.

Notes:
    1. Primitive implementation just as an example on top of NIOTcpServer.
    2. Supports RequestParam and RequestBody annotations only (covers most of the use-cases).
    3. Only json media type supported.
    4. Skips the overhead of servlet api in spring mvc based apps and directly delegates requests
        to rest controllers.
    5. Uses spring api to scan rest controllers.
    6. Packages to scan must be defined in server.properties
        server.properties must be in classpath.

Examples:
    src/test/java/ramana/example/httprestserver/example
