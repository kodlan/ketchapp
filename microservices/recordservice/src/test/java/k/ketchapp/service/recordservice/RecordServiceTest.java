package k.ketchapp.service.recordservice;


import static org.junit.Assert.assertTrue;
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
import java.util.List;
import java.util.Random;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.GetEventsResponse;
import k.ketchapp.proto.RecordServiceGrpc;
import k.ketchapp.proto.StoreEventRequest;
import k.ketchapp.service.recordservice.dao.RecordDao;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RecordServiceTest {

  @Rule
  public final GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private Channel channel;

  @Mock
  private RecordDao recordDao;

  @Before
  public void setUp() throws Exception {
    String serverName = InProcessServerBuilder.generateName();

    grpcCleanupRule.register(InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(new RecordService(recordDao))
        .build()
        .start());

    channel = grpcCleanupRule.register(InProcessChannelBuilder
        .forName(serverName)
        .directExecutor()
        .build());
  }

  @Test
  public void storeEventTest() {
    RecordServiceGrpc.RecordServiceBlockingStub blockingStub = RecordServiceGrpc.newBlockingStub(channel);

    StoreEventRequest storeEventRequest = generateRandomRequest();

    Empty emptyResponse = blockingStub.storeEvent(storeEventRequest);

    Mockito.verify(recordDao).saveEvent(storeEventRequest.getEvent());
    assertNotNull(emptyResponse);
  }

  @Test
  public void getEventsTest() {
    RecordServiceGrpc.RecordServiceBlockingStub blockingStub = RecordServiceGrpc.newBlockingStub(channel);

    List<Event> eventList = List.of(generateRandomEvent(), generateRandomEvent(), generateRandomEvent());
    Mockito.when(recordDao.getEvents()).thenReturn(eventList);

    GetEventsResponse getEventsResponse = blockingStub.getEvents(Empty.newBuilder().build());

    Mockito.verify(recordDao).getEvents();
    assertNotNull(getEventsResponse);
    assertEquals(eventList, getEventsResponse.getEventList());
    assertTrue("Some additional elements present in returned list", eventList.containsAll(getEventsResponse.getEventList()));
    assertTrue("Some elements are missing in the returned list", getEventsResponse.getEventList().containsAll(eventList));
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

  private static StoreEventRequest generateRandomRequest() {
    return StoreEventRequest.newBuilder()
        .setEvent(generateRandomEvent())
        .build();
  }
}
