package k.ketchapp.service.recordservice;

import static org.junit.Assert.assertNull;
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
import k.ketchapp.proto.RecordServiceGrpc;
import k.ketchapp.proto.StoreEventRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RecordServerTest {

  @Rule
  public final GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  private Channel channel;

  @Before
  public void setUp() throws Exception {
    String serverName = InProcessServerBuilder.generateName();

    grpcCleanupRule.register(InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(new RecordService())
        .build()
        .start());

    channel = grpcCleanupRule.register(InProcessChannelBuilder
        .forName(serverName)
        .directExecutor()
        .build());
  }

  @Test
  public void storeEvent() {
    RecordServiceGrpc.RecordServiceBlockingStub blockingStub = RecordServiceGrpc.newBlockingStub(channel);

    StoreEventRequest storeEventRequest = generateRandomRequest();

    Empty emptyResponse = blockingStub.storeEvent(storeEventRequest);

    assertNull(emptyResponse);
  }

  private static StoreEventRequest generateRandomRequest() {
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

    Event event =  Event.newBuilder()
        .setStartDate(startTimestamp)
        .setEndDate(endTimestamp)
        .build();

    return StoreEventRequest.newBuilder()
        .setEvent(event)
        .build();
  }
}
