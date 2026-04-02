# 📱 ProjectAndroidAutoRoutine

**사용자의 맥락을 인지하여 스스로 루틴을 생성하고 제안하는 스마트 Android 애플리케이션**

## 💡 프로젝트 소개
이 프로젝트는 백그라운드 환경에서 사용자의 기기 상태(시간, 배터리, 이어폰 연결, 앱 사용 등) 로그를 수집하고, 특정 조건이 반복될 경우 **"자동화 루틴(Routine)"**을 스스로 추론하여 사용자에게 제안합니다. 사용자가 이를 수락하면 해당 조건이 만족될 때 지정된 동작(밝기 조절, 볼륨 조절 등)이 자동으로 실행됩니다.

마치 **"갤럭시 빅스비 루틴"**의 AI 자동 생성 버전과 같은 역할을 수행합니다.

## 🛠 아키텍처 및 기술 스택
- **언어**: Kotlin 1.9+
- **UI**: Jetpack Compose (Material 3) 기반 단방향 데이터 흐름(UDF)
- **비즈니스 로직**: UseCase 클래스 분리 및 ViewModel 패턴 적용
- **데이터베이스**: Room Database (로컬 로그 저장 및 분석 대상 데이터 보존)
- **백그라운드 처리**: Foreground Service / WorkManager 로직 준비
- **의존성 주입**: Dagger Hilt

## 📂 핵심 디렉토리 설명

- `data/local/`: 사용자 기기의 하드웨어 이벤트 로그(`LogEntity`)와 루틴(`RoutineEntity`) 데이터 모델 및 DAO 정의 
- `domain/usecase/`: 수집된 로그 통계를 기반으로 `Routine`을 추천하는 핵심 AI/규칙 엔진 로직 
- `service/`: 앱이 백그라운드에 있을 때도 디바이스 센서 및 패키지 상태를 주기적으로 모니터링하는 서비스 
- `presentation/`: Compose 기반의 UI. 새로 제안된 루틴의 뷰와 활성화된 루틴 스위치 토글 기능 내장

## 🚀 향후 발전 가능성 (To-Do)
- [ ] 단순 Rule-based 엔진에서 ML Kit 모델 또는 Apriori 알고리즘 포팅
- [ ] 루틴 실행을 위한 실제 Audio/Brightness API 시스템 제어 권한 연동
- [ ] 백그라운드 배터리 최적화를 위한 JobScheduler/WorkManager 고도화
