package k.ketchapp.service.achievementservice;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.logging.Logger;
import k.ketchapp.proto.Achievement;
import k.ketchapp.proto.AchievementProgress;
import k.ketchapp.proto.AchievementServiceGrpc;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.GetAchievementProgressRequest;
import k.ketchapp.proto.GetAchievementProgressResponse;
import k.ketchapp.proto.GetAchievementRequest;
import k.ketchapp.proto.GetAchievementResponse;
import k.ketchapp.proto.UpdateAchievementsRequest;
import k.ketchapp.service.achievementservice.dao.AchievementDao;

public class AchievementService extends AchievementServiceGrpc.AchievementServiceImplBase {

  private static final Logger logger = Logger.getLogger(AchievementService.class.getName());

  private final AchievementDao achievementDao;

  public AchievementService(AchievementDao achievementDao) {
    this.achievementDao = achievementDao;
  }

  @Override
  public void getAchievements(GetAchievementRequest request, StreamObserver<GetAchievementResponse> responseObserver) {
    List<Achievement> achievements = achievementDao.getAchievements();

    GetAchievementResponse response = GetAchievementResponse.newBuilder()
        .addAllAchievement(achievements)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getAchievementProgress(GetAchievementProgressRequest request, StreamObserver<GetAchievementProgressResponse> responseObserver) {
    Achievement achievement = request.getAchievement();

    AchievementProgress achievementProgress = achievementDao.getAchievementProgress(achievement);

    GetAchievementProgressResponse response = GetAchievementProgressResponse.newBuilder()
        .setAchievementProgress(achievementProgress)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updateAchievements(UpdateAchievementsRequest request, StreamObserver<Empty> responseObserver) {
    Event event = request.getEvent();

    logger.info("updating Achievements with event = " + event);

    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }
}
