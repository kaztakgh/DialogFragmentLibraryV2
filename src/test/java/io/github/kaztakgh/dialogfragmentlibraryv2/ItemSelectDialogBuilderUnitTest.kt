package io.github.kaztakgh.dialogfragmentlibraryv2

import android.os.Bundle
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Bundle::class)
class ItemSelectDialogBuilderUnitTest {
    private val emptyItems = ArrayList<SelectorItem>()
    /**
     * 正しく入力された場合
     */
    @Test
    fun inputCorrect() {
        val selectorItems = createSelectorItems()
        assertDoesNotThrow {
            ItemSelectDialog.Builder()
                .title("TestDialogTitle")
                .selectorItems(selectorItems)
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
            ItemSelectDialog.Builder()
                .title("TestDialogTitle")
                .columnNum(1)
                .selectorItems(selectorItems)
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
            ItemSelectDialog.Builder()
                .selectorItems(selectorItems)
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
            ItemSelectDialog.Builder()
                .title("")
                .selectorItems(selectorItems)
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
            ItemSelectDialog.Builder()
                .title("TestDialogTitle")
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
            ItemSelectDialog.Builder()
                .title("TestDialogTitle")
                .selectorItems(emptyItems)
                .requestKey("TestDialog")
                .build()
        }
        Assertions.assertEquals(ErrorMessage.SELECTOR_ITEM_IS_EMPTY, exception.message)
    }

    /**
     * リクエストキーが存在しない場合
     */
    @Test
    fun missingRequestKey() {
        val selectorItems = createSelectorItems()
        val exception = assertThrows(java.lang.IllegalArgumentException::class.java) {
            ItemSelectDialog.Builder()
                .title("TestDialogTitle")
                .selectorItems(selectorItems)
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
            ItemSelectDialog.Builder()
                .title("TestDialogTitle")
                .selectorItems(selectorItems)
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