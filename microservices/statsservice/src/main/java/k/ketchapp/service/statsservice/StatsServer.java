package k.ketchapp.service.statsservice;

import io.grpc.BindableService;
import java.io.IOException;
import k.ketchapp.server.AbstractServer;
import k.ketchapp.service.statsservice.dao.StatsDao;

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
    // TODO: use DI framework to inject this
    StatsDao statsDao = new StatsDao();
    return new StatsService(statsDao);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    StatsServer statsServer = new StatsServer();
    statsServer.startAndBlock();
  }
}
