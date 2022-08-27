package k.ketchapp.client;

import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Date;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.PomodoroGrpc;
import k.ketchapp.proto.PomodoroGrpc.PomodoroBlockingStub;

public class PomodoroClient {

  private static void recordEvent(ManagedChannel channel) {
    PomodoroBlockingStub pomodoroStub = PomodoroGrpc.newBlockingStub(channel);
  }

  public static void main(String[] args) {
    ManagedChannel channel = ManagedChannelBuilder
        .forAddress("localhost", 50006)
        .build();

    channel.shutdown();
  }
}
