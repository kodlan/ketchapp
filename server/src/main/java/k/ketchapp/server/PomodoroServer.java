package k.ketchapp.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import k.ketchapp.functional.ThrowingRunnable;
import k.ketchapp.service.PomodoroImpl;

public class PomodoroServer {

  private static final Logger logger = Logger.getLogger(PomodoroServer.class.getName());

  private Server server;

  public void startServer() throws IOException {
    int port = 50006;

    server = ServerBuilder
        .forPort(port)
        .addService(new PomodoroImpl())
        .build()
        .start();

    Runtime.getRuntime().addShutdownHook(
        new Thread(
            ThrowingRunnable.handleThrowingRunnable(
                PomodoroServer.this::stopServer,
                exception -> logger.log(Level.SEVERE, "Server shutdown error", exception))));

    logger.log(Level.INFO, "Server started at port " + port);
  }

  public void stopServer() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
      logger.log(Level.INFO, "Server stopped");
    }
  }

  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    PomodoroServer pomodoroServer = new PomodoroServer();

    pomodoroServer.startServer();
    pomodoroServer.blockUntilShutdown();
  }
}
