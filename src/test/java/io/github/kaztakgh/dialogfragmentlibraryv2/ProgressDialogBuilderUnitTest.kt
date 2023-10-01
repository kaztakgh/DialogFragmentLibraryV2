package io.github.kaztakgh.dialogfragmentlibraryv2

import android.os.Bundle
import org.junit.Assert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * ProgressDialogのビルダーテスト
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Bundle::class)
class ProgressDialogBuilderUnitTest {
    /**
     * 正しく入力された場合
     */
    @Test
    fun inputCorrect() {
        assertDoesNotThrow {
            ProgressDialog.Builder()
                .negativeLabel("Cancel")
                .requestKey("TestDialog")
                .build()
        }
    }

    /**
     * 全てのパラメータを入力
     */
    @Test
    fun inputAllFunction() {
        assertDoesNotThrow {
            ProgressDialog.Builder()
                .message("読み込み中")
                .negativeLabel("Cancel")
                .quantityMax(100)
                .requestKey("TestDialog")
                .tag("TestDialog")
                .build()
        }
    }

    /**
     * quantityMaxに0未満の数値を入力
     */
    @Test
    fun inputNegativeMaxValue() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            ProgressDialog.Builder()
                .negativeLabel("Cancel")
                .quantityMax(-1)
                .requestKey("TestDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.CANNOT_ENTER_LESS_THAN_ZERO, exception.message)
    }

    /**
     * ボタンの設定がない場合
     */
    @Test
    fun missingNegativeButton() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            ProgressDialog.Builder()
                .requestKey("TestDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.BUTTON_IS_NOT_EXIST_ON_PROGRESS_DIALOG, exception.message)
    }

    /**
     * キャンセルボタンに空文字を入力
     */
    @Test
    fun inputBlankWordOnButtons() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            ProgressDialog.Builder()
                .negativeLabel("")
                .requestKey("TestDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.BUTTON_IS_NOT_EXIST_ON_PROGRESS_DIALOG, exception.message)
    }

    /**
     * リクエストキーが存在しない場合
     */
    @Test
    fun missingRequestKey() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            ProgressDialog.Builder()
                .negativeLabel("Cancel")
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
            ProgressDialog.Builder()
                .negativeLabel("Cancel")
                .requestKey("")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.REQUEST_KEY_MISSING, exception.message)
    }
}