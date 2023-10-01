package io.github.kaztakgh.dialogfragmentlibraryv2

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable

/**
 * 選択肢のアイテム
 *
 * @param text テキスト
 * @param icon 画像
 * @param isChecked 選択状態
 * @param isSelectable 選択可能か
 */
data class SelectorItem(
    var text: String = "",
    var icon: Drawable? = null,
    var isChecked: Boolean = false,
    var isSelectable: Boolean = true
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        Bitmap.CREATOR.createFromParcel(parcel).toDrawable(Resources.getSystem()),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    init {
        // 入力チェック
        if (this.text.isBlank() && this.icon == null) {
            throw IllegalArgumentException("text or icon is required.")
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        val bitmap: Bitmap = icon?.toBitmap() ?: icon!!.toBitmap()
        parcel.writeString(text)
        parcel.writeParcelable(bitmap, flags)
        parcel.writeByte(if (isChecked) 1 else 0)
        parcel.writeByte(if (isSelectable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SelectorItem> {
        override fun createFromParcel(parcel: Parcel): SelectorItem {
            return SelectorItem(parcel)
        }

        override fun newArray(size: Int): Array<SelectorItem?> {
            return arrayOfNulls(size)
        }
    }
}
