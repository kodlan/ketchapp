package k.ketchapp.service.eventservice;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status.Code;
import io.grpc.stub.StreamObserver;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.EventServiceGrpc;
import k.ketchapp.proto.ProcessEventRequest;
import k.ketchapp.proto.RecordServiceGrpc;
import k.ketchapp.proto.RecordServiceGrpc.RecordServiceBlockingStub;
import k.ketchapp.proto.StatsServiceGrpc;
import k.ketchapp.proto.StatsServiceGrpc.StatsServiceBlockingStub;
import k.ketchapp.proto.StoreEventRequest;
import k.ketchapp.proto.UpdateStatsRequest;

public class EventService extends EventServiceGrpc.EventServiceImplBase {

  private static final int DEADLINE_MS = 2 * 1000;
  private static final Logger logger = Logger.getLogger(EventService.class.getName());

  // TODO: when to close this channel? what is lifecycle of this object?
  ManagedChannel recordServiceChannel = ManagedChannelBuilder
      .forAddress("localhost", 50006)
      .usePlaintext()
      .build();

  ManagedChannel statsServiceChannel = ManagedChannelBuilder
      .forAddress("localhost", 50008)
      .usePlaintext()
      .build();

  @Override
  public void processEvent(ProcessEventRequest request, StreamObserver<Empty> responseObserver) {
    Event event = request.getEvent();

    FunctionalGprsErrorHandler.handleGrpcError(

        () -> callStoreService(event),

        Map.of(
            Code.DEADLINE_EXCEEDED, () -> logger.info("callStoreService() call failed with DEADLINE_EXCEEDED"),
            Code.ABORTED, () -> logger.info("callStoreService() call failed with ABORTED")
        ),
        responseObserver::onError,
        (exception) -> logger.info("Unspecified error from callStoreService(): " + exception)

    ).andThenCall(

        () -> callStatsService(event),

        Map.of(
            Code.DEADLINE_EXCEEDED, () -> logger.info("callStatsService() call failed with DEADLINE_EXCEEDED"),
            Code.ABORTED, () -> logger.info("callStatsService() call failed with ABORTED")
        ),
        responseObserver::onError,
        (exception) -> logger.info("Unspecified error from callStatsService(): " + exception)

    );

    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  private void callStoreService(Event event) {
    logger.info("Calling RecordService with event: " + event);
    RecordServiceBlockingStub recordServiceBlockingStub = RecordServiceGrpc.newBlockingStub(recordServiceChannel);

    StoreEventRequest storeEventRequest = StoreEventRequest.newBuilder()
        .setEvent(event)
        .build();

    //noinspection ResultOfMethodCallIgnored
    recordServiceBlockingStub
        .withDeadlineAfter(DEADLINE_MS, TimeUnit.MILLISECONDS)
        .storeEvent(storeEventRequest);
  }

  private void callStatsService(Event event) {
    logger.info("Calling StatsService with event: " + event);
    StatsServiceBlockingStub statsServiceBlockingStub = StatsServiceGrpc.newBlockingStub(statsServiceChannel);

    UpdateStatsRequest updateStatsRequest = UpdateStatsRequest.newBuilder()
        .setEvent(event)
        .build();

    //noinspection ResultOfMethodCallIgnored
    statsServiceBlockingStub
        .withDeadlineAfter(DEADLINE_MS, TimeUnit.MILLISECONDS)
        .updateStats(updateStatsRequest);
  }
}
