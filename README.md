## 서비스 주요 아키텍처
1. 캐시를 활용한 큐레이션 서비스
2. 인가 서버 구축

## 아키텍처 설명
### 버전 관리를 통해 캐시 성능 최적화
[캐시 + 버전관리 서비스 성능 개선](https://jseungmin.notion.site/1dfe2fd91ae280be85d2f2e11db2e8b0?pvs=4)

[캐시 이중화를 통해 안정성있는 서비스 환경 구축](https://jseungmin.notion.site/1e0e2fd91ae2802aace6e66bd7d76d2a?pvs=4)
1. 캐시를 통해 조회 개선
2. TTL을 통해 캐시 메모리 관리
3. CachePut을 통해 캐시 업데이트 - 관리자 영역으로 무분별한 업데이트 제어
4. 단일 요청점을 통해 캐시 미스 시 무분별하게 발생할 수 있는 데이터베이스 조회 제어
5. 버전 관리를 통해 캐시 업데이트
6. 캐시 장애 조치

### 인가 서버를 왜 만들까?
[인가서버를 통해 LLM 서버의 보안 구축](https://jseungmin.notion.site/LLM-1dde2fd91ae28033a09bf009beea49e4)
1. LLM 서버와 API 서버가 분리된 환경
2. JWT 토큰 발급을 API 서버가 담당하지만 LLM 서버에 별도의 인가 프로세스를 구축하는 것이 비효율적
3. 따라서 사용자가 LLM 서버로 요청을 보낼때 엑세스 토큰을 같이 보내면, LLM 서버가 API 서버로 토큰을 전송
4. API 서버에서 토큰 응답이 성공적으로 마무리되면 LLM 서버로 응답을 내려줌
5. LLM 서버가 이 응답을 확인한 후 LLM 응답 생성
