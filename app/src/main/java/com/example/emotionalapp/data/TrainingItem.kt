package com.example.emotionalapp.data
// 전체 훈련 어댑팅을 위한 데이터 클래스

// 어떤 액티비티로 이동할지 식별하기 위한 Enum (선택 사항이지만 권장)
enum class TrainingType {
    REPRESENTATIVE_TRAINING, // 대표 훈련
    EMOTION_AVOIDANCE_TRAINING, // 정서회피 훈련
    MIND_WATCHING_TRAINING, // 마음보기 훈련
    DEFAULT_DETAIL // 기본 상세 페이지 (타입으로 구분하기 어려울 때)
}

data class TrainingItem(
    val id: String,
    val title: String,
    val subtitle: String, // XML에 tv_training_subtitle이 있으므로 추가
    val trainingType: TrainingType, // 각 아이템이 어떤 종류의 훈련인지 또는 어떤 페이지로 가야하는지 식별
    val targetActivityClass: Class<*>? = null // 직접 액티비티 클래스를 지정할 경우 (선택적)
    // val iconResId: Int? = null // XML에는 없지만, 필요하다면 아이콘 리소스 ID도 추가 가능
)
