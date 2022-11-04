package k.ketchapp.service.statsservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.Channel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.GetStatsRequest;
import k.ketchapp.proto.GetStatsResponse;
import k.ketchapp.proto.StatsServiceGrpc;
import k.ketchapp.proto.UpdateStatsRequest;
import k.ketchapp.service.statsservice.dao.StatsDao;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StatsServiceTest {

  @Rule
  public final GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private Channel channel;

  @Mock
  private StatsDao statsDao;

  @Before
  public void setUp() throws Exception {
    String serverName = InProcessServerBuilder.generateName();

    grpcCleanupRule.register(InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(new StatsService(statsDao))
        .build()
        .start());

    channel = grpcCleanupRule.register(InProcessChannelBuilder
        .forName(serverName)
        .directExecutor()
        .build());
  }

  @Test
  public void storeEventTest() {
    StatsServiceGrpc.StatsServiceBlockingStub blockingStub = StatsServiceGrpc.newBlockingStub(channel);

    UpdateStatsRequest updateStatsRequest = generateRandomRequest();

    Empty emptyResponse = blockingStub.updateStats(updateStatsRequest);

    Mockito.verify(statsDao).incrementCounter();
    assertNotNull(emptyResponse);
  }

  @Test
  public void getEventsTest() {
    StatsServiceGrpc.StatsServiceBlockingStub blockingStub = StatsServiceGrpc.newBlockingStub(channel);

    Mockito.when(statsDao.getEventCounter()).thenReturn(25);

    GetStatsRequest getStatsRequest = GetStatsRequest.newBuilder().build();

    GetStatsResponse getStatsResponse = blockingStub.getStats(getStatsRequest);

    Mockito.verify(statsDao).getEventCounter();
    assertNotNull(getStatsResponse);
    assertNotNull(getStatsResponse.getEventCount());
    assertEquals(25, getStatsResponse.getEventCount().getCount(), "Event count doesn't match");
  }

  private static Event generateRandomEvent() {
    Random rnd = new Random();

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime start = now.minusHours(rnd.nextInt() % 10);
    LocalDateTime end = start.plusMinutes(10 + rnd.nextInt() % 49);

    Instant startInstant = start.toInstant(ZoneOffset.UTC);
    Instant endInstant = end.toInstant(ZoneOffset.UTC);

    Timestamp startTimestamp = Timestamp.newBuilder()
        .setSeconds(startInstant.getEpochSecond())
        .setNanos(startInstant.getNano())
        .build();

    Timestamp endTimestamp = Timestamp.newBuilder()
        .setSeconds(endInstant.getEpochSecond())
        .setNanos(endInstant.getNano())
        .build();

    return Event.newBuilder()
        .setStartDate(startTimestamp)
        .setEndDate(endTimestamp)
        .build();
  }

  private static UpdateStatsRequest generateRandomRequest() {
    return UpdateStatsRequest.newBuilder()
        .setEvent(generateRandomEvent())
        .build();
  }
}