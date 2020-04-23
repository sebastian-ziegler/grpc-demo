package ziegler.sebastian.helloworld

import io.grpc.Server
import io.grpc.ServerBuilder
import java.io.Closeable

class HelloWorldServer constructor(
    private val port: Int
) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(HelloWorldService())
        .build()

    fun start() {
        server.start()
        println("Server started, listening port: $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("Shutting down gRPC server. JVM was terminated")
                this@HelloWorldServer.stop()
                println("Server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class HelloWorldService: GreeterGrpcKt.GreeterCoroutineImplBase() {
        override suspend fun sayHello(request: HelloRequest): HelloReply = HelloReply
            .newBuilder()
            .setMessage("Hey ${request.name}, welcome!")
            .build()
    }
}

fun main(args: Array<String>) {
    val port = 50051
    val server = HelloWorldServer(port)
    server.start()
    server.blockUntilShutdown()
}