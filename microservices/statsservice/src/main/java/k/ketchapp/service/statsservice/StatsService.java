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
import k.ketchapp.service.statsservice.dao.StatsDao;

public class StatsService extends StatsServiceGrpc.StatsServiceImplBase {

  private static final Logger logger = Logger.getLogger(StatsService.class.getName());

  private StatsDao statsDao;

  public StatsService(StatsDao statsDao) {
    this.statsDao = statsDao;
  }

  @Override
  public void updateStats(UpdateStatsRequest request, StreamObserver<Empty> responseObserver) {
    logger.log(Level.INFO, "Updating stats with event: " + request.getEvent());

    statsDao.incrementCounter();

    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void getStats(GetStatsRequest request, StreamObserver<GetStatsResponse> responseObserver) {
    logger.log(Level.INFO, "Getting stats");

    int events = statsDao.getEventCounter();

    EventCount eventCount = EventCount.newBuilder()
        .setCount(events)
        .build();

    GetStatsResponse response = GetStatsResponse.newBuilder()
        .setEventCount(eventCount)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
