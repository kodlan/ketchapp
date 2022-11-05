package k.ketchapp.server.logging;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.logging.Logger;

public class LoggingInterceptor implements ServerInterceptor {

  private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());

  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
    ServerCall<ReqT, RespT> listener = new SimpleForwardingServerCall<>(serverCall) {
      @Override
      public void sendMessage(RespT message) {
        String fullMethodName = serverCall.getMethodDescriptor().getFullMethodName();

        logger.info( fullMethodName + " sending response" + message);

        super.sendMessage(message);
      }
    };

    return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(serverCallHandler.startCall(listener, metadata)) {
      @Override
      public void onMessage(ReqT message) {
        String fullMethodName = serverCall.getMethodDescriptor().getFullMethodName();

        logger.info( fullMethodName + " method called with argument: " + message);

        super.onMessage(message);
      }
    };
  }
}
