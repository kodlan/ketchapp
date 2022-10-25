package k.ketchapp.service.recordservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import k.ketchapp.proto.Event;
import k.ketchapp.proto.RecordServiceGrpc;
import k.ketchapp.proto.StoreEventRequest;
import org.junit.Rule;
import org.junit.Test;

public class RecordServerTest {

  @Rule
  public final GrpcCleanupRule grpcCleanupRule = new GrpcCleanupRule();

  @Test
  public void storeEvent() throws IOException {

    String serverName = InProcessServerBuilder.generateName();

    grpcCleanupRule.register(InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(new RecordService())
        .build()
        .start());

    Channel channel = grpcCleanupRule.register(InProcessChannelBuilder
        .forName(serverName)
        .directExecutor()
        .build());

    RecordServiceGrpc.RecordServiceBlockingStub blockingStub = RecordServiceGrpc.newBlockingStub(channel);

    Event event = Event.newBuilder()
        .build();

    StoreEventRequest storeEventRequest = StoreEventRequest.newBuilder()
        .setEvent(event)
        .build();

    Empty emptyResponse = blockingStub.storeEvent(storeEventRequest);

    assertNotNull(emptyResponse);
  }


}
