# 🧠 Emotion Regulation Training App

사용자가 4주간 감정·신체·사고·행동을 체계적으로 기록하고, 주간 설문을 통해 자신의 정서 상태를 점검할 수 있도록 설계한 정서 조절 훈련 Android 애플리케이션입니다.

---

## 🎥 Demo / 📷 App Screenshots

🎥 **Demo Video**

https://github.com/user-attachments/assets/08486867-2274-4b0b-b429-77569ffa7e3c (1분 40초 이후)

| 로그인 | 회원가입 | 메인 | 마이 |
|---|---|---|---|
| <img width="1080" height="2400" alt="로그인" src="https://github.com/user-attachments/assets/cd9ef095-58d5-416c-8e33-38c061969e9c" />
 | <img width="1080" height="2400" alt="회원가입" src="https://github.com/user-attachments/assets/33bdc9dc-3314-4dd9-aad3-6c885d4491a2" />
 | <img width="1080" height="2400" alt="메인" src="https://github.com/user-attachments/assets/7d351c51-1c1e-45bb-9d99-16f0191b557e" />
 | <img width="1080" height="2400" alt="세부훈련(1주차)" src="https://github.com/user-attachments/assets/7855e46d-664b-41a9-94e0-cbfb75b84796" />
 |
| 졸음 | 담배 | 휴대폰 | 미벨트 |
| ![](images/drowsy.jpg) | ![](images/smoking.jpg) | ![](images/phone.jpg) | ![](images/seatbelt.jpg) |
| 알람 | 결과 | 통계 | 날씨 |
| ![](images/alarm.jpg) | ![](images/result.jpg) | ![](images/stat.jpg) | ![](images/weather.jpg) |

---

## 📌 Overview

본 프로젝트는 사용자가 감정·신체·사고·행동을 기록하고  
주차 기반 훈련 프로그램을 통해 정서 상태를 점검할 수 있도록 설계한  
정서 조절 훈련 Android 애플리케이션입니다.

- 주간 설문과 훈련 기록으로 정서 상태 점검
- Firestore 기반 데이터 저장 및 실시간 조회
- MPAndroidChart를 통한 감정 변화 시각화
- 사용자별 훈련 진행 상태 관리 및 주차 기반 접근 제어 UX 설계

---

## 🛠 Tech Stack

### 📱 Mobile
Kotlin · Android

### 🌐 Backend
Firebase Authentication · Cloud Firestore

### 📊 Data Visualization
MPAndroidChart

---
## 📂 Project Structure

### 간략 구조
```
emotionalApp/
└─ app/src/main/java/com/example/emotionalapp/
   ├─ MyApplication.kt
   ├─ data/            # Training, Report 모델 정의
   ├─ ui/              # 기능별 화면 (emotion, body, mind, expression, weekly, chat)
   └─ util/            # 공통 확장 함수 및 유틸
```
<details>
  <summary>상세 구조</summary>
  <pre><code>
emotionalApp/
├─ app/
│  └─ src/main/
│     ├─ java/com/example/emotionalapp/
│     │  ├─ MyApplication.kt
│     │  ├─ AddTester.kt
│     │  ├─ data/
│     │  │  ├─ TrainingType.kt
│     │  │  ├─ TrainingItem.kt
│     │  │  ├─ DetailTrainingItem.kt
│     │  │  ├─ ReportItem.kt
│     │  │  └─ DrivenActionQuizItem.kt
│     │  ├─ ui/
│     │  │  ├─ open/ (BottomNavActivity.kt)
│     │  │  ├─ login_signup/
│     │  │  ├─ intro/
│     │  │  ├─ alltraining/
│     │  │  ├─ emotion/
│     │  │  ├─ body/
│     │  │  ├─ mind/
│     │  │  ├─ expression/
│     │  │  ├─ weekly/
│     │  │  └─ chat/
│     │  └─ util/
│     └─ res/
│        ├─ layout/
│        ├─ drawable/
│        └─ values/
  </code></pre>
</details>

### Layer Overview

- **App Layer**  
  `MyApplication`에서 Firebase 초기화 및 전역 설정 관리

- **Data Layer**  
  훈련 및 리포트 도메인 모델 정의 (`Training*`, `ReportItem`)

- **UI Layer**  
  기능 단위 패키지 구조 (emotion, body, mind, expression, weekly)

- **Util Layer**  
  공통 확장 함수 및 재사용 로직 (예: Click Debounce)

- **Backend Integration**  
  Firebase Authentication + Cloud Firestore 기반 사용자 인증 및 데이터 저장

---
## ⚙️ Key Features

### 🔐 이메일 기반 사용자 인증
- Firebase Authentication을 활용한 회원가입 / 로그인
- 사용자별 데이터 분리 저장 및 접근 제어

### ✍️ 감정 설문 및 기록 저장
- 주간 상태 점검 설문 작성
- 감정 · 신체 · 사고 · 행동 기록 저장
- Cloud Firestore 기반 데이터 관리

### 📊 감정 데이터 조회 및 시각화
- 주차별 · 날짜별 감정 기록 조회
- MPAndroidChart를 활용한 감정 통계 시각화

### 📞 관리자 연락 기능
- 관리자에게 전화 및 이메일 전송 (Intent 기반 외부 앱 연동)

---

## 💡 Implementation Highlights

### 1️⃣ Firestore 문서 구조 설계

정서 기록 데이터는 사용자 단위로 지속적으로 누적되기 때문에
데이터 조회 패턴을 고려한 Firestore 문서 구조를 설계했습니다.

문서 구조  
```
/user/{email}/{훈련명}/{timestamp}
```
- 사용자 기준 데이터 스코프 분리
- 훈련 유형별 서브컬렉션 구성 (emotion / body / mind / expression)
- 문서 ID를 `timestamp` 기반으로 설계하여 **유일성 확보 + 최신순 정렬 기준**으로 동시 활용

### 2️⃣ 사용자 경험(UX) 설계

✔ **주차 기반 훈련 흐름 설계**

- 주차별 설문을 선행 조건으로 설정
- 미진행 시 팝업 안내를 통해 자연스럽게 훈련 진행 유도

✔ **훈련 진행 제약 및 상태 가시화**

- 훈련별 진행 가능 일자 및 횟수 제한 로직 구현
- 동일 훈련 내 진행 횟수를 `0/1`, `0/2` 형태로 표시
- 현재 진행 상태를 직관적으로 인지할 수 있도록 UI 설계

✔ **주차 경과에 따른 자동 잠금 처리**

- 회원 가입일 기준 주차 계산
- 미래 주차 훈련은 사전 진행 불가하도록 제어
- 이전 주차 훈련은 언제든 수행 가능하도록 설계

✔ **기록 조회 경험 개선**

- 훈련 기록은 주차별로 언제든 열람 가능
- 최신 기록 기준 정렬로 최근 활동을 우선적으로 확인 가능

✔ **감정 변화의 시각적 피드백 제공**

- MPAndroidChart를 활용한 감정 변화 시각화
- 사용자가 정서 변화 과정을 직관적으로 확인할 수 있도록 UX 설계

---

## 🔧 Troubleshooting

### 1️⃣ Firestore 문서 구조 설계 및 문서 ID 전략

❌ **문제 상황**

정서 기록 데이터가 지속적으로 누적되는 구조였기 때문에
초기에는 단순 컬렉션에 저장하는 방식을 고려했으나  
사용자 단위 조회 및 카테고리별 정렬 쿼리 구성에서 복잡성이 발생함.  

또한 기록 데이터의 정렬 기준과 유일성을 동시에 확보할 수 있는
문서 ID 설계가 필요했음.

🔎 **원인 분석**

- Firestore는 관계형 DB처럼 JOIN이 불가능
- 사용자 단위 접근이 많기 때문에 데이터 스코프 분리가 중요
- 자동 생성 ID 사용 시 정렬 기준을 별도로 관리해야 함
- 기록형 데이터 특성상 유일성과 정렬 기준을 동시에 확보해야 했음

🛠 **해결 방법**

- `/user/{email}/훈련명/{timestamp}` 구조로 사용자 기준 데이터 분리
- 훈련 유형별 서브컬렉션 분리하여 조회 단순화
- 문서 ID를 `timestamp` 기반으로 구성하여  
  - 기록의 유일성 확보  
  - 최신순 정렬 기준으로 활용 가능하도록 설계

📈 **결과**

- 사용자 기준 데이터 조회 구조 단순화
- 훈련 유형별 데이터 접근 및 쿼리 구성 개선
- 최신 기록 기준 정렬 가능
- Firestore 데이터 모델 구조 안정화

### 2️⃣ 중복 저장 문제 (완료 버튼 연타 이슈)

❌ **문제 상황**

훈련 완료 버튼을 연속으로 클릭할 경우  
Firestore에 동일 기록이 중복 저장되는 문제가 발생함.

🔎 **원인 분석**

- 비동기 네트워크 요청 처리 중 버튼이 계속 활성화 상태였음
- 서버 응답 전까지 UI에서 중복 입력을 막지 못함

🛠 **해결 방법**

- 완료 버튼 클릭 시 **1초 재클릭 제한 로직 적용**
- 저장 요청 진행 중 **버튼 비활성화 처리**
- 저장 성공 후에만 UI 상태 갱신

📈 **결과**

- 동일 데이터 중복 저장 문제 해결
- 비동기 요청 처리 중 UI 안정성 확보
- 사용자 입력에 대한 방어적 UI 처리 구현

### 3️⃣ 주차 기반 훈련 잠금 UX 설계

❌ **문제 상황**

훈련에는 선행 설문, 주차 제한, 횟수 제한 등 다양한 제약 조건이 존재했음.  
단순히 기능을 잠그는 방식으로 구현할 경우  
사용자가 왜 진행이 불가능한지 이해하기 어려울 수 있었음.

🔎 **원인 분석**

- 기능 제약 조건이 사용자에게 명확히 전달되지 않음
- 진행 상태를 직관적으로 확인할 수 있는 UI 요소 부족

🛠 **해결 방법**

- 선행 설문 미진행 시 **팝업 안내 제공**
- `0/1`, `0/2` 형태로 **훈련 진행 상태 가시화**
- 미래 주차 훈련은 **비활성화 처리**
- 주차 계산은 **가입일 기준 동적 계산 로직**으로 구현

📈 **결과**

- 사용자에게 진행 제한 이유를 명확하게 전달
- 훈련 진행 상태를 직관적으로 확인 가능
- 주차 기반 훈련 흐름 유지 및 UX 개선
