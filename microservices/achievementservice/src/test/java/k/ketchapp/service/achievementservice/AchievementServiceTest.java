package k.ketchapp.service.achievementservice;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.util.List;
import k.ketchapp.proto.Achievement;
import k.ketchapp.proto.AchievementProgress;
import k.ketchapp.proto.AchievementServiceGrpc;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.GetAchievementProgressRequest;
import k.ketchapp.proto.GetAchievementProgressResponse;
import k.ketchapp.proto.GetAchievementRequest;
import k.ketchapp.proto.GetAchievementResponse;
import k.ketchapp.proto.UpdateAchievementRequest;
import k.ketchapp.service.achievementservice.dao.AchievementDao;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AchievementServiceTest {

  @Rule
  public final GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private Channel channel;

  @Mock
  private AchievementDao achievementDao;

  @Before
  public void setUp() throws Exception {
    String serverName = InProcessServerBuilder.generateName();

    grpcCleanupRule.register(InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(new AchievementService(achievementDao))
        .build()
        .start());

    channel = grpcCleanupRule.register(InProcessChannelBuilder
        .forName(serverName)
        .directExecutor()
        .build());
  }

  @Test
  public void getAchievementsTest() {
    AchievementServiceGrpc.AchievementServiceBlockingStub blockingStub = AchievementServiceGrpc.newBlockingStub(channel);

    List<Achievement> achievements = List.of(Achievement.newBuilder()
        .setId(1)
        .setNameId(1)
        .build());

    Mockito.when(achievementDao.getAchievements()).thenReturn(achievements);

    GetAchievementRequest request = GetAchievementRequest.newBuilder().build();

    GetAchievementResponse getAchievementResponse = blockingStub.getAchievements(request);

    Mockito.verify(achievementDao).getAchievements();
    assertNotNull(getAchievementResponse);
    assertEquals(achievements, getAchievementResponse.getAchievementList());
    assertTrue("Some additional elements present in returned list", achievements.containsAll(getAchievementResponse.getAchievementList()));
    assertTrue("Some elements are missing in the returned list", getAchievementResponse.getAchievementList().containsAll(achievements));
  }

  @Test
  public void getAchievementProgress() {
    AchievementServiceGrpc.AchievementServiceBlockingStub blockingStub = AchievementServiceGrpc.newBlockingStub(channel);

    Achievement achievement = Achievement.newBuilder()
        .setId(1)
        .build();

    GetAchievementProgressRequest request = GetAchievementProgressRequest.newBuilder()
        .setAchievement(achievement)
        .build();

    AchievementProgress progress = AchievementProgress.newBuilder()
        .setProgress(65)
        .setCompleted(false)
        .build();

    Mockito.when(achievementDao.getAchievementProgress(achievement)).thenReturn(progress);

    GetAchievementProgressResponse getAchievementProgressResponse = blockingStub.getAchievementProgress(request);

    Mockito.verify(achievementDao).getAchievementProgress(achievement);
    assertNotNull(getAchievementProgressResponse);
    assertEquals(progress, getAchievementProgressResponse.getAchievementProgress());
  }

  @Test
  public void updateAchievementsTest() {
    AchievementServiceGrpc.AchievementServiceBlockingStub blockingStub = AchievementServiceGrpc.newBlockingStub(channel);

    Event event = Event.newBuilder().build();

    UpdateAchievementRequest request = UpdateAchievementRequest.newBuilder()
        .setEvent(event)
        .build();

    Empty empty = blockingStub.updateAchievements(request);

    Mockito.verify(achievementDao).updateAchievement(event);
    assertNotNull(empty);
  }
}