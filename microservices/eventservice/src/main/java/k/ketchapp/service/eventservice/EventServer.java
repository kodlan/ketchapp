package k.ketchapp.service.eventservice;

import io.grpc.BindableService;
import java.io.IOException;
import k.ketchapp.server.AbstractServer;

public class EventServer extends AbstractServer {

  @Override
  protected String getServerName() {
    return "EventServer";
  }

  @Override
  protected int getPort() {
    return 50007;
  }

  @Override
  protected BindableService getService() {
    return new EventService();
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    EventServer eventServer = new EventServer();
    eventServer.startAndBlock();
  }
}
