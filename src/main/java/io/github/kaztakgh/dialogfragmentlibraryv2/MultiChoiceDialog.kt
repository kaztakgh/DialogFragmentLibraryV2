package io.github.kaztakgh.dialogfragmentlibraryv2

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 複数選択のダイアログ
 *
 * 選択肢をテキストまたは画像(Drawable)で表示可能
 *
 * 選択肢の内容はSelectorItemクラスで指定したArrayListで定義すること
 */
class MultiChoiceDialog : BaseDialog() {
    /**
     * ダイアログに適用するアダプター
     *
     * 結果送信時、回転時に選択肢を復元する際に使用する
     */
    private lateinit var dialogAdapter: MultiChoiceItemAdapter

    companion object {
        fun newInstance() = MultiChoiceDialog()

        /**
         * 列数
         */
        const val DIALOG_COLUMN_NUM = "columns"

        /**
         * 選択肢リスト
         */
        const val DIALOG_SELECTORS_LIST = "selectorsList"

        /**
         * 選択していたチェック状態
         */
        const val SAVED_CHECKED_LIST = "checkedList"

        /**
         * 押下ボタンで決定した際に返すisCheckedのリストのキー
         */
        const val CHECK_STATE_LIST = "checkStateList"
    }

    /**
     * 複数選択系ダイアログのビルド関数群
     *
     * 関数に関しては各派生先で実装すること
     */
    class Builder: BaseDialog.Builder() {
        private var title: String = ""
        private var columnNum: Int = 1
        private var selectorItems: ArrayList<SelectorItem>? = null
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
         * 列数を指定
         *
         * @param num 列数
         */
        fun columnNum(num: Int): Builder = apply { this.columnNum = num }

        /**
         * 選択肢となるアイテムを指定
         *
         * 列数が1の場合はテキスト指定を推奨
         *
         * 列数が2以上の場合はアイコン指定を推奨
         *
         * @param items 1つの選択肢に表示するテキスト、アイコンなど(SelectorItemクラス参照)
         */
        fun selectorItems(items: ArrayList<SelectorItem>): Builder = apply { this.selectorItems = items }

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
         * 表示する内容を決定して、ダイアログのビルドを実行する
         *
         * @throws IllegalArgumentException タイトル、選択肢、リクエストキーのいずれかが存在しないか、
         * positiveLabel、negativeLabel、neutralLabelの設定が1つもない場合に発生
         */
        override fun build(): MultiChoiceDialog {
            // タイトルが存在しない場合
            if (this.title.isEmpty() || this.title.isBlank()) {
                throw IllegalArgumentException(ErrorMessage.TITLE_MISSING)
            }
            // 選択肢が存在しない場合
            if (this.selectorItems == null || this.selectorItems!!.isEmpty()) {
                throw IllegalArgumentException(ErrorMessage.SELECTOR_ITEM_IS_EMPTY)
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
            val inputArgs: Bundle = Bundle().also {
                it.putString(DIALOG_TITLE, this.title)
                it.putInt(DIALOG_COLUMN_NUM, this.columnNum)
                it.putParcelableArrayList(DIALOG_SELECTORS_LIST, this.selectorItems)
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
        val view: View = layoutInflater.inflate(
            R.layout.layout_select_dialog,
            null,
            false
        )
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val rvSelect = view.findViewById<RecyclerView>(R.id.rvSelect)
        val btnPositive = view.findViewById<Button>(R.id.btnPositive)
        val btnNegative = view.findViewById<Button>(R.id.btnNegative)
        val btnNeutral = view.findViewById<Button>(R.id.btnNeutral)

        // タイトルは設定した場合のみ表示する
        if (arguments?.getString(DIALOG_TITLE)!!.isNotBlank()) {
            tvTitle.text = arguments?.getString(DIALOG_TITLE)
        }
        else {
            tvTitle.visibility = View.GONE
        }

        // RecyclerViewの設定
        val columnNum = arguments?.getInt(DIALOG_COLUMN_NUM, 1)!!
        val selectorItemArrayList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList(DIALOG_SELECTORS_LIST, SelectorItem::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList(DIALOG_SELECTORS_LIST)
        }
        if (savedInstanceState?.getBooleanArray(SAVED_CHECKED_LIST) != null) {
            selectorItemArrayList!!.forEachIndexed { index, selectorItem ->
                selectorItem.isChecked = savedInstanceState.getBooleanArray(SAVED_CHECKED_LIST)!![index]
            }
        }
        this.dialogAdapter = MultiChoiceItemAdapter(
            selectorItemArrayList as ArrayList<SelectorItem>,
            columnNum
        )
        val layoutManager: RecyclerView.LayoutManager = if (columnNum > 1)
            GridLayoutManager(requireContext(), columnNum)
        else LinearLayoutManager(requireContext())
        rvSelect.layoutManager = layoutManager
        rvSelect.adapter = this.dialogAdapter

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
            .setCancelable(arguments?.getBoolean(DIALOG_IS_CANCELABLE)!!)
            .create()
        isCancelable = arguments?.getBoolean(DIALOG_IS_CANCELABLE)!!
        dialog.setCanceledOnTouchOutside(arguments?.getBoolean(DIALOG_IS_CANCELABLE)!!)
        return dialog
    }

    /**
     * 画面回転などでダイアログ再生成が必要な場合に現在の状態を保存する
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBooleanArray(SAVED_CHECKED_LIST, this.getAdapterCheckBoxStateList().toBooleanArray())
    }

    /**
     * ダイアログの選択を行ったときに応答を呼び出し元のクラスに返す
     *
     * ダイアログによって内容が変化する
     *
     * @param buttonVal 押下ボタンの判定(-1: positive, -2: negative, -3: neutral)
     */
    override fun onButtonClick(buttonVal: Int) {
        Log.d(javaClass.simpleName, "(onButtonClick)$buttonVal")
        super.onButtonClick(buttonVal)
    }

    /**
     * Activityから呼び出したときのコールバック処理を指定する。
     *
     * bundle.getBooleanArray(CHECK_STATE_LIST)で選択した内容を取得することができる。
     *
     * @param buttonVal 押下ボタンの判定(-1: positive, -2: negative, -3: neutral)
     */
    override fun sendDialogResultToActivity(buttonVal: Int) {
        val selectedBundle = Bundle()
        selectedBundle.putBooleanArray(CHECK_STATE_LIST, this.getAdapterCheckBoxStateList().toBooleanArray())
        this.selectListener!!.receiveResultFromDialog(
            arguments?.getString(DIALOG_REQUEST_KEY)!!,
            buttonVal,
            selectedBundle
        )
    }

    /**
     * Fragmentから呼び出した時のコールバック処理を指定する。
     *
     * 呼び出し元でsetFragmentResultListenerの定義が必要。
     *
     * @param buttonVal 押下ボタンの判定(-1: positive, -2: negative, -3: neutral)
     */
    override fun sendDialogResultToFragment(buttonVal: Int) {
        val resultArgs = Bundle()
        resultArgs.putInt(DIALOG_RESULT_KEY, buttonVal)
        resultArgs.putBooleanArray(CHECK_STATE_LIST, this.getAdapterCheckBoxStateList().toBooleanArray())
        Log.d(javaClass.simpleName, "(sendDialogResultToFragment)$buttonVal")
        setFragmentResult(
            arguments?.getString(DIALOG_REQUEST_KEY)!!,
            resultArgs
        )
    }

    /**
     * 現在の選択状態を取得する
     *
     * @return MultiSelectorItemAdapter.checkBoxStateList参照
     */
    private fun getAdapterCheckBoxStateList(): List<Boolean> = this.dialogAdapter.checkBoxStateList
}