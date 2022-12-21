package k.ketchapp.service.eventservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import k.ketchapp.proto.AchievementServiceGrpc;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.EventServiceGrpc;
import k.ketchapp.proto.ProcessEventRequest;
import k.ketchapp.proto.RecordServiceGrpc;
import k.ketchapp.proto.StatsServiceGrpc;
import k.ketchapp.proto.StoreEventRequest;
import k.ketchapp.proto.UpdateAchievementRequest;
import k.ketchapp.proto.UpdateStatsRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceDeadlineExceededTest {

  @Rule
  public final GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  String serverName = InProcessServerBuilder.generateName();

  private Channel channel;

  /**
   * Mock original EventService and use delegateTo to
   * change real channels with channels for in process communication.
   * This channels will be called to communicate with RecordSevice and StatsService
   */
  private final EventServiceGrpc.EventServiceImplBase mockEventService =
      mock(EventServiceGrpc.EventServiceImplBase.class, delegatesTo(
          new EventService() {

            {
              // register original channels for cleanup
              grpcCleanupRule.register(this.recordServiceChannel);
              grpcCleanupRule.register(this.statsServiceChannel);
              grpcCleanupRule.register(this.achievementServiceChannel);


              // create new channels for testing
              this.recordServiceChannel = grpcCleanupRule.register(InProcessChannelBuilder
                  .forName(serverName)
                  .directExecutor()
                  .build());
              this.statsServiceChannel = grpcCleanupRule.register(InProcessChannelBuilder
                  .forName(serverName)
                  .directExecutor()
                  .build());
              this.achievementServiceChannel = grpcCleanupRule.register(InProcessChannelBuilder
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
              if (getShouldRecordServiceError()) {
                responseObserver.onError(Status.DEADLINE_EXCEEDED.asRuntimeException());
              } else {
                responseObserver.onNext(Empty.newBuilder().build());
                responseObserver.onCompleted();
              }
            }
          }
      ));

  private boolean shouldRecordServiceError = false;

  private boolean getShouldRecordServiceError() {
    return shouldRecordServiceError;
  }

  /**
   * StatsService mock. Override storeEvent, return emtpy response and close the communication.
   * This is required to unblock gRPC communication process, otherwise it will hang waiting for response.
   */
  private final StatsServiceGrpc.StatsServiceImplBase mockStatsService =
      mock(StatsServiceGrpc.StatsServiceImplBase.class, delegatesTo(
          new StatsServiceGrpc.StatsServiceImplBase() {

            @Override
            public void updateStats(UpdateStatsRequest request, StreamObserver<Empty> responseObserver) {
              if (getShouldStatsServiceError()) {
                responseObserver.onError(Status.DEADLINE_EXCEEDED.asRuntimeException());
              } else {
                responseObserver.onNext(Empty.newBuilder().build());
                responseObserver.onCompleted();
              }
            }
          }
      ));

  private boolean shouldStatsServiceError = false;

  private boolean getShouldStatsServiceError() {
    return shouldStatsServiceError;
  }




  /**
   * AchievementService mock. Override updateAchievements, return emtpy response and close the communication.
   * This is required to unblock gRPC communication process, otherwise it will hang waiting for response.
   */
  private final AchievementServiceGrpc.AchievementServiceImplBase mockAchievementService =
      mock(AchievementServiceGrpc.AchievementServiceImplBase.class, delegatesTo(
          new AchievementServiceGrpc.AchievementServiceImplBase() {

            @Override
            public void updateAchievements(UpdateAchievementRequest request, StreamObserver<Empty> responseObserver) {
              if (getShouldAchievementServiceError()) {
                responseObserver.onError(Status.DEADLINE_EXCEEDED.asRuntimeException());
              } else {
                responseObserver.onNext(Empty.newBuilder().build());
                responseObserver.onCompleted();
              }
            }
          }
      ));

  private boolean shouldAchievementServiceError = false;

  private boolean getShouldAchievementServiceError() {
    return shouldAchievementServiceError;
  }



  private final ProcessEventRequest processEventRequest = ProcessEventRequest.newBuilder()
      .setEvent(Event.newBuilder().build())
      .build();

  @Before
  public void setUp() throws Exception {
    grpcCleanupRule.register(InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(mockEventService)
        .addService(mockRecordService)
        .addService(mockStatsService)
        .addService(mockAchievementService)
        .build()
        .start());

    channel = grpcCleanupRule.register(InProcessChannelBuilder
        .forName(serverName)
        .directExecutor()
        .build());
  }

  @Test(expected = StatusRuntimeException.class)
  public void deadlineExceededForStoreServiceTest() {
    EventServiceGrpc.EventServiceBlockingStub blockingStub = EventServiceGrpc.newBlockingStub(channel);

    shouldRecordServiceError = true; // Store service fails with deadline exceeded
    shouldStatsServiceError = false; // Stats service will not fail
    shouldAchievementServiceError = false; // Achievement service will not fail

    // Call processEvent. This will call RecordService and StatsService.
    Empty empty = blockingStub.processEvent(processEventRequest);

    assertNotNull(empty);

    // events service was called
    verify(mockEventService).processEvent(ArgumentMatchers.any(), ArgumentMatchers.any());
    // record service was called
    verify(mockRecordService).storeEvent(ArgumentMatchers.any(), ArgumentMatchers.any());
    // stats service should not get called as record service failed with deadline exceeded
    verify(mockStatsService, never()).updateStats(ArgumentMatchers.any(), ArgumentMatchers.any());
    // achievement service should not get called as record service failed with deadline exceeded
    verify(mockAchievementService, never()).updateAchievements(ArgumentMatchers.any(), ArgumentMatchers.any());
  }

  @Test(expected = StatusRuntimeException.class)
  public void deadlineExceededForStatsServiceTest() {
    EventServiceGrpc.EventServiceBlockingStub blockingStub = EventServiceGrpc.newBlockingStub(channel);

    shouldRecordServiceError = false;
    shouldStatsServiceError = true;
    shouldAchievementServiceError = false;

    // Call processEvent. This will call RecordService and StatsService.
    Empty empty = blockingStub.processEvent(processEventRequest);

    assertNotNull(empty);

    // events service was called
    verify(mockEventService).processEvent(ArgumentMatchers.any(), ArgumentMatchers.any());
    // record service was called
    verify(mockRecordService).storeEvent(ArgumentMatchers.any(), ArgumentMatchers.any());
    // stats service was called, but it will fail with deadline exceeded
    verify(mockStatsService).updateStats(ArgumentMatchers.any(), ArgumentMatchers.any());
    // achievement service should not get called as record service failed with deadline exceeded
    verify(mockAchievementService, never()).updateAchievements(ArgumentMatchers.any(), ArgumentMatchers.any());
  }


  @Test(expected = StatusRuntimeException.class)
  public void deadlineExceededForAchievementServiceTest() {
    EventServiceGrpc.EventServiceBlockingStub blockingStub = EventServiceGrpc.newBlockingStub(channel);

    shouldRecordServiceError = false;
    shouldStatsServiceError = false;
    shouldAchievementServiceError = true;

    // Call processEvent. This will call RecordService and StatsService.
    Empty empty = blockingStub.processEvent(processEventRequest);

    assertNotNull(empty);

    // events service was called
    verify(mockEventService).processEvent(ArgumentMatchers.any(), ArgumentMatchers.any());
    // record service was called
    verify(mockRecordService).storeEvent(ArgumentMatchers.any(), ArgumentMatchers.any());
    // stats service was called
    verify(mockStatsService).updateStats(ArgumentMatchers.any(), ArgumentMatchers.any());
    // achievement service was called but it will fail with deadline exceeded
    verify(mockAchievementService).updateAchievements(ArgumentMatchers.any(), ArgumentMatchers.any());
  }


  @Test
  public void deadlineExceededForNoServiceTest() {
    EventServiceGrpc.EventServiceBlockingStub blockingStub = EventServiceGrpc.newBlockingStub(channel);

    shouldRecordServiceError = false;
    shouldStatsServiceError = false;
    shouldAchievementServiceError = false;

    // Call processEvent. This will call RecordService and StatsService.
    Empty empty = blockingStub.processEvent(processEventRequest);

    assertNotNull(empty);

    // events service was called
    verify(mockEventService).processEvent(ArgumentMatchers.any(), ArgumentMatchers.any());
    // record service was called
    verify(mockRecordService).storeEvent(ArgumentMatchers.any(), ArgumentMatchers.any());
    // stats service was called
    verify(mockStatsService).updateStats(ArgumentMatchers.any(), ArgumentMatchers.any());
    // achievement service was called
    verify(mockAchievementService).updateAchievements(ArgumentMatchers.any(), ArgumentMatchers.any());
  }
}
