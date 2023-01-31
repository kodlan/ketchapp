package k.ketchapp.service.achievementservice;

import io.grpc.BindableService;
import java.io.IOException;
import k.ketchapp.server.AbstractServer;
import k.ketchapp.service.achievementservice.dao.AchievementDao;

public class AchievementServer extends AbstractServer {

  @Override
  protected String getServerName() {
    return "AchievementServer";
  }

  @Override
  protected int getPort() {
    return Integer.parseInt(System.getenv().getOrDefault("PORT", "50009"));
  }

  @Override
  protected BindableService getService() {
    AchievementDao dao = new AchievementDao();
    return new AchievementService(dao);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    AchievementServer achievementServer = new AchievementServer();
    achievementServer.startAndBlock();
  }
}
