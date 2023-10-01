package io.github.kaztakgh.dialogfragmentlibraryv2

import android.os.Bundle
import org.junit.Assert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.time.LocalDate

/**
 * DateSelectDialogのビルダーテスト
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Bundle::class)
class DateSelectDialogBuilderUnitTest {
    /**
     * 正しく入力された場合
     */
    @Test
    fun inputCorrect() {
        assertDoesNotThrow {
            DateSelectDialog.Builder()
                .date(LocalDate.of(2023, 9, 9))
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
            DateSelectDialog.Builder()
                .date(LocalDate.of(2023, 9, 9))
                .requestKey("TestDialog")
                .tag("TestDialog")
                .isCancelable(true)
                .build()
        }
    }

    /**
     * リクエストキーが存在しない場合
     */
    @Test
    fun missingRequestKey() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            DateSelectDialog.Builder()
                .date(LocalDate.of(2023, 9, 9))
                .build()
        }
        Assertions.assertEquals(ErrorMessage.REQUEST_KEY_MISSING, exception.message)
    }

    /**
     * リクエストキーに空文字を入力した場合
     */
    @Test
    fun inputEmptyRequestKey() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            DateSelectDialog.Builder()
                .date(LocalDate.of(2023, 9, 9))
                .requestKey("")
                .build()

        }
        Assertions.assertEquals(ErrorMessage.REQUEST_KEY_MISSING, exception.message)
    }
}