# ProjectAndroidAutoRoutine: Senior Android Engineer 분석 및 리팩토링 리포트

## 1. 프로젝트 목적 및 문제 정의
**해결하려는 문제:** 기존 사용자들은 스마트폰 설정(Wi-Fi, 밝기 조절, 무음 모드 등)을 매번 상황에 맞춰 수동으로 변경해야 하는 불편함이 있습니다.
**제공 가치:** 백그라운드 환경 감지를 통해 사용자의 반복적인 라이프스타일 패턴을 AI가 학습 및 추론하여, 명시적인 조건 설정 없이도 "나만의 커스텀 자동화 환경"을 0-click 으로 제공합니다.
**기존 기능과의 차별점:** 삼성 빅스비 루틴이나 Tasker 애플리케이션은 사용자가 명시적으로 IF-THEN 규칙을 수동으로 짜야 하지만, 본 프로젝트는 `Pattern Recognition(빈발 규칙 학습)`을 통해 **"알아서 묻고, 알아서 생성해주는"** 지능형 어시스턴스를 지향합니다.

---

## 2. Android 아키텍처 분석 및 개선
**현재 코드 분석:** 방금 생성된 뼈대는 **Clean Architecture를 표방하는 MVVM 패턴**의 초기 형태입니다. 그러나 비즈니스 로직(UseCase)에 하드코딩된 규칙이 섞여 있고, Service가 강하게 결합되어 있어 진정한 의미의 Clean Architecture로 보기는 어렵습니다. 과도기로 볼 수 있습니다.

### 개선된 Clean Architecture + MVVM 설계
데이터 강결합성을 해소하기 위해 아래와 같이 패키지를 엄격히 분리합니다.

- **Presentation Layer (UI/ViewModel)**: Compose UI, `RoutineViewModel`. UI는 Event만 발생시키고, 상태는 Flow 수집을 통해 렌더링.
- **Domain Layer (UseCase/Model)**: `SuggestRoutineUseCase`. 안드로이드 프레임워크(Context 등)에 의존성이 0%인 순수 Kotlin 모듈로 유지.
- **Data Layer (Repository/DataSource)**: `RoutineRepositoryImpl`, Room DB, `ContextMonitorService`에서 수집되는 센서 데이터 DataSource 캡슐화.

**단계별 데이터 흐름:**
> `ContextMonitorService`(Sensor) → `LogEntity` (Room) → `RoutineRepository` (Data) → `SuggestRoutineUseCase` (Domain, 패턴 분석 수행) → `RoutineEntity` 추천 목록 반환 → `RoutineViewModel` (StateFlow 업데이트) → `Compose UI` 렌더링

---

## 3. 상태 관리 및 비동기 처리
**현재 및 개선 제안 분석:**
- 현재 비동기 처리는 기본 `viewModelScope.launch`를 사용하고 있으나, UI State 캡슐화가 부족합니다.
- **개선안**: `StateFlow` 패턴을 도입하여 ViewModel에서 `Loading`, `Success`, `Error` 상태를 명확히 분리하는 단방향 데이터 흐름(UDF, Unidirectional Data Flow)을 강제합니다.

**UI State 설계:**
```kotlin
sealed interface RoutineUiState {
    object Loading : RoutineUiState
    data class Success(val routines: List<RoutineEntity>) : RoutineUiState
    data class Error(val message: String) : RoutineUiState
}
```

---

## 4. Android 시스템 연동 (핵심)
**현재 상호작용 분석:** Foreground Service를 막 띄운 상태로, 배터리 광탈에 취약한 폴링(Polling) 구조의 위험이 있습니다.
**기술 요소별 분석:**
- **BroadcastReceiver**: 배터리 상태, 이어폰 연결(ACTION_HEADSET_PLUG) 감지는 Polling이 아닌 시스템 Receiver를 활용해야 리소스를 아낍니다.
- **Foreground Service**: 상시 모니터링이 필요하므로 불가피하나, 안드로이드 14 이상의 백그라운드 정책에 맞춰 적절한 ForegroundServiceType(예: `dataSync` 또는 `specialUse`) 명시 필수.
- **WorkManager**: 기계학습 모델을 돌려 루틴을 추천하는 무거운 작업은 Service의 MainThread나 Coroutine 대신, 충전 중이거나 Wi-Fi 연결 시 동작하는 `WorkManager`의 `PeriodicWorkRequest`로 오프로드해야 **배터리 광탈 이슈**를 넘길 수 있습니다.

---

## 5. 자동화 로직 설계
**현재 구조:** 하드코딩된 `if-else`문 기반의 **Rule 기반 패턴**입니다. ("밤이고 유튜브면 추천해라")
**확장 가능한 구조 설계 (Condition-Action Pattern):**
- 루틴 엔진 구조화:
  - `Condition`: `interface Condition { fun evaluate(context: AppContext): Boolean }`
  - `Action`: `interface Action { fun execute(context: AppContext) }`
- 이 구조를 두면, 새로운 조건(예: 블루투스 연결)이나 새로운 액션(예: 밝기 조절)이 추가되어도 기존 코드를 수정하지 않고 OCP(개방폐쇄원칙)를 지켜 무한 확장이 가능합니다.

---

## 6. 데이터 저장 방식 개선
**현재 방식:** Room DB 뼈대 적용.
**테이블 구조 최적화 정의:**
- `DeviceLog` 테이블: 대량의 시계열 로그. 시간 만료 시 자동 삭제 정책 필요.
  - `timestamp` (Indexed), `type` (배터리, 화면 등), `value`
- `Routine` 테이블: 트리거 조건의 묶음.
  - `id`, `name`, `trigger_json`(List of Conditions), `action_json`(List of Actions), `isEnabled`
- **개선:** 조건과 액션을 일대다/다대다로 대응하기 위해 JSON 형태 직렬화 컨버터(`@TypeConverter`) 혹은 중간 관계 매핑 테이블을 추가 활용해야 합니다.

---

## 7. 성능 및 리소스 관리
**발생 가능한 문제:**
1. **메모리 누수**: Service 내부에서 싱글톤 객체나 Callback 참조를 잘못 잡을 경우 Leak 발생.
2. **배터리 광탈**: 1분마다 센서를 조회하면 안드로이드 Doze Mode를 깨워 앱이 OS에 의해 강제 종료(Kill)됩니다.
**해결 전략:**
- **OS 권장 API 사용**: 위치 추적의 경우 `Geofencing` API 연동.
- 빈번한 DB 쓰기: 단건 `insert` 대신 메모리 Queue에 일정량 버퍼를 모아 한 번에 `insert`하는 Bulk 인서트 / Flow debounce 적용.

---

## 8. 코드 품질 및 Android Best Practice
**문제점:** 초기 스캐폴딩이므로 객체 주입, 상태 구조 캡슐화가 다소 느슨함. 
**개선 방향:**
- **의존성 주입(Dagger Hilt)**: 보일러플레이트 없이 생명주기에 맞는 싱글톤 의존성을 주입하여 유닛 테스트가 가능하도록 철저히 분리.
- **Coroutines Best Practice**: `Dispatchers.IO` 명시적 주입 적용(ViewModel 등에 하드코딩 주입 X). 안드로이드 구글 공식 가이드에 맞추어 `Dispatcher`를 Interface/DI로 빼내서 테스터블하게 변경.

---

## 9. 테스트 전략
ViewModel 등의 핵심 로직이 UI 라이브러리와 떨어져 있으므로 순수 JUnit 모의 테스트가 가능합니다.
**단위 테스트 예시 (`SuggestRoutineUseCaseTest`)**:
```kotlin
@Test
fun `밤에 유튜브를 5번 이상 시청한 로그가 주어지면 심야루틴을 제안해야 한다`() = runTest {
    // Given: Mock Log Repository
    val logs = generateMockLogs(time = "Night", app = "youtube", count = 5)
    // When
    val suggested = useCase.execute(logs)
    // Then
    assertTrue(suggested.any { it.ruleName == "심야 유튜브 모드" })
}
```
**UI 테스트**: Espresso 혹은 Compose `createComposeRule()`을 사용하여 버튼 클릭 및 제안 목록 랜더링 검증.

---

## 10. 면접 대비 핵심 질문 & 모범 답변 리스트

> [!NOTE]
> 해당 질문들을 대비하면 네이버/카카오 플랫폼, 통신사 및 IT 대기업의 모바일 파트 면접을 돌파할 수 있습니다.

### Q1. Android에서 Background 프로세스 중 배터리와 시스템 최적화 이슈를 어떻게 해결했나요?
**A:** 단순 Service로 무한 모니터링을 하면 Doze Mode에 의해 시스템에서 앱이 Kill 되고 배터리가 소모됩니다. 따라서 즉각적인 이벤트는 `BroadcastReceiver`를 통해 리소스를 낭비하지 않고 수신했고, 무거운 ML 패턴 추론이나 로그 대량 분석은 충전 중+Wi-Fi 상태 조건을 걸어 `WorkManager`로 백그라운드 오프로딩 시켜 시스템 생태계를 해치지 않도록 설계했습니다.

### Q2. Architecture로 MVVM과 Clean Architecture를 채택했는데, Domain 레이어의 역할은 무엇인가요?
**A:** Domain 레이어는 안드로이드의 Context 의존성이 전혀 없는 순수한 비즈니스 로직의 집합소입니다. `RecommendRoutineUseCase`처럼 데이터 검증, 패턴 탐색 규칙을 여기에 둠으로써, UI가 바뀌거나 DB가 SQLite에서 Realm으로 바뀌더라도 핵심 로직은 수정할 필요(OCP 준수)가 없으며, Context Mocking 없이 가장 빠르게 단위 테스트를 100% 진행할 수 있는 것이 가장 큰 장점입니다.

### Q3. Coroutine Flow와 Livedata의 차이는 무엇이며, 본 프로젝트에서는 왜 StateFlow를 썼나요?
**A:** LiveData는 안드로이드 Lifecycle에 종속적이라 Domain 레이어에서 사용할 수 없고, Main Thread에서만 작동합니다. 반면 StateFlow는 코틀린 표준 라이브러리로 Lifecycle 종속성 없이 다루기 용이하고 Flow의 다양한 연산자(map, filter, combine)를 자유롭게 연결할 수 있습니다. Compose 환경에서도 `collectAsState`로 매끄럽게 연결되므로 StateFlow를 채택했습니다.

### Q4. Room Database에서 데이터가 변경되었을 때 화면에 어떻게 즉각적으로 반영되나요? 
**A:** Room의 DAO 반환 타입을 `Flow<List<RoutineEntity>>`로 설정합니다. 루틴이 업데이트/추가/삭제되면 Room은 바뀐 데이터를 Emit하여 Flow 파이프라인을 타고 ViewModel을 거쳐 Compose 화면을 리컴포지션(Recomposition)되게 만듭니다. 우리는 이를 `단방향 데이터 흐름(UDF)`이라 부릅니다.

### Q5. BroadcastReceiver와 Foreground Service의 역할을 본 프로젝트 관점에서 구분지어 설명해보세요.
**A:** BroadcastReceiver는 이어폰 장착이나 배터리 낮음 수준 도달과 같은 '비동기적인 시스템 이벤트(Trigger)'를 낚아채는 인터페이스 역할입니다. Foreground Service는 사용자에게 Notification을 보여주어 "앱이 동작중입니다"를 인지시키는 동시에, 프로세스 우선순위를 높여 OOM(Out of Memory) Killer에게 강제 종료되지 않고 수신된 이벤트를 처리하고 기록하는 실 작업 환경 역할입니다.

**(Q6~10은 답변 내용이 길어질 수 있어, 다음 스텝에서 추가적으로 파고들거나 문서에 구체화 가능합니다.)**
- **Q6**: Routine 자동 실행의 권한 문제는 어떻게 해결했나 (SYSTEM_ALERT_WINDOW 및 Settings.System 수정 권한)
- **Q7**: WorkManager의 한계치와 예외 처리 방식
- **Q8**: Hilt를 적용한 의존성 주입의 장점
- **Q9**: Flow Debounce 처리 (짧은 시간에 반복된 로그 발생 방어법)
- **Q10**: 수집된 데이터를 서버로 보낼 때 발생 가능한 스레드 블라킹 해결방안

---

## 11. 최종 평가 및 리팩토링 로드맵

**현재 수준 평가: 중 (개념 검증 PoC 수준)**
- 현재: 컴포넌트 뼈대 구조만 잡혀있으며, 실제 OS 센서와의 브릿지와 AI 로직, 상태 Flow가 결상된 상태임.

**면접 합격권 진입을 위한 로드맵:**

- **1단계 (필수/도메인 완성)** 
  - `RoutineUiState` 클래스 도입을 통한 무결점 상태 관리.
  - Service에서 가짜 더미 로그를 뿜어내고 이를 Flow를 통해 UI 단까지 실시간 렌더링 시키는 UDF(Unidirectional Data Flow) 루프 완성.
- **2단계 (심화/안정성)** 
  - Dagger Hilt 모듈 정의. UseCase 및 Repository 의존성 강제 분리.
  - BroadcastReceiver 연동 (이어폰 끼면 Dummy Log 쌓이는 이벤트 붙이기).
- **3단계 (차별화/고도화 테스트)**
  - WorkManager 추적 워크플로우 분리.
  - `MockK` 라이브러리를 활용한 완벽한 순수 Domain 레이어 단위 테스트 작성 및 README 커버리지 결과 추가.

## ⚠️ User Review Required
위의 분석/개선 리포트 및 Q&A를 숙지하신 후, 곧바로 **[1단계: 도메인 완성 및 MVVM UI 상태 분리 (UDF)]** 코딩 스텝을 실행하여 소스코드를 대폭 고도화 시킬까요? 피드백 부탁드립니다.
