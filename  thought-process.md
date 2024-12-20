# Verve Tech Challenge 
# Thought Process and Design Considerations


## High-Level Overview
This project is a high-performance, Java-based RESTful service designed to handle at least 10,000 requests per second while ensuring accurate deduplication of incoming requests. The service is also robust enough to operate seamlessly in a distributed environment with multiple instances. Key functionalities include:

### 1. GET Endpoint: 
Accepts requests via /api/verve/accept, requiring a mandatory id parameter and an optional endpoint parameter.

### 2. Deduplication: 
Ensures unique processing of requests based on the id parameter.

### 3. Metrics Reporting: 
Reports the count of unique requests every minute.

### 4. Streaming Service Integration: 
Sends the count to a Kafka topic for distributed processing.

### 5. Optional HTTP POST: 
Sends a POST request with the unique request count to an external endpoint if provided.


## Key Design Considerations
### 1. High Throughput
To handle high request volumes, the service incorporates:

#### i) Optimized Thread Pooling: 
Configured thread pools for the Tomcat server and internal tasks to manage concurrency efficiently.

#### ii) Non-blocking HTTP Calls: 
Utilizes WebClient, a reactive and non-blocking HTTP client, to handle external communications without blocking threads.

### 2. Distributed Deduplication
Deduplication is achieved using:

#### Redis as a Distributed Cache: 
Redis’s operations (setIfAbsent) ensure deduplication across multiple instances. IDs are stored with a Time-To-Live (TTL) of 1 minute, matching the reporting interval.

### 3. Scalability and Reliability
#### i) Kafka Integration: 
Kafka is used to stream unique request counts reliably. It supports scalability through partitioning and replication, ensuring fault tolerance.

#### ii) Horizontal Scalability: 
The architecture supports deployment behind a load balancer, enabling the system to handle growing loads by adding more instances.


### 4. Resilience
The system is built to handle errors gracefully and ensure continuous operation:

#### i) Error Logging: 
Errors during Redis operations, Kafka messaging, or HTTP requests are logged without disrupting request processing.

#### ii) Scheduled Tasks: 
Spring’s @Scheduled annotation ensures regular resetting of counters and reporting to Kafka, maintaining consistency and preventing memory leaks.

## Implementation Details

### Request Handling

#### i) Controller Layer:
The /api/verve/accept endpoint processes incoming requests. Validation and deduplication logic are separated for clarity and maintainability.

#### ii) Service Layer:
Manages deduplication (via Redis) and handles external interactions like Kafka messaging and optional HTTP POST calls.

### Unique ID Deduplication

Redis’s setIfAbsent ensures each id is processed only once within a minute. This approach avoids race conditions and supports distributed deduplication across instances.

### Kafka Integration

A Kafka producer is configured to send unique request counts to the unique-request-counts topic every minute. Kafka’s distributed architecture ensures reliable delivery and scalability for downstream consumers.

### Optional HTTP POST

When an endpoint is provided, the service sends a POST request with a JSON payload containing the unique request count.
The HTTP response status is logged to ensure continuous monitoring of external endpoint availability.

### Scheduled Tasks

A scheduled task resets the currentMinuteCount at one-minute intervals and publishes the count to Kafka. This ensures the system remains consistent and efficient over time.

## Conclusion
This implementation is designed to balance performance, scalability, and resilience, meeting the requirements of a high-throughput, distributed RESTful service. By leveraging Redis for deduplication, Kafka for reliable messaging, and Spring Boot for robust task scheduling, the system is well-equipped to handle large volumes of concurrent requests efficiently while maintaining consistency and reliability.