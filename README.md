# ketchapp
Pet project for yet another pomodoro app :) - cloud-first microservices demo application.

<b>Technologies used:</b> 
* Java
* gRPC
  * [Unit tests](https://github.com/kodlan/ketchapp/tree/master/microservices/eventservice/src/test/java/k/ketchapp/service/eventservice)
  * [Deadlines](https://github.com/kodlan/ketchapp/blob/master/microservices/eventservice/src/main/java/k/ketchapp/service/eventservice/EventService.java)
  * Error handling (TBD)
  * [Logging interceptor](https://github.com/kodlan/ketchapp/blob/master/microservices/common/src/main/java/k/ketchapp/server/logging/LoggingInterceptor.java)
  * Cancellation (TBD)
  * Health checks (TBD)
  * Tracing (Jaeger?) (TBD)
  * Metrics (TBD)
  * Load balancing (TBD)
  * Sagas (TBD)
  * mTLS (TBD)
  * Authentication (TBD)
* Docker
  * Multi-stage build ([builder image](https://github.com/kodlan/ketchapp/blob/master/Dockerfile), [RecordService image](https://github.com/kodlan/ketchapp/blob/master/microservices/recordservice/Dockerfile))

Plans include following technologies into this project:
* Go (Go microservice, Go CLI)
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

## Building Docker images manually
From the project root folder:
```
docker build -t ketchapp/app:latest .

cd microservices/recordservice
docker build -t ketchapp/recordservice:latest .

cd ../statsservice
docker build -t ketchapp/statsservice:latest .

cd ../eventservice
docker build -t ketchapp/eventservice:latest .

cd ../achievementservice
docker build -t ketchapp/achievementservice:latest .
```