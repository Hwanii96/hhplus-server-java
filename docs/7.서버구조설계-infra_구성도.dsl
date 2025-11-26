workspace "Concert Reservation Service" "콘서트 예약 서비스 인프라 구성도" {

  !identifiers hierarchical

  model {

    // ─────────────────────────
    // 사람
    // ─────────────────────────
    user = person "User" "콘서트 좌석 예약 및 결제를 수행하는 사용자"

    // ─────────────────────────
    // 소프트웨어 시스템 + 컨테이너들
    // ─────────────────────────
    system = softwareSystem "Concert Reservation Service" "좌석 예약 대기열 + 예약 + 충전 및 결제 서비스" {

      // 클라이언트 / 서버 / 백그라운드 (배치)
      web    = container "Web Client"                       "UI/UX 미정"                                                                                           "Web / Browser"
      api    = container "Concert Reservation Service API"  "Auth / Concerts / Queue / Reservations / Points / Payments API를 제공하는 Spring Boot Application"    "Java / Spring Boot"
      worker = container "Background Worker"                "대기열 토큰의 상태 변경 및 좌석 임시 배정 만료 처리 등의 비동기 작업을 수행"                          "Java / Spring Boot"

      // 인프라 컨테이너
      redis        = container "Redis"                     "대기열 상태 / 순번 캐시, 좌석 분산 락 관리에 사용하는 인메모리 데이터 스토어"                          "Redis"
      mq           = container "Message Queue (RabbitMQ)"  "대기열 및 예약 관련 이벤트를 생성 및 소비하는 메시지 브로커"                                    "RabbitMQ"
      mysqlPrimary = container "MySQL Primary"             "Primary DB - 콘서트 / 스케줄 / 좌석 / 예약 / 포인트 / 결제 데이터의 정합성을 보장"                     "MySQL"
      mysqlReplica = container "MySQL Replica (Read-Only)" "읽기 전용 Replica DB - 콘서트 및 좌석 조회 시 트래픽을 분산"                                           "MySQL (Read Replica)"
    
        
    }

    // ─────────────────────────
    // 관계
    // ─────────────────────────
    user -> system.web                   "HTTPS"
    
    system.web -> system.api             "REST API 호출"

    system.api -> system.redis           "대기열 / 좌석 락 상태 조회 및 갱신"
    system.api -> system.mysqlPrimary    "예약 / 포인트 / 결제 등 트랜잭션 작업"
    system.api -> system.mysqlReplica    "콘서트 및 좌석 조회용 읽기 작업"
    system.api -> system.mq              "이벤트 생성

    system.worker -> system.mq           "이벤트 소비"
    system.worker -> system.mysqlPrimary "예약 / 대기열 상태 업데이트"
    system.worker -> system.redis        "대기열 캐시 / 좌석 락 상태 갱신"

    // ─────────────────────────
    // 배포 환경 (Production)
    // ─────────────────────────
    production = deploymentEnvironment "Production" {

      lb = deploymentNode "L7 Load Balancer" "모든 HTTP 트래픽을 여러 API 서버 인스턴스로 라우팅" "Nginx / API Gateway" {

        apiNode1 = deploymentNode "API Server #1" "Spring Boot 콘서트 예약 API 서버 인스턴스 1" "Docker 컨테이너 / JVM" {
          apiInstance1 = containerInstance system.api
        }

        apiNode2 = deploymentNode "API Server #2" "Spring Boot 콘서트 예약 API 서버 인스턴스 2" "Docker 컨테이너 / JVM" {
          apiInstance2 = containerInstance system.api
        }
      }

      workerNode = deploymentNode "Background Worker Node" "예약 만료 처리, 대기열 승격 등의 비동기 작업을 수행하는 워커" "Docker 컨테이너 / JVM" {
        workerInstance = containerInstance system.worker
      }

      redisNode = deploymentNode "Redis Node" "대기열 상태 / 좌석 락 캐시용 Redis" "Redis" {
        redisInstance = containerInstance system.redis
      }

      mqNode = deploymentNode "RabbitMQ Node" "이벤트 전송용 메시지 큐" "RabbitMQ" {
        mqInstance = containerInstance system.mq
      }

      dbPrimaryNode = deploymentNode "MySQL Primary Node" "쓰기 / 트랜잭션 담당 메인 DB 서버" "MySQL" {
        mysqlPrimaryInstance = containerInstance system.mysqlPrimary
      }

      dbReplicaNode = deploymentNode "MySQL Replica Node" "조회 트래픽 분산용 읽기 전용 복제 DB 서버" "MySQL" {
        mysqlReplicaInstance = containerInstance system.mysqlReplica
      }
      
    }
    
  }

  views {

    // 시스템 컨텍스트 뷰
    systemContext system "SystemContext" "콘서트 예약 서비스와 사용자의 상호 작용" {
      include *
      // autoLayout lr
    }

    // 컨테이너 뷰
    container system "Containers" "콘서트 예약 서비스 내부 컨테이너 구조" {
      include *
      // autoLayout lr
    }

    // 프로덕션 배포 뷰
    deployment * production "ProductionDeployment" "운영 환경 배포 구조" {
      include *
      // autoLayout tb
    }

    // 스타일
    styles {
    
      element "Person" {
        shape Person
      }
      element "Software System" {
        shape RoundedBox
      }
      element "Container" {
        shape RoundedBox
      }
      element "Deployment Node" {
        shape Hexagon
      }
      
      // 모든 관계 라인을 직각 라우팅으로
      relationship "Relationship" {
        routing Orthogonal
      }
      
    }
    
  }
  
}
