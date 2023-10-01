package io.github.kaztakgh.dialogfragmentlibraryv2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * SelectorItemクラスのテスト
 * Robolectricで画像テストが可能
 */
@RunWith(RobolectricTestRunner::class)
class SelectorItemUnitTest {
    @Test
    fun inputTextCorrect() {
        val item = SelectorItem(text = "SampleItem")
        assertEquals("SampleItem", item.text)
    }

    // 画像の用意方法が不明
//    @Test
//    fun inputIcon() {
//        val drawable = ShadowNativeBitmapDrawable()
//        val item = SelectorItem(icon = drawable)
//        assertEquals(drawable, item.icon)
//    }

    @Test
    fun inputCheckedItem() {
        val item = SelectorItem(text = "SampleItem", isChecked = true)
        assertTrue(item.isChecked)
    }

    @Test
    fun inputUnselectableItem() {
        val item = SelectorItem(text = "SampleItem", isSelectable = false)
        assertFalse(item.isSelectable)
    }

    @Test
    fun inputTextNothing() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            SelectorItem(text = "")
        }
        assertEquals("text or icon is required.", exception.message)
    }
}