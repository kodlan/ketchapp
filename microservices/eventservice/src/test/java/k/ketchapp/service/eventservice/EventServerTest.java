package k.ketchapp.service.eventservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.Channel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.EventProcessorGrpc;
import k.ketchapp.proto.ProcessEventRequest;
import k.ketchapp.proto.RecordServiceGrpc;
import k.ketchapp.proto.StatsServiceGrpc;
import k.ketchapp.proto.StoreEventRequest;
import k.ketchapp.proto.UpdateStatsRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventServerTest {

  @Rule
  public final GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  String serverName = InProcessServerBuilder.generateName();

  private Channel channel;

  /**
   * Mock original EventService and use delegateTo to
   * change real channels with channels for in process communication.
   * This channels will be called to communicate with RecordSevice and StatsService
   */
  private final EventProcessorGrpc.EventProcessorImplBase mockEventService =
      mock(EventProcessorGrpc.EventProcessorImplBase.class, delegatesTo(
          new EventService() {

            {
              // register original channels for cleanup
              grpcCleanupRule.register(this.recordServiceChannel);
              grpcCleanupRule.register(this.statsServiceChannel);

              // create new channels for testing
              this.recordServiceChannel = grpcCleanupRule.register(InProcessChannelBuilder
                  .forName(serverName)
                  .directExecutor()
                  .build());
              this.statsServiceChannel = grpcCleanupRule.register(InProcessChannelBuilder
                  .forName(serverName)
                  .directExecutor()
                  .build());
            }

          }));

  /**
   * RecordService mock. Override storeEvent, return emtpy response and close the communication.
   * This is required to unblock gRPC communication process, otherwise it will hang waiting for response.
   */
  private final RecordServiceGrpc.RecordServiceImplBase mockRecordService =
      mock(RecordServiceGrpc.RecordServiceImplBase.class, delegatesTo(
          new RecordServiceGrpc.RecordServiceImplBase() {

            @Override
            public void storeEvent(StoreEventRequest request, StreamObserver<Empty> responseObserver) {
              responseObserver.onNext(Empty.newBuilder().build());
              responseObserver.onCompleted();
            }
          }
      ));

  /**
   * StatsService mock. Override storeEvent, return emtpy response and close the communication.
   * This is required to unblock gRPC communication process, otherwise it will hang waiting for response.
   */
  private final StatsServiceGrpc.StatsServiceImplBase mockStatsService =
      mock(StatsServiceGrpc.StatsServiceImplBase.class, delegatesTo(
          new StatsServiceGrpc.StatsServiceImplBase() {

            @Override
            public void updateStats(UpdateStatsRequest request, StreamObserver<Empty> responseObserver) {
              responseObserver.onNext(Empty.newBuilder().build());
              responseObserver.onCompleted();
            }
          }
      ));


  @Before
  public void setUp() throws Exception {
    grpcCleanupRule.register(InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(mockEventService)
        .addService(mockRecordService)
        .addService(mockStatsService)
        .build()
        .start());

    channel = grpcCleanupRule.register(InProcessChannelBuilder
        .forName(serverName)
        .directExecutor()
        .build());
  }

  @Test
  public void processEventTest() {
    EventProcessorGrpc.EventProcessorBlockingStub blockingStub = EventProcessorGrpc.newBlockingStub(channel);

    Event event = generateRandomEvent();

    ProcessEventRequest request = ProcessEventRequest.newBuilder()
        .setEvent(event)
        .build();

    // Call processEvent. This will call RecordService and StatsService.
    Empty empty = blockingStub.processEvent(request);

    assertNotNull(empty);

    // verify that RecordService was called with correct event value
    ArgumentCaptor<StoreEventRequest> storeEventRequestArgumentCaptor = ArgumentCaptor.forClass(StoreEventRequest.class);
    verify(mockRecordService).storeEvent(storeEventRequestArgumentCaptor.capture(), ArgumentMatchers.any());
    assertEquals(event, storeEventRequestArgumentCaptor.getValue().getEvent());

    // verify that StatsService was called with correct event value
    ArgumentCaptor<UpdateStatsRequest> updateStatsRequestArgumentCaptor = ArgumentCaptor.forClass(UpdateStatsRequest.class);
    verify(mockStatsService).updateStats(updateStatsRequestArgumentCaptor.capture(), ArgumentMatchers.any());
    assertEquals(event, updateStatsRequestArgumentCaptor.getValue().getEvent());
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

}
