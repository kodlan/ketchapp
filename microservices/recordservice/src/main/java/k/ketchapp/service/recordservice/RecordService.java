package k.ketchapp.service.recordservice;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.GetEventsResponse;
import k.ketchapp.proto.RecordServiceGrpc;
import k.ketchapp.proto.StoreEventRequest;
import k.ketchapp.service.recordservice.dao.RecordDao;

public class RecordService extends RecordServiceGrpc.RecordServiceImplBase {

  private static final Logger logger = Logger.getLogger(RecordService.class.getName());

  private final RecordDao recordDao;

  public RecordService(RecordDao recordDao) {
    this.recordDao = recordDao;
  }

  @Override
  public void storeEvent(StoreEventRequest request, StreamObserver<Empty> responseObserver) {
    logger.log(Level.INFO, "storing event: " + request.getEvent());

    recordDao.saveEvent(request.getEvent());

    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void getEvents(Empty request, StreamObserver<GetEventsResponse> responseObserver) {
    logger.log(Level.INFO, "getting events ...");

    List<Event> eventList = recordDao.getEvents();

    GetEventsResponse response = GetEventsResponse.newBuilder()
        .addAllEvent(eventList)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
