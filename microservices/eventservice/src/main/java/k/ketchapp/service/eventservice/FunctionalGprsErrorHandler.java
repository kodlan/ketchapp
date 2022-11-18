package k.ketchapp.service.eventservice;

import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.util.Map;
import java.util.function.Consumer;

@FunctionalInterface
interface FunctionalGprsErrorHandler {
  void call() throws StatusRuntimeException;

  @SuppressWarnings("UnusedReturnValue")
  default FunctionalGprsErrorHandler andThenCall(FunctionalGprsErrorHandler handler, Map<Code, Runnable> codeToHandlerMap,
      Consumer<StatusRuntimeException> commonHandler, Consumer<StatusRuntimeException> handlerNotFoundAction) {
    return handleGrpcError(handler, codeToHandlerMap, commonHandler, handlerNotFoundAction);
  }

  static FunctionalGprsErrorHandler handleGrpcError(FunctionalGprsErrorHandler handler, Map<Code, Runnable> codeToHandlerMap,
      Consumer<StatusRuntimeException> commonHandler, Consumer<StatusRuntimeException> handlerNotFoundAction) {

    try {
      handler.call();
    } catch (StatusRuntimeException exception) {
      Runnable codeHandler = codeToHandlerMap.get(exception.getStatus().getCode());

      if (codeHandler != null) {
        codeHandler.run();
        commonHandler.accept(exception);
        return new NoAndThenGrpcErrorHandler();
      } else {
        handlerNotFoundAction.accept(exception);
        return new NoAndThenGrpcErrorHandler();
      }
    }

    return handler;
  }

  class NoAndThenGrpcErrorHandler implements FunctionalGprsErrorHandler {

    @Override
    public void call() throws StatusRuntimeException {
      // do nothing
    }

    @Override
    public FunctionalGprsErrorHandler andThenCall(FunctionalGprsErrorHandler handler, Map<Code, Runnable> codeToHandlerMap,
        Consumer<StatusRuntimeException> commonHandler,
        Consumer<StatusRuntimeException> handlerNotFoundAction) {
      // do nothing
      return new NoAndThenGrpcErrorHandler();
    }
  }
}