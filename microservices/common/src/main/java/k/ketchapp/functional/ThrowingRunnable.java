package k.ketchapp.functional;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {

  void run() throws E;

  static <E extends Exception> Runnable handleThrowingRunnable(
      ThrowingRunnable<E> throwingRunnable, Consumer<Exception> handler) {

    return () -> {
      try {
        throwingRunnable.run();
      } catch (Exception ex) {
        handler.accept(ex);
      }
    };
  }
}
