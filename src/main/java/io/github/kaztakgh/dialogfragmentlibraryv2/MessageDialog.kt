package io.github.kaztakgh.dialogfragmentlibraryv2

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

/**
 * メッセージを出力するダイアログ
 *
 * テキストのみ出力可能
 */
class MessageDialog : BaseDialog() {
    companion object {
        fun newInstance() = MessageDialog()
    }

    /**
     * メッセージ系ダイアログのビルド関数群
     *
     * 関数に関しては各派生先で実装すること
     */
    class Builder: BaseDialog.Builder() {
        private var title: String = ""
        private var text: String = ""
        private var positiveLabel: String = ""
        private var negativeLabel: String = ""
        private var neutralLabel: String = ""

        /**
         * タイトルを指定
         *
         * @param title 表示するタイトル
         */
        fun title(title: String): Builder = apply { this.title = title }

        /**
         * テキスト(本文)を指定
         *
         * @param text 表示するテキスト(本文)
         */
        fun text(text: String): Builder = apply { this.text = text }

        /**
         * OKなど肯定的な回答のボタンに表示するラベル
         *
         * @param label ラベル
         */
        fun positiveLabel(label: String): Builder = apply { this.positiveLabel = label }

        /**
         * キャンセルなど否定的な回答のボタンに表示するラベル
         *
         * @param label ラベル
         */
        fun negativeLabel(label: String): Builder = apply { this.negativeLabel = label }

        /**
         * 保留など中立的な回答のボタンに表示するラベル
         *
         * @param label ラベル
         */
        fun neutralLabel(label: String): Builder = apply { this.neutralLabel = label }

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
         * ダイアログの表示内容を確定する
         *
         * @throws IllegalArgumentException タイトル、テキスト(本文)、リクエストキーのいずれかが存在しないか、
         * positiveLabel、negativeLabel、neutralLabelの設定が1つもない場合に発生
         */
        override fun build(): MessageDialog {
            // タイトルが存在しない場合
            if (this.title.isEmpty() || this.title.isBlank()) {
                throw IllegalArgumentException(ErrorMessage.TITLE_MISSING)
            }
            // テキスト(本文)が存在しない場合
            if (this.text.isEmpty() || this.text.isBlank()) {
                throw IllegalArgumentException(ErrorMessage.TEXT_MISSING)
            }
            // リクエストキーが存在しない場合
            if (this.reqKey.isEmpty() || this.reqKey.isBlank()) {
                throw IllegalArgumentException(ErrorMessage.REQUEST_KEY_MISSING)
            }
            // ボタン用のラベルが存在しない場合
            if ((this.positiveLabel.isEmpty() || this.positiveLabel.isBlank())
                && (this.negativeLabel.isEmpty() || this.negativeLabel.isBlank())
                && (this.neutralLabel.isEmpty() || this.neutralLabel.isBlank())) {
                throw IllegalArgumentException(ErrorMessage.BUTTON_IS_NOT_EXIST)
            }
            val inputArgs = Bundle().also {
                it.putString(DIALOG_TITLE, this.title)
                it.putString(DIALOG_TEXT, this.text)
                it.putString(DIALOG_POSITIVE_BUTTON_LABEL, this.positiveLabel)
                it.putString(DIALOG_NEGATIVE_BUTTON_LABEL, this.negativeLabel)
                it.putString(DIALOG_NEUTRAL_BUTTON_LABEL, this.neutralLabel)
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
        // レイアウトの読み込み
        // onCreateViewを利用するとダイアログ自体が表示されなくなるため、ここでViewの設定を行う
        val view: View = layoutInflater.inflate(
            R.layout.layout_message_dialog,
            null,
            false
        )
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessageText)
        val btnPositive = view.findViewById<Button>(R.id.btnPositive)
        val btnNegative = view.findViewById<Button>(R.id.btnNegative)
        val btnNeutral = view.findViewById<Button>(R.id.btnNeutral)

        tvTitle.text = arguments?.getString(DIALOG_TITLE)
        tvMessage.text = arguments?.getString(DIALOG_TEXT)

        // ボタンのラベルを指定していない場合、ボタンを表示させない
        if (arguments?.getString(DIALOG_POSITIVE_BUTTON_LABEL)!!.isNotBlank()) {
            btnPositive.text = arguments?.getString(DIALOG_POSITIVE_BUTTON_LABEL)
            btnPositive.setOnClickListener {
                onButtonClick(DialogInterface.BUTTON_POSITIVE)
            }
        } else {
            btnPositive.visibility = View.GONE
        }
        if (arguments?.getString(DIALOG_NEGATIVE_BUTTON_LABEL)!!.isNotBlank()) {
            btnNegative.text = arguments?.getString(DIALOG_NEGATIVE_BUTTON_LABEL)
            btnNegative.setOnClickListener {
                onButtonClick(DialogInterface.BUTTON_NEGATIVE)
            }
        } else {
            btnNegative.visibility = View.GONE
        }
        if (arguments?.getString(DIALOG_NEUTRAL_BUTTON_LABEL)!!.isNotBlank()) {
            btnNeutral.text = arguments?.getString(DIALOG_NEUTRAL_BUTTON_LABEL)
            btnNeutral.setOnClickListener {
                onButtonClick(DialogInterface.BUTTON_NEUTRAL)
            }
        } else {
            btnNeutral.visibility = View.GONE
        }

        val dialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        isCancelable = arguments?.getBoolean(DIALOG_IS_CANCELABLE)!!
        dialog.setCanceledOnTouchOutside(arguments?.getBoolean(DIALOG_IS_CANCELABLE)!!)
        return dialog
    }
}