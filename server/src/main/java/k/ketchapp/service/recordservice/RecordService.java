package k.ketchapp.service.recordservice;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.GetEventsResponse;
import k.ketchapp.proto.RecordServiceGrpc;
import k.ketchapp.proto.StoreEventRequest;

public class RecordService extends RecordServiceGrpc.RecordServiceImplBase {

  private static final Logger logger = Logger.getLogger(RecordService.class.getName());

  List<Event> events = new ArrayList<>();

  @Override
  public void storeEvent(StoreEventRequest request, StreamObserver<Empty> responseObserver) {
    logger.log(Level.INFO, "storing event: " + request.getEvent());

    events.add(request.getEvent());

    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void getEvents(Empty request, StreamObserver<GetEventsResponse> responseObserver) {
    logger.log(Level.INFO, "getting events ...");

    GetEventsResponse response = GetEventsResponse.newBuilder()
        .addAllEvent(events)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
