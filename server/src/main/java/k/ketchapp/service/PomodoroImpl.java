package k.ketchapp.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.GetEventsResponse;
import k.ketchapp.proto.PomodoroGrpc;

public class PomodoroImpl extends PomodoroGrpc.PomodoroImplBase {

  List<Event> events = new ArrayList<>();

  @Override
  public void recordEvent(Event request, StreamObserver<Empty> responseObserver) {
    events.add(request);
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void getEvents(Empty request, StreamObserver<GetEventsResponse> responseObserver) {
    GetEventsResponse response = GetEventsResponse.newBuilder()
        .addAllEvent(events)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
