# ProjectAndroidAutoRoutine 구현 계획서

사용자의 스마트폰 사용 패턴을 백그라운드에서 분석하여, 갤럭시 '빅스비 루틴(모드 및 루틴)'처럼 자동으로 루틴을 생성하고 제안하는 스마트 어플리케이션 프로젝트입니다.

## 🚀 1. 목표 및 핵심 기능
- **컨텍스트 자동 수집**: 시간, 장소(GPS/Wi-Fi), 배터리, 블루투스/이어폰 연결 상태, 실행 중인 앱 등을 백그라운드에서 수집.
- **루틴 패턴 추론 (AI/ML)**: 수집된 로그 데이터를 기반으로 "A 조건일 때 B 행동을 한다"는 빈발 패턴(Frequent Itemset)을 추출.
- **사용자 제안 및 승인 (UI)**: 분석된 루틴("밤 11시에 유튜브를 켜면 화면을 어둡게 할까요?")을 알림 또는 인앱 UI로 제안.
- **루틴 적용(실행)**: 승인된 루틴은 실제 Background Service 로직에 등록되어 조건 달성 시 백그라운드에서 자동 실행.

## 💡 2. 기술 스택 및 라이브러리 (Android Native)

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture Pattern
- **Database**: Room (유저의 행동 로그 및 루틴 조건 저장)
- **Background**: WorkManager / Foreground Service (백그라운드에서 상태 모니터링)
- **Dependency Injection**: Hilt / Dagger

## 🎯 3. 핵심 모듈 및 패키지 구조

```text
ProjectAndroidAutoRoutine/
├── app/src/main/
│   ├── AndroidManifest.xml       # 권한(위치, 배터리, 접근성 등) 선언
│   └── java/com/example/autoroutine/
│       ├── di/                   # Hilt 의존성 주입 모듈
│       ├── data/
│       │   ├── local/            # Room DB (LogEntity, RoutineEntity)
│       │   └── repository/       # Repository 구현체
│       ├── domain/
│       │   ├── model/            # 비즈니스 도메인 모델
│       │   ├── repository/       # Repository 인터페이스
│       │   └── usecase/          # 패턴 추론 알고리즘(Rule/ML) 유즈케이스
│       ├── service/              # ContextMonitorService (이벤트 감지)
│       └── presentation/         # UI 레이어
│           ├── viewmodel/
│           └── ui/               # Compose 뷰 (루틴 제안 화면, 관리 화면)
```

## ⚠️ User Review Required

> [!IMPORTANT]
> 개발 환경 주의사항
> 본 환경에서 Android 앱을 바로 컴파일하거나 실행(에뮬레이터)을 띄우기는 어렵습니다. 
> 따라서 실제 **Android Studio에서 열어서 바로 빌드할 수 있는 완벽한 형태의 Gradle 프로젝트 뼈대와 주요 코어(Service, Model, UI, Rule Engine) 소스 코드**들을 생성해 드리는 방식으로 진행하겠습니다.

## ✅ 다음 실행 스텝 (승인 시 바로 진행)

1. `ProjectAndroidAutoRoutine` 디렉토리 및 Android Gradle 템플릿(build.gradle 등) 스캐폴딩
2. `AndroidManifest.xml`에 필요한 권한(백그라운드 위치, 앱 사용 추적 등) 추가
3. `ContextMonitorService` 뼈대(이벤트 수집기)와 `Room DB` 모델 작성
4. Jetpack Compose UI (루틴 셋업 및 제안 화면) 스캐폴딩

위 계획대로 Gradle 안드로이드 프로젝트 구조를 생성해 드릴까요?
