package k.ketchapp.service.eventprocessor;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.logging.Level;
import java.util.logging.Logger;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.EventProcessorGrpc;
import k.ketchapp.proto.ProcessEventRequest;
import k.ketchapp.proto.RecordServiceGrpc;
import k.ketchapp.proto.RecordServiceGrpc.RecordServiceBlockingStub;
import k.ketchapp.proto.StatsServiceGrpc;
import k.ketchapp.proto.StatsServiceGrpc.StatsServiceBlockingStub;
import k.ketchapp.proto.StoreEventRequest;
import k.ketchapp.proto.UpdateStatsRequest;
import k.ketchapp.service.recordservice.RecordService;

public class EventProcessor extends EventProcessorGrpc.EventProcessorImplBase {

  private static final Logger logger = Logger.getLogger(RecordService.class.getName());

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
    logger.log(Level.INFO, "Processing event: " + request.getEvent());

    Event event = request.getEvent();

    callStoreService(event);

    callStatsService(event);

    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  private void callStoreService(Event event) {
    logger.log(Level.INFO, "Storing event: " + event);
    RecordServiceBlockingStub recordServiceBlockingStub = RecordServiceGrpc.newBlockingStub(recordServiceChannel);

    StoreEventRequest storeEventRequest = StoreEventRequest.newBuilder()
        .setEvent(event)
        .build();

    //noinspection ResultOfMethodCallIgnored
    recordServiceBlockingStub.storeEvent(storeEventRequest);
  }

  private void callStatsService(Event event) {
    logger.log(Level.INFO, "Updating stats with event: " + event);
    StatsServiceBlockingStub statsServiceBlockingStub = StatsServiceGrpc.newBlockingStub(statsServiceChannel);

    UpdateStatsRequest updateStatsRequest = UpdateStatsRequest.newBuilder()
        .setEvent(event)
        .build();

    //noinspection ResultOfMethodCallIgnored
    statsServiceBlockingStub.updateStats(updateStatsRequest);
  }
}
