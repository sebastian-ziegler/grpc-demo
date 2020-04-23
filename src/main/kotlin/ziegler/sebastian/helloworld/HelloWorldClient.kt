package ziegler.sebastian.helloworld

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.*
import java.io.Closeable
import java.util.concurrent.TimeUnit

class HelloWorldClient constructor(
    private val channel: ManagedChannel
) : Closeable {

    private val stub: GreeterGrpcKt.GreeterCoroutineStub = GreeterGrpcKt.GreeterCoroutineStub(channel)

    suspend fun greet(name: String) = coroutineScope {
        val request = HelloRequest.newBuilder().setName(name).build()
        val response = async { stub.sayHello(request) }
        println("Received: ${response.await().message}")
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

fun main(args: Array<String>): Unit = runBlocking {
    val port = 50051

    val client = HelloWorldClient(
        ManagedChannelBuilder.forAddress("localhost", port)
            .usePlaintext()
            .executor(Dispatchers.Default.asExecutor())
            .build()
    )
    val user = args.singleOrNull() ?: "w00t"
    client.greet(user)
}