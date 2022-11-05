package k.ketchapp.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import k.ketchapp.functional.ThrowingRunnable;
import k.ketchapp.server.logging.LoggingInterceptor;

public abstract class AbstractServer {

  protected final Logger logger = Logger.getLogger(getClass().getName());

  private Server server;

  public void startAndBlock() throws IOException, InterruptedException {
    this.startServer();
    this.blockUntilShutdown();
  }

  protected abstract String getServerName();

  protected abstract int getPort();

  protected abstract BindableService getService();

  protected void startServer() throws IOException {
    int port = getPort();

    logger.log(Level.INFO, "Building a " + getServerName() + "  server...");

    BindableService service = getService();

    ServerInterceptor interceptor = new LoggingInterceptor();

    server = ServerBuilder
        .forPort(port)
        .addService(service)
        .addService(ServerInterceptors.intercept(service, interceptor))
        .build()
        .start();

    Runtime.getRuntime().addShutdownHook(
        new Thread(
            ThrowingRunnable.handleThrowingRunnable(
                AbstractServer.this::stopServer,
                exception -> logger.log(Level.SEVERE, "Server shutdown error", exception))));

    logger.log(Level.INFO, getServerName() + " server started at port " + port);
  }

  protected void stopServer() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
      logger.log(Level.INFO, "Server stopped");
    }
  }

  protected void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }
}
