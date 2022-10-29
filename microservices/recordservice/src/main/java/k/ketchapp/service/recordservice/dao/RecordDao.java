package k.ketchapp.service.recordservice.dao;

import java.util.ArrayList;
import java.util.List;
import k.ketchapp.proto.Event;

public class RecordDao {

  private List<Event> events = new ArrayList<>();

  public void saveEvent(Event event) {
    this.events.add(event);
  }

  public List<Event> getEvents() {
    return this.events;
  }
}
