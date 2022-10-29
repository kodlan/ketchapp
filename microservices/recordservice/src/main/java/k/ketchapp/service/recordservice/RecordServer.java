package k.ketchapp.service.recordservice;

import io.grpc.BindableService;
import java.io.IOException;
import k.ketchapp.server.AbstractServer;
import k.ketchapp.service.recordservice.dao.RecordDao;

public class RecordServer extends AbstractServer {

  @Override
  protected String getServerName() {
    return "RecordServer";
  }

  @Override
  protected int getPort() {
    return 50006;
  }

  @Override
  protected BindableService getService() {
    // TODO: use DI framework to inject this
    RecordDao recordDao = new RecordDao();
    return new RecordService(recordDao);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    RecordServer recordServer = new RecordServer();
    recordServer.startAndBlock();
  }
}
