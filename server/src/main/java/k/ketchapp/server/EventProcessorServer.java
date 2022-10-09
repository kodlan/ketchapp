package k.ketchapp.server;

import io.grpc.BindableService;
import java.io.IOException;
import k.ketchapp.service.eventprocessor.EventProcessor;

public class EventProcessorServer extends AbstractServer {

  @Override
  protected String getServerName() {
    return "EventProcessor";
  }

  @Override
  protected int getPort() {
    return 50007;
  }

  @Override
  protected BindableService getService() {
    return new EventProcessor();
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    EventProcessorServer eventProcessorServer = new EventProcessorServer();
    eventProcessorServer.startAndBlock();
  }
}
