package k.ketchapp.service.statsservice.dao;

public class StatsDao {

  private int eventCounter = 0;

  public void incrementCounter() {
    eventCounter ++;
  }

  public int getEventCounter() {
    return eventCounter;
  }
}
