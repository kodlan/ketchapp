package k.ketchapp.client;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.GetEventsResponse;
import k.ketchapp.proto.PomodoroGrpc;
import k.ketchapp.proto.PomodoroGrpc.PomodoroBlockingStub;

public class PomodoroClient {

  private static final Random rnd = new Random();

  private static Event generateRandomEvent() {
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

  private static void recordEvents(PomodoroBlockingStub pomodoroStub) {
    for (int i = 0; i < 20; i ++) {
      pomodoroStub.recordEvent(generateRandomEvent());
    }
  }

  private static void getAllRecordedEvents(PomodoroBlockingStub pomodoroStub) {
    GetEventsResponse eventsResponse = pomodoroStub.getEvents(Empty.newBuilder().build());
    printEvents(eventsResponse);
  }

  private static void printEvents(GetEventsResponse response) {
    int count = 0;

    for (Event event : response.getEventList()) {
      System.out.println("Event #" + count + " " + event.getStartDate() + " - " + event.getEndDate());
    }
  }

  public static void main(String[] args) {
    ManagedChannel channel = ManagedChannelBuilder
        .forAddress("localhost", 50006)
        .usePlaintext()
        .build();

    PomodoroBlockingStub pomodoroStub = PomodoroGrpc.newBlockingStub(channel);

    recordEvents(pomodoroStub);

    getAllRecordedEvents(pomodoroStub);

    channel.shutdown();
  }
}
