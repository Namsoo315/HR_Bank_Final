# {팀이름}

![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white) [팀 협업 문서 링크](https://www.notion.so/2680fa6d0dae80f3b316ceff3494cfe2)

## 팀원 구성
| 이름  | 역할          | 설명                                             | GitHub 주소                              |
| --- | ----------- | ---------------------------------------------- | -------------------------------------- |
| 남현수 | (팀장) 백엔드 개발자     | 직원 관리 시스템 엔티티 설계 및 구현, CI/CD 파이프라인 구축, 환경변수 관리 | [GitHub](https://github.com/Namsoo315) |
| 류승민 | 백엔드 개발자     | 부서 관리 및 데이터 백업 시스템 개발, Batch 처리 및 스케줄링 담당      | [GitHub](https://github.com/bustam00)  |
| 박종현 | 백엔드 개발자     | 수정 로그 시스템 개발, ERD 설계 및 코드 리팩터링                 | [GitHub](https://github.com/yeahlimm)  |
| 이예림 | 백엔드/프론트 개발자 | 프론트엔드와 수정 상세 내역 조회 기능 개발, PPT 및 작업 흐름도 제작      | [GitHub](https://github.com/Namsoo315) |
| 민재영 | 백엔드/프론트 개발자 | 프론트엔드 및 데이터 백업 기능 개발, 파일 스토리지 설정 및 관리          | [GitHub](https://github.com/jymin0)    |


---

## 프로젝트 소개
### HR_Bank
Batch로 데이터를 관리하는 Open EMS

> 🏢 기업의 핵심 자산, 인적 자원을 체계적으로 관리하세요!
HR Bank는 인사 데이터를 안전하고 효율적으로 관리할 수 있도록 설계된 Open EMS(Enterprise Management System)입니다. 대량의 데이터를 안정적으로 처리할 수 있는 Batch 시스템을 기반으로 부서 및 직원 정보를 체계적으로 운영할 수 있으며, 백업 자동화, 이력 관리, 대시보드 제공을 통해 기업 인사 관리를 더욱 효과적으로 지원합니다. 📊💼

- **프로젝트 기간:** 2025.09.05 ~ 2025.09.16

---
### 기술 스택
### Frontend
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black)

#### Backend  
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MapStruct](https://img.shields.io/badge/MapStruct-000000?style=for-the-badge&logo=mapstruct&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

#### Database  
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-004088?style=for-the-badge&logo=h2&logoColor=white)

#### CI/CD  
![Railway](https://img.shields.io/badge/Railway-0B0D0E?style=for-the-badge&logo=railway&logoColor=white)

#### 협업 Tool  
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white)

---
## 팀원별 구현 기능 상세

## 박 종현
## 페이지네이션 구현 (ChangeLog)

### 상황

- 대량 데이터에서도 안정적인 페이지네이션 정렬 필요.

### 문제

- 페이지네이션을 사용하지 않으면 전체 쿼리를 항상 구해와 성능적으로 떨어짐
- ChangeLog, Histories같은 데이터는 많이 쌓이므로 페이지별로 구해오는 과정이 필요함.

### 행동

- Service
    - Repository의 검색 결과를 size + 1로 조회, hasNext 판별 후 자르기
    - 마지막 요소의 `id` 다음 커서로 사용: `nextCursor = {"id": <lastId>}` 문자열 생성

```java
@Override
  public CursorPageResponse<ChangeLogDTO> searchChangeLogs(
      String employeeNumber, String memo, String ipAddress, ChangeLogType type, Instant atFrom, Instant atTo, Long idAfter,
      String cursor, int size, String sortField, String sortDirection) {

    List<ChangeLog> changeLogs = changeLogRepository.searchChangeLogs(
        employeeNumber, memo, ipAddress, type, atFrom, atTo, idAfter, size + 1, sortField, sortDirection);
    boolean hasNext = changeLogs.size() > size;
    if(hasNext) changeLogs = changeLogs.subList(0, size);

    List<ChangeLogDTO> dtos = changeLogs.stream()
        .map(changeLogMapper::toDto)
        .toList();

    Long nextIdAfter = hasNext ? dtos.get(dtos.size() - 1).id() : null;
    long totalCount = changeLogRepository.countChangeLogs(employeeNumber, memo, ipAddress, type, atFrom, atTo);

    return CursorPageResponse.<ChangeLogDTO>builder()
        .content(dtos)
        .nextCursor(nextIdAfter != null ? ("{\\"id\\":" + nextIdAfter + "}") : null)
        .nextIdAfter(nextIdAfter)
        .size(size)
        .totalElements(totalCount)
        .hasNext(hasNext)
        .build();
  }

```

### 결과

- 운영자가 원하는 조건으로 안정적인 이력 조회 가능.
- 커서 방식으로 스크롤형 페이지네이션 시 응답 안정성 확보.

## 파일 구조
```
├─src
  ├─main
  │  ├─java
  │  │  └─com
  │  │      └─codeit
  │  │          └─hrbank
  │  │              │  HrBankApplication.java
  │  │              │
  │  │              ├─config
  │  │              │      OpenApiConfig.java
  │  │              │      QuerydslConfig.java
  │  │              │      WebConfig.java
  │  │              │
  │  │              ├─controller
  │  │              │      BackupController.java
  │  │              │      BinaryContentController.java
  │  │              │      ChangeLogController.java
  │  │              │      DepartmentController.java
  │  │              │      DownloadController.java
  │  │              │      EmployeeController.java
  │  │              │
  │  │              ├─dto
  │  │              │  ├─data
  │  │              │  │      BackupDTO.java
  │  │              │  │      BinaryContentDTO.java
  │  │              │  │      ChangeLogDTO.java
  │  │              │  │      DepartmentDTO.java
  │  │              │  │      EmployeeDistributionDTO.java
  │  │              │  │      EmployeeDTO.java
  │  │              │  │      EmployeeTrendDTO.java
  │  │              │  │      HistoryDTO.java
  │  │              │  │
  │  │              │  ├─request
  │  │              │  │      BinaryContentCreateRequest.java
  │  │              │  │      BinaryContentUpdateRequest.java
  │  │              │  │      DepartmentCreateRequest.java
  │  │              │  │      DepartmentUpdateRequest.java
  │  │              │  │      EmployeeCreateRequest.java
  │  │              │  │      EmployeeUpdateRequest.java
  │  │              │  │      HistoryCreateRequest.java
  │  │              │  │      HistoryUpdateRequest.java
  │  │              │  │
  │  │              │  └─response
  │  │              │          CursorPageResponse.java
  │  │              │
  │  │              ├─entity
  │  │              │      Backup.java
  │  │              │      BackupStatus.java
  │  │              │      BinaryContent.java
  │  │              │      ChangeLog.java
  │  │              │      ChangeLogType.java
  │  │              │      DateUnit.java
  │  │              │      Department.java
  │  │              │      Employee.java
  │  │              │      EmployeeGroupBy.java
  │  │              │      EmployeeStatus.java
  │  │              │      History.java
  │  │              │
  │  │              ├─exception
  │  │              │      GlobalExceptionHandler.java
  │  │              │
  │  │              ├─mapper
  │  │              │      BackupMapper.java
  │  │              │      BinaryContentMapper.java
  │  │              │      ChangeLogMapper.java
  │  │              │      DepartmentMapper.java
  │  │              │      EmployeeMapper.java
  │  │              │      HistoryMapper.java
  │  │              │      PageResponseMapper.java
  │  │              │
  │  │              ├─repository
  │  │              │  │  BackupQueryRepository.java
  │  │              │  │  BackupRepository.java
  │  │              │  │  BinaryContentRepository.java
  │  │              │  │  ChangeLogQueryRepository.java
  │  │              │  │  ChangeLogRepository.java
  │  │              │  │  DepartmentQueryRepository.java
  │  │              │  │  DepartmentRepository.java
  │  │              │  │  EmployeeQueryRepository.java
  │  │              │  │  EmployeeRepository.java
  │  │              │  │  HistoryRepository.java
  │  │              │  │
  │  │              │  └─impl
  │  │              │          BackupQueryRepositoryImpl.java
  │  │              │          ChangeLogQueryRepositoryImpl.java
  │  │              │          DepartmentQueryRepositoryImpl.java
  │  │              │          EmployeeQueryRepositoryImpl.java
  │  │              │
  │  │              ├─scheduler
  │  │              │      MyScheduler.java
  │  │              │
  │  │              ├─service
  │  │              │  │  BackupService.java
  │  │              │  │  BinaryContentService.java
  │  │              │  │  ChangeLogService.java
  │  │              │  │  DepartmentService.java
  │  │              │  │  EmployeeAnalyticsService.java
  │  │              │  │  EmployeeService.java
  │  │              │  │
  │  │              │  ├─csv
  │  │              │  │      CsvExportService.java
  │  │              │  │
  │  │              │  └─impl
  │  │              │          BackupServiceImpl.java
  │  │              │          BinaryContentServiceImpl.java
  │  │              │          ChangeLogServiceImpl.java
  │  │              │          DepartmentServiceImpl.java
  │  │              │          EmployeeAnalyticsServiceImpl.java
  │  │              │          EmployeeServiceImpl.java
  │  │              │
  │  │              ├─storage
  │  │              │  │  BinaryContentStorage.java
  │  │              │  │
  │  │              │  └─type
  │  │              │          LocalBinaryContentStorage.java
  │  │              │
  │  │              └─util
  │  │                      ChangeLogUtils.java
  │  │
  │  └─resources
  │        application-dev.yml
  │        application-prod.yml
  │        application.yml
  │        schema.sql
  │      
  │      
  └─test
      └─java
          └─com
              └─codeit
                  └─hrbank
                          HrBankApplicationTests.java
```
## 구현 홈페이지
- 실제 배포 사이트 : [https://hrbank-production.up.rail](https://hrbank-production.up.railway.app/swagger-ui/index.html)
- 배포 Swagger :  [Swagger](https://hrbank-production.up.railway.app/swagger-ui/index.html)

## 프로젝트 회고록
(제작한 발표자료 링크 혹은 첨부파일)
