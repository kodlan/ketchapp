package k.ketchapp.service.statsservice;

import io.grpc.BindableService;
import java.io.IOException;
import k.ketchapp.server.AbstractServer;

public class StatsServer extends AbstractServer {

  @Override
  protected String getServerName() {
    return "StatsServer";
  }

  @Override
  protected int getPort() {
    return 50008;
  }

  @Override
  protected BindableService getService() {
    return new StatsService();
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    StatsServer statsServer = new StatsServer();
    statsServer.startAndBlock();
  }
}
