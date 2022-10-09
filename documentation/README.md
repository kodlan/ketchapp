### Modules:

- proto
- server
- cli-client
- web-client
- client - some testing code for now

## Draft microservice structure:

* for now there is no concept of user in a system 
* User service is out of scope

```
                                   +--------------+
                                   |Web/App client|
                                   +------+--+----+
                                          |  |
                                          |  |
         +---------------+                |  |
         |Event Processor+----------------+  +--------------------+--------------+
         +--+---------+--+                                        |              |
            |         |                                           |              |
            |         +-------------------+                       |              |
            |         |                   |                       |              |
+-----------+--+  +---+---------+  +------+-------------+  +------+-----+  +-----+------+
|Record Service|  |Stats Service|  |Achievements Service|  |User Service|  |Tags Service|
+------+-------+  +------+------+  +---------+----------+  +------+-----+  +------+-----+
       |                 |                   |                    |               |
       |                 |                   |                    |               |
  +----+----+       +----+---+       +-------+-------+        +---+---+       +---+---+
  |Record DB|       |Stats DB|       |Achievements DB|        |User DB|       |Tags DB|
  +---------+       +--------+       +---------------+        +-------+       +-------+
```

### EventProcessor
Processes all incoming event objects. Works as frontend 
before other services that do actual event processing:
- Record service
- Stats service
- Achievements service

#### Methods:
* `processEvent(Event event)` 

### Record Service
Store events

#### Methods:
* `storeEvent(Event event)`  
* `List<Event> Event getEvents()` # todo: add some period or whatever

### Stats Service
Update stats with each event.

#### Methods: 
* `updateStats(Event event)`
* `Stats getStats()`

### Achievements Service
Get the list of possible achievements, and track current progress

#### Methods:
* `List<Achievement> getAchivements()`
* `AchievementsProgress getAchievement(Achievement achievement)`

### User service
Handle user creation, login, etc. 

TBD

### Tags Service
Handle users tags that are used to categorize events

#### Methods:
* `addTag(Tag tag)`
* `removeTag(Tag tag)` # todo: what will happen with tagged events???
* `List<Tag> getTags()`

### Objects

Event:
* start date
* end date
* tag

Tag: 
* name

Achievement:
* name

AchievementsProgress:
* achieved



### Future considerations:
* update everything with userIds and authentication
* add getByTag method to record service
