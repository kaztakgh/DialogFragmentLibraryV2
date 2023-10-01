package io.github.kaztakgh.dialogfragmentlibraryv2

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 日付選択ダイアログ
 *
 * 独自のviewを適用するとDatePickerDialogの機能が利用できなくなる?
 */
class DateSelectDialog : BaseDialog(), OnDateSetListener {
    companion object {
        fun newInstance() : DateSelectDialog = DateSelectDialog()

        /**
         * 選択した日付
         */
        const val DIALOG_SELECT_DATE = "select_date"
    }

    /**
     * 日付決定時の動作指定をActivity/Fragment上で定義する
     */
    interface OnDateSelectListener {
        /**
         * 日付決定時の動作指定
         * @param reqKey 選択したダイアログのリクエストキー
         * @param year 年
         * @param month 月
         * @param dayOfMonth 日
         */
        fun onDateSelect(reqKey: String, year: Int, month: Int, dayOfMonth: Int)
    }

    /**
     * 日付選択時に反応するリスナー
     */
    private lateinit var listener: OnDateSelectListener

    /**
     * 日付選択系ダイアログのビルド関数群
     * 関数に関しては各派生先で実装すること
     */
    class Builder: BaseDialog.Builder() {
        private var date: LocalDate = LocalDate.now()

        /**
         * 初期状態で選択されている日付を指定する
         *
         * @param date 日付(YYYY-MM-DD)
         */
        fun date(date: LocalDate): Builder {
            return apply { this.date = date }
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
        override fun build(): DateSelectDialog {
            // リクエストキーが存在しない場合
            if (this.reqKey.isEmpty() || this.reqKey.isBlank()) {
                throw IllegalArgumentException(ErrorMessage.REQUEST_KEY_MISSING)
            }
            val inputArgs = Bundle().also {
                it.putString(DIALOG_SELECT_DATE, this.date.toString())
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
     * DateSelectListenerで値を呼び出し元に通知するように設定する
     *
     * @param context コンテキスト
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDateSelectListener) {
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
        val inputDate = LocalDate.parse(
            arguments?.getString(DIALOG_SELECT_DATE),
            DateTimeFormatter.ISO_DATE
        )
        val context = context

        return if (context != null) {
            // DatePickerDialog上では月の数値が実際の数値より1少ないため、
            // 1引かないと意図した日付を示さない
            val dialog = DatePickerDialog(
                context,
                this,
                inputDate.year,
                inputDate.month.value - 1,
                inputDate.dayOfMonth
            )
            dialog.setCanceledOnTouchOutside(arguments?.getBoolean(DIALOG_IS_CANCELABLE, true)!!)
            dialog.setCancelable(arguments?.getBoolean(DIALOG_IS_CANCELABLE, true)!!)
            dialog
        } else {
            super.onCreateDialog(savedInstanceState)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // この関数上のmonthはCalendar型を参照しているため、LocalDateに対応させるために
        // monthに1を追加する
        this.listener.onDateSelect(
            arguments?.getString(DIALOG_REQUEST_KEY)!!,
            year,
            month + 1,
            dayOfMonth
        )
    }
}