# wordmate-android

매일 10분, 단어로 겨루고 성장하는 영단어 학습 앱 **WORD MATE**의 학생용 Android 앱입니다.
Figma `WORD MATE` 파일의 **App-학생** 페이지와 **Design System** 페이지를 기준으로 구현했습니다.

## 기술 스택

| 구분 | 사용 |
| --- | --- |
| 언어 | Kotlin 2.2 |
| UI | Jetpack Compose (Material 3), Compose BOM 2025.10 |
| 내비게이션 | Navigation Compose 2.9 (kotlinx.serialization 타입 세이프 라우트) |
| 상태 관리 | ViewModel + StateFlow |
| 빌드 | AGP 8.13, Gradle 8.14, 버전 카탈로그 |
| SDK | minSdk 26 / target·compile 36 |

## 프로젝트 구조

```
app/src/main/java/com/wordmate/android
├── core
│   ├── designsystem
│   │   ├── theme        # Color · Typography · Spacing/Radius · WordMateTheme
│   │   └── component    # 버튼, 입력, 칩, 카드, 상단바, 진행바, 바텀시트 등
│   └── ui               # ViewModel 팩토리, TTS
├── data
│   ├── model            # 도메인 모델
│   └── repository       # WordMateRepository (현재 인메모리 구현 + 샘플 데이터)
├── navigation           # 라우트, 하단 탭바, NavHost
└── feature
    ├── auth             # 로그인, 약관 동의
    ├── home             # 홈, 알림 팝업
    ├── wordpack         # 단어팩 목록/상세, 내 단어팩 만들기·편집, 신고
    ├── study            # 학습 설정, 플래시카드/4지선다/직접 입력, 결과
    ├── battle           # 대결 홈, 방 만들기, 대기실, 코드 참가, 대결 진행/결과
    ├── ranking          # 랭킹
    └── mypage           # 마이페이지, 배지, 설정, 공지, 고객센터
```

## 구현 화면

- **인증**: DataGSM 로그인, 약관 전체/개별 동의, 약관 상세
- **홈**: 히어로 카드, 연속 학습·레벨·오늘 XP, 오늘 할 일, 이어서 학습, 오늘의 문장, 일일 미션(XP 받기), 랭킹 미리보기, 알림 팝업
- **단어팩**: 기본/내 단어팩, 검색·카테고리 필터, 상세(학습 상태 필터, 뜻 가리기, 발음 듣기, 즐겨찾기), 신고, 내 단어팩 만들기·단어 추가/수정/삭제·팩 삭제
- **학습**: 학습 방식·출제 옵션 설정, 플래시카드, 4지선다, 직접 입력, 중단 확인, 결과(틀린 단어만 다시 풀기)
- **대결**: 빠른 대결 매칭, 대결방 만들기(단어팩 고르기, 점수 방식·문항 수·시간·인원), 대기실(코드 복사/공유), 코드로 참가, 타이머·점수·이모지 반응이 있는 대결 진행, 결과(시상대·순위·신고), 틀린 단어, 대결 기록
- **랭킹**: 범위(학교/학년/반/친구)·기간(주간/월간/전체) 필터, 시상대, 내 순위 고정
- **마이**: 프로필·레벨·통계, 랭킹 배지, 설정(알림, 로그인 관리, 로그아웃·탈퇴), 공지사항, 고객센터 문의

## 빌드 및 실행

```bash
./gradlew assembleDebug        # APK 빌드
./gradlew installDebug         # 연결된 기기/에뮬레이터에 설치
./gradlew testDebugUnitTest    # 단위 테스트
```

Android Studio에서 프로젝트를 열고 `app` 구성을 실행해도 됩니다. JDK 17 이상이 필요합니다.

## 참고 사항

- **서버 연동 전 상태**: 모든 데이터는 `InMemoryWordMateRepository`의 샘플 데이터이며 앱을 재시작하면 초기화됩니다. API가 준비되면 `WordMateRepository` 인터페이스를 유지한 채 원격 구현으로 교체하면 됩니다.
- **로그인**: DataGSM OAuth는 아직 연결하지 않았고, 버튼을 누르면 바로 로그인된 상태가 됩니다.
- **대결**: 실시간 서버가 없어 상대 입장·점수는 기기 안에서 시뮬레이션합니다.
- **폰트**: Figma 시안의 폰트 파일은 포함하지 않아 시스템 기본 폰트를 사용합니다. 폰트를 `res/font`에 추가한 뒤 `Type.kt`의 `WmFontFamily`만 바꾸면 전체에 적용됩니다.
- **범위**: Figma의 App-교사 / App-관리자 페이지는 이번 작업 범위에 포함하지 않았습니다.
