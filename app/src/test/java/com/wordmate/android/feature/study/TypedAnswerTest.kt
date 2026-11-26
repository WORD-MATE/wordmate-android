package com.wordmate.android.feature.study

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TypedAnswerTest {
    @Test
    fun `쉼표로 구분된 뜻 중 하나만 맞아도 정답`() {
        assertTrue(StudyViewModel.isTypedCorrect("버리다", "버리다, 포기하다"))
        assertTrue(StudyViewModel.isTypedCorrect(" 포기하다 ", "버리다, 포기하다"))
    }

    @Test
    fun `영어 정답은 대소문자와 공백을 무시`() {
        assertTrue(StudyViewModel.isTypedCorrect("Abandon ", "abandon"))
    }

    @Test
    fun `빈 입력이나 다른 뜻은 오답`() {
        assertFalse(StudyViewModel.isTypedCorrect("", "구별하다"))
        assertFalse(StudyViewModel.isTypedCorrect("구분", "구별하다"))
    }
}
