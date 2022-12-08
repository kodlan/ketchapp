package k.ketchapp.service.achievementservice;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.grpc.Channel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.util.List;
import k.ketchapp.proto.Achievement;
import k.ketchapp.proto.AchievementServiceGrpc;
import k.ketchapp.proto.GetAchievementRequest;
import k.ketchapp.proto.GetAchievementResponse;
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
  public void getEventsTest() {
    AchievementServiceGrpc.AchievementServiceBlockingStub blockingStub = AchievementServiceGrpc.newBlockingStub(channel);

    List<Achievement> achievements = List.of(Achievement.newBuilder()
        .setId(1)
        .setName("test")
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
}
