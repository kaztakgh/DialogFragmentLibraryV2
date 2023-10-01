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
 * SingleChoiceDialogのビルダーテスト
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Bundle::class)
class SingleChoiceDialogBuilderUnitTest {
    private val emptyItems = ArrayList<SelectorItem>()

    /**
     * 正しく入力された場合
     */
    @Test
    fun inputCorrect() {
        val selectorItems = createSelectorItems()
        assertDoesNotThrow {
            SingleChoiceDialog.Builder()
                .title("TestDialogTitle")
                .selectorItems(selectorItems)
                .positiveLabel("OK")
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
        val selectorItems = createSelectorItems()
        assertDoesNotThrow {
            SingleChoiceDialog.Builder()
                .title("TestDialogTitle")
                .columnNum(1)
                .selectorItems(selectorItems)
                .positiveLabel("OK")
                .negativeLabel("Cancel")
                .neutralLabel("Later")
                .requestKey("TestDialog")
                .tag("TestTag")
                .isCancelable(false)
                .build()
        }
    }

    /**
     * タイトルがない場合
     */
    @Test
    fun missingTitle() {
        val selectorItems = createSelectorItems()
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            SingleChoiceDialog.Builder()
                .selectorItems(selectorItems)
                .positiveLabel("OK")
                .negativeLabel("Cancel")
                .requestKey("TestDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.TITLE_MISSING, exception.message)
    }

    /**
     * タイトルが空文字の場合
     */
    @Test
    fun inputEmptyTitle() {
        val selectorItems = createSelectorItems()
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            SingleChoiceDialog.Builder()
                .title("")
                .selectorItems(selectorItems)
                .positiveLabel("OK")
                .negativeLabel("Cancel")
                .requestKey("TestDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.TITLE_MISSING, exception.message)
    }

    /**
     * 選択肢の指定がない場合
     */
    @Test
    fun missingItems() {
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            SingleChoiceDialog.Builder()
                .title("TestDialogTitle")
                .positiveLabel("OK")
                .negativeLabel("Cancel")
                .requestKey("TestDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.SELECTOR_ITEM_IS_EMPTY, exception.message)
    }

    /**
     * 選択肢に空のArrayListを入力した場合
     */
    @Test
    fun inputEmptySelectorItemList() {
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            SingleChoiceDialog.Builder()
                .title("TestDialogTitle")
                .selectorItems(emptyItems)
                .positiveLabel("OK")
                .negativeLabel("Cancel")
                .requestKey("TestDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.SELECTOR_ITEM_IS_EMPTY, exception.message)
    }

    /**
     * ボタンの設定がない場合
     */
    @Test
    fun missingButtons() {
        val selectorItems = createSelectorItems()
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            SingleChoiceDialog.Builder()
                .title("TestDialogTitle")
                .selectorItems(selectorItems)
                .requestKey("TestDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.BUTTON_IS_NOT_EXIST, exception.message)
    }

    /**
     * ボタンがすべて空文字の場合
     */
    @Test
    fun inputBlankWordOnButtons() {
        val selectorItems = createSelectorItems()
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SingleChoiceDialog.Builder()
                .title("TestDialogTitle")
                .selectorItems(selectorItems)
                .positiveLabel("")
                .negativeLabel("")
                .neutralLabel("")
                .requestKey("TestDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.BUTTON_IS_NOT_EXIST, exception.message)
    }

    /**
     * リクエストキーが存在しない場合
     */
    @Test
    fun missingRequestKey() {
        val selectorItems = createSelectorItems()
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            SingleChoiceDialog.Builder()
                .title("TestDialogTitle")
                .selectorItems(selectorItems)
                .positiveLabel("OK")
                .negativeLabel("Cancel")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.REQUEST_KEY_MISSING, exception.message)
    }

    /**
     * リクエストキーに空文字を指定した場合
     */
    @Test
    fun inputEmptyRequestKey() {
        val selectorItems = createSelectorItems()
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SingleChoiceDialog.Builder()
                .title("TestDialogTitle")
                .selectorItems(selectorItems)
                .positiveLabel("OK")
                .negativeLabel("Cancel")
                .requestKey("")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.REQUEST_KEY_MISSING, exception.message)
    }

    private fun createSelectorItems(): ArrayList<SelectorItem> {
        val selectorItems = ArrayList<SelectorItem>()
        for (i in 0..3) {
            val item = SelectorItem(text = "SampleText$i")
            selectorItems.add(item)
        }
        return selectorItems
    }
}