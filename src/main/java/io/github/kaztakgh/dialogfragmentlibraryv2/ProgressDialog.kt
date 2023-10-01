package io.github.kaztakgh.dialogfragmentlibraryv2

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
import androidx.core.content.ContextCompat.registerReceiver


/**
 * プログレスダイアログ
 *
 * 処理中で待つ場合に使用する
 */
class ProgressDialog : BaseDialog() {
    companion object {
        fun newInstance() = ProgressDialog()

        /**
         * 全体の処理量
         */
        const val DIALOG_MAX_QUANTITY = "quantity"

        /**
         * 更新アクション
         */
        const val DIALOG_UPDATE = "update"

        /**
         * 終了アクション
         */
        const val DIALOG_CLOSE = "close"

        /**
         * 進行状況
         *
         * 更新する場合、IntentにDIALOG_PROGRESSをキーとした数値のデータを指定する
         */
        const val DIALOG_PROGRESS = "progress"
    }

    // 変数
    private var quantityProgress = 0

    /**
     * 状況が更新された場合、表示を対応させる
     */
    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == DIALOG_UPDATE) {
                // 進行状況の更新
                val progress = intent.getIntExtra(DIALOG_PROGRESS, 0)
                update(progress)
            } else if (action == DIALOG_CLOSE) {
                // ダイアログを終了する
                dismissAllowingStateLoss()
            }
        }
    }

    /**
     * プログレスダイアログのビルド関数群
     *
     * 関数に関しては各派生先で実装すること
     */
    class Builder: BaseDialog.Builder() {
        private var text: String = ""
        private var quantity: Int = 0
        private var negativeLabel: String = ""

        /**
         * 表示中に出力させるメッセージ
         *
         * MessageDialogとは違い、必須ではない
         *
         * @param text メッセージ
         */
        fun message(text: String): Builder = apply { this.text = text }

        /**
         * 要素数の最大値
         *
         * 全て処理し終わったときに終了するので、最初から処理量が分かっている場合はここに要素数を
         * 入力する
         *
         * 0を入力した場合は処理量不明という扱いとする
         *
         * @param quantity 待機終了するための要素数
         */
        fun quantityMax(quantity: Int): Builder = apply { this.quantity = quantity }

        /**
         * キャンセルなど否定的な回答のボタンに表示するラベル
         *
         * @param label ラベル
         */
        fun negativeLabel(label: String): Builder = apply { this.negativeLabel = label }

        /**
         * どのダイアログからの回答かを判別する文字列
         *
         * 空文字列は入力不可
         *
         * @param key リクエストキー
         * @throws IllegalArgumentException 空文字の場合に発生
         */
        override fun requestKey(key: String): Builder = apply { this.reqKey = key }

        /**
         * ダイアログを判別するためのタグ(文字列)
         *
         * @param tag タグ
         */
        override fun tag(tag: String): Builder = apply { this.tagName = tag }

        /**
         * 表示する内容を決定して、ダイアログのビルドを実行する
         */
        override fun build(): ProgressDialog {
            if (this.quantity < 0) {
                throw IllegalArgumentException(ErrorMessage.CANNOT_ENTER_LESS_THAN_ZERO)
            }
            if (this.negativeLabel.isBlank() || this.negativeLabel.isEmpty()) {
                throw IllegalArgumentException(ErrorMessage.BUTTON_IS_NOT_EXIST_ON_PROGRESS_DIALOG)
            }
            if (this.reqKey.isEmpty() || this.reqKey.isBlank()) {
                throw IllegalArgumentException(ErrorMessage.REQUEST_KEY_MISSING)
            }
            val inputArgs = Bundle().also {
                it.putString(DIALOG_TEXT, text)
                it.putInt(DIALOG_MAX_QUANTITY, quantity)
                it.putString(DIALOG_NEGATIVE_BUTTON_LABEL, negativeLabel)
                it.putString(DIALOG_REQUEST_KEY, this.reqKey)
                it.putString(DIALOG_TAG, this.tagName)
            }
            val dialog = newInstance()
            dialog.arguments = inputArgs
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter()
        filter.addAction(DIALOG_UPDATE)
        filter.addAction(DIALOG_CLOSE)
        registerReceiver(requireContext(), this.receiver, filter, RECEIVER_NOT_EXPORTED)
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
        val view: View = layoutInflater.inflate(
            R.layout.layout_progress_bar_dialog,
            null,
            false
        )
        val message = arguments?.getString(DIALOG_TEXT, "")
        val quantityMax = arguments?.getInt(DIALOG_MAX_QUANTITY, 0)
        val negativeLabel = arguments?.getString(DIALOG_NEGATIVE_BUTTON_LABEL, "")
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val pbPercent = view.findViewById<ProgressBar>(R.id.pbPercent)
        val btnNegative = view.findViewById<Button>(R.id.btnNegative)
        view.findViewById<Button>(R.id.btnNeutral).visibility = View.GONE
        view.findViewById<Button>(R.id.btnPositive).visibility = View.GONE

        // パラメータに対する表示の設定
        if (message!!.isNotBlank()) {
            tvMessage.text = message
        }
        else {
            tvMessage.visibility = View.GONE
        }
        if (quantityMax != null && quantityMax > 0) {
            pbPercent.max = quantityMax
        }
        else {
            pbPercent.isIndeterminate = true
        }
        if (negativeLabel!!.isNotBlank()) {
            btnNegative.text = negativeLabel
            btnNegative.setOnClickListener {
                onButtonClick(DialogInterface.BUTTON_NEGATIVE)
                // ダイアログを終了する
                dismissAllowingStateLoss()
            }
        }
        else {
            btnNegative.visibility = View.GONE
        }

        // ダイアログの表示内容を決定する
        // キャンセルボタンでのみ閉じるようにする
        isCancelable = false
        val dialog: AlertDialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after [.onStop] and before [.onDetach].
     */
    override fun onDestroy() {
        super.onDestroy()
        this.close()
        // ダイアログを終了する
        dismissAllowingStateLoss()
    }

    /**
     * プログレスバーの進行状況の更新
     *
     * @param progress 進行状況
     */
    private fun update(progress: Int) {
        val quantityMax: Int = requireArguments().getInt(DIALOG_MAX_QUANTITY, 0)
        // プログレスバーのIndeterminateをtrueに設定している場合は更新しない
        if (quantityMax <= 0) {
            return
        }
        if (dialog == null) {
            return
        }

        // 表示内容の更新
        quantityProgress = progress
        val quantityPercent = progress * 100 / quantityMax
        val progressText = "$quantityProgress / $quantityMax"
        val percentText = "$quantityPercent%"
        val pbPercent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            dialog!!.requireViewById<ProgressBar>(R.id.pbPercent)
        } else {
            dialog!!.findViewById(R.id.pbPercent)
        }
        val tvQuantity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            dialog!!.requireViewById<TextView>(R.id.tvQuantity)
        } else {
            dialog!!.findViewById(R.id.tvQuantity)
        }
        val tvProgressPercent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            dialog!!.requireViewById<TextView>(R.id.tvProgressPercent)
        } else {
            dialog!!.findViewById(R.id.tvProgressPercent)
        }
        pbPercent.setProgress(quantityProgress, true)
        tvQuantity.text = progressText
        tvProgressPercent.text = percentText
    }

    /**
     * ダイアログを終了する
     *
     * ここでBroadCastReceiverの解除を行う
     */
    private fun close() {
        // BroadCastReceiverの解除
        requireActivity().unregisterReceiver(this.receiver)
    }
}