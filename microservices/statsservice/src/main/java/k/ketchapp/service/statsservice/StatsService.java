package k.ketchapp.service.statsservice;


import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.logging.Level;
import java.util.logging.Logger;
import k.ketchapp.proto.EventCount;
import k.ketchapp.proto.GetStatsRequest;
import k.ketchapp.proto.GetStatsResponse;
import k.ketchapp.proto.StatsServiceGrpc;
import k.ketchapp.proto.UpdateStatsRequest;

public class StatsService extends StatsServiceGrpc.StatsServiceImplBase {

  private static final Logger logger = Logger.getLogger(StatsService.class.getName());

  private int eventCounter = 0;

  @Override
  public void updateStats(UpdateStatsRequest request, StreamObserver<Empty> responseObserver) {
    logger.log(Level.INFO, "Updating stats with event: " + request.getEvent());

    eventCounter ++;

    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void getStats(GetStatsRequest request, StreamObserver<GetStatsResponse> responseObserver) {
    logger.log(Level.INFO, "Getting stats");

    EventCount eventCount = EventCount.newBuilder()
        .setCount(eventCounter)
        .build();

    GetStatsResponse response = GetStatsResponse.newBuilder()
        .setEventCount(eventCount)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
