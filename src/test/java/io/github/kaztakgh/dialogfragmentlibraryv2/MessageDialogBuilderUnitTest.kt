package io.github.kaztakgh.dialogfragmentlibraryv2

import android.os.Bundle
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


/**
 * MessageDialogのビルダーテスト
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Bundle::class)
class MessageDialogBuilderUnitTest {
    /**
     * 正しく入力された場合
     */
    @Test
    fun inputCorrect() {
        // PowerMockを使用するように修正
        // (Bundleを使用しているため、junitでは対応不可能)
        assertDoesNotThrow {
            MessageDialog.Builder()
                .title("TestDialogTitle")
                .text("TestDialogText")
                .positiveLabel("OK")
                .requestKey("testMessageDialog")
                .build()
        }
    }

    /**
     * 全てのパラメータを入力
     */
    @Test
    fun inputAllFunction() {
        assertDoesNotThrow {
            MessageDialog.Builder()
                .title("TestDialogTitle")
                .text("TextDialogText")
                .positiveLabel("OK")
                .negativeLabel("Cancel")
                .neutralLabel("Later")
                .requestKey("testMessageDialog")
                .tag("testTag")
                .isCancelable(false)
                .build()
        }
    }

    /**
     * タイトルが空文字の場合
     */
    @Test
    fun missingTitle() {
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            MessageDialog.Builder()
                .text("TestDialogText")
                .positiveLabel("OK")
                .requestKey("testMessageDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.TITLE_MISSING, exception.message)
    }

    /**
     * テキスト(本文)が空文字の場合
     */
    @Test
    fun missingMessage() {
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            MessageDialog.Builder()
                .title("TestDialogTitle")
                .positiveLabel("OK")
                .requestKey("testMessageDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.TEXT_MISSING, exception.message)
    }

    /**
     * ボタンの設定がない場合
     */
    @Test
    fun missingButtons() {
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            MessageDialog.Builder()
                .title("TestDialogTitle")
                .text("TestDialogText")
                .requestKey("testMessageDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.BUTTON_IS_NOT_EXIST, exception.message)
    }

    /**
     * リクエストキーが存在しない場合
     */
    @Test
    fun missingRequestKey() {
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            MessageDialog.Builder()
                .title("TestDialogTitle")
                .text("TestDialogText")
                .positiveLabel("OK")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.REQUEST_KEY_MISSING, exception.message)
    }
}