package io.github.kaztakgh.dialogfragmentlibraryv2

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 時刻選択ダイアログ
 */
class TimeSelectDialog : BaseDialog(), OnTimeSetListener {
    companion object {
        fun newInstance() : TimeSelectDialog = TimeSelectDialog()

        /**
         * 選択した時刻
         */
        const val DIALOG_SELECT_TIME = "select_time"

        /**
         * 24時間表記
         */
        const val DIALOG_DISPLAY_24H = "display_24h"
    }

    /**
     * 時刻決定時の動作指定をActivity/Fragment上で定義する
     */
    interface OnTimeSelectListener {
        /**
         * 時刻決定時の動作定義
         *
         * @param reqKey 選択したダイアログのリクエストキー
         * @param hour 時(24時間表記)
         * @param minute 分
         */
        fun onTimeSelect(reqKey: String, hour: Int, minute: Int)
    }

    /**
     * 時刻選択時に反応するリスナー
     */
    private lateinit var listener: OnTimeSelectListener

    /**
     * 時刻選択系ダイアログのビルド関数群
     *
     * 関数に関しては各派生先で実装すること
     */
    class Builder: BaseDialog.Builder() {
        private var time: LocalTime = LocalTime.now()
        private var display24h: Boolean = true

        /**
         * 初期状態で選択されている時刻を指定する
         *
         * @param time 時刻(HH:ii:ss形式)
         */
        fun time(time: LocalTime): Builder {
            return apply { this.time = time }
        }

        /**
         * 24時間表記を行うか
         *
         * 指定しない場合は24時間表記
         *
         * @param isDisplay 24時間表記を行うか
         */
        fun display24h(isDisplay: Boolean): Builder {
            return apply { this.display24h = isDisplay }
        }

        /**
         * どのダイアログからの回答かを判別する文字列
         *
         * 空文字列は入力不可
         *
         * @param key リクエストキー
         */
        override fun requestKey(key: String): Builder = apply { this.reqKey = key }

        /**
         * ダイアログを判別するためのタグ(文字列)
         *
         * @param tag タグ
         */
        override fun tag(tag: String): Builder = apply { this.tagName = tag }

        /**
         * ダイアログ外でキャンセルの可否設定
         *
         * setCancelableで設定を反映させる
         *
         * @param cancelable キャンセル可能か
         */
        fun isCancelable(cancelable: Boolean): Builder = apply { this.cancelable = cancelable }

        /**
         * 表示する内容を決定して、ダイアログのビルドを実行する
         *
         * @throws IllegalArgumentException リクエストキーが存在しない場合
         */
        override fun build(): TimeSelectDialog {
            // リクエストキーが存在しない場合
            if (this.reqKey.isEmpty() || this.reqKey.isBlank()) {
                throw IllegalArgumentException(ErrorMessage.REQUEST_KEY_MISSING)
            }
            val inputArgs = Bundle().also {
                it.putString(DIALOG_SELECT_TIME, this.time.toString())
                it.putBoolean(DIALOG_DISPLAY_24H, this.display24h)
                it.putString(DIALOG_REQUEST_KEY, this.reqKey)
                it.putString(DIALOG_TAG, this.tagName)
                it.putBoolean(DIALOG_IS_CANCELABLE, this.cancelable)
            }
            val dialog = newInstance()
            dialog.arguments = inputArgs
            return dialog
        }
    }

    /**
     * ダイアログの表示を行うときの初期動作
     *
     * TimeSelectListenerで値を呼び出し元に通知するように設定する
     *
     * @param context コンテキスト
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTimeSelectListener) {
            this.listener = context
        }
    }

    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * [.onCreateView] does not need
     * to be implemented since the AlertDialog takes care of its own content.
     *
     *
     * This method will be called after [.onCreate] and
     * immediately before [.onCreateView].  The
     * default implementation simply instantiates and returns a [Dialog]
     * class.
     *
     *
     * *Note: DialogFragment own the [ Dialog.setOnCancelListener][Dialog.setOnCancelListener] and
     * [ Dialog.setOnDismissListener][Dialog.setOnDismissListener] callbacks.
     * You must not set them yourself.*
     * To find out about these events, override [.onCancel]
     * and [.onDismiss].
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inputTime: LocalTime = LocalTime.parse(
            arguments?.getString(DIALOG_SELECT_TIME),
            DateTimeFormatter.ISO_TIME
        )
        val dialog = TimePickerDialog(
            context,
            this,
            inputTime.hour,
            inputTime.minute,
            arguments?.getBoolean(DIALOG_DISPLAY_24H, true)!!
        )
        dialog.setCanceledOnTouchOutside(arguments?.getBoolean(DIALOG_IS_CANCELABLE, true)!!)
        dialog.setCancelable(arguments?.getBoolean(DIALOG_IS_CANCELABLE, true)!!)
        return dialog
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        this.listener.onTimeSelect(
            arguments?.getString(DIALOG_REQUEST_KEY)!!,
            hour,
            minute
        )
    }
}