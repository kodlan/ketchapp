# ketchapp
Pet project for yet another pomodoro app :) - cloud-first microservices demo application.

<b>Technologies used:</b> Java, gRPC

Plans include following technologies into this project:
* Kubernetes
* Skaffold or Helm
* Istio
* Monitoring (Stackdriver?)

## Architecture (planned, work in progress)
```
                                      +--------------+
                                      |Web/App client|
                                      +------+--+----+
                                             |  |
                                             |  |
            +---------------+                |  |
            |Event Service  +----------------+  +--------------------+--------------+
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
See [this README.md](./documentation/) for details.
