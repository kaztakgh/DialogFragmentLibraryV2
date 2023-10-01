package io.github.kaztakgh.dialogfragmentlibraryv2

import android.os.Bundle
import org.junit.Assert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.time.LocalTime

/**
 * TimeSelectDialogのビルダーテスト
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Bundle::class)
class TimeSelectDialogBuilderUnitTest {
    /**
     * 正しく入力された場合
     */
    @Test
    fun inputCorrect() {
        assertDoesNotThrow {
            TimeSelectDialog.Builder()
                .time(LocalTime.of(15, 0))
                .requestKey("TestDialog")
                .build()
        }
    }

    /**
     * 全てのパラメータを入力
     */
    @Test
    fun inputAllFunctions() {
        assertDoesNotThrow {
            TimeSelectDialog.Builder()
                .time(LocalTime.of(15, 0))
                .display24h(false)
                .requestKey("TestDialog")
                .tag("TestDialog")
                .isCancelable(false)
                .build()
        }
    }

    /**
     * リクエストキーが存在しない場合
     */
    @Test
    fun missingRequestKey() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            TimeSelectDialog.Builder()
                .time(LocalTime.of(15, 0))
                .build()
        }
        Assertions.assertEquals(ErrorMessage.REQUEST_KEY_MISSING, exception.message)
    }

    /**
     * リクエストキーに空文字を入力
     */
    @Test
    fun inputEmptyRequestKey() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            TimeSelectDialog.Builder()
                .time(LocalTime.of(15, 0))
                .requestKey("")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.REQUEST_KEY_MISSING, exception.message)

    }
}