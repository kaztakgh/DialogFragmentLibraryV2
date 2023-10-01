package io.github.kaztakgh.dialogfragmentlibraryv2

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * アイテムを選択するとダイアログが閉じる選択肢
 *
 * 選択肢をテキストまたは画像(Drawable)で表示可能
 *
 * 選択肢の内容はSelectorItemクラスで指定したArrayListで定義すること
 */
class ItemSelectDialog : BaseDialog() {
    /**
     * ダイアログに適用するアダプター
     *
     * 結果送信時、回転時に選択肢を復元する際に使用する
     */
    private lateinit var dialogAdapter: SelectItemAdapter

    companion object {
        fun newInstance() = ItemSelectDialog()

        /**
         * 列数
         */
        const val DIALOG_COLUMN_NUM = "columns"

        /**
         * 選択肢リスト
         */
        const val DIALOG_SELECTORS_LIST = "selectorsList"

        /**
         * 押下ボタンで決定した際に返すインデックスのキー
         */
        const val SELECTED_INDEX = "selectedIndex"
    }

    /**
     * 1回限りの選択系ダイアログのビルド関数群
     *
     * 関数に関しては各派生先で実装すること
     */
    class Builder: BaseDialog.Builder() {
        private var title: String = ""
        private var columnNum: Int = 1
        private var selectorItems: ArrayList<SelectorItem>? = null

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
         * @throws IllegalArgumentException 選択肢となる要素が1つもない場合に発生する
         */
        fun selectorItems(items: ArrayList<SelectorItem>): Builder = apply { this.selectorItems = items }

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
         * @throws IllegalArgumentException 選択肢が1つも存在しないか、リクエストキーが存在しない場合に発生
         */
        override fun build(): ItemSelectDialog {
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
            val inputArgs: Bundle = Bundle().also {
                it.putString(DIALOG_TITLE, this.title)
                it.putInt(DIALOG_COLUMN_NUM, this.columnNum)
                it.putParcelableArrayList(DIALOG_SELECTORS_LIST, this.selectorItems)
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
        view.findViewById<Button>(R.id.btnPositive).visibility = View.GONE
        view.findViewById<Button>(R.id.btnNegative).visibility = View.GONE
        view.findViewById<Button>(R.id.btnNeutral).visibility = View.GONE
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val rvSelect = view.findViewById<RecyclerView>(R.id.rvSelect)
        // タイトルは設定した場合のみ表示する
        if (arguments?.getString(DIALOG_TITLE)!!.isNotBlank()) {
            tvTitle.text = arguments?.getString(DIALOG_TITLE)
        }
        else {
            tvTitle.visibility = View.GONE
        }
        // 結果を返すための変数は別途定義する必要あり
        val isCalledFromFragment = this.isCalledFromFragment

        // RecyclerViewの設定
        val columnNum = arguments?.getInt(SingleChoiceDialog.DIALOG_COLUMN_NUM, 1)!!
        val selectorItemArrayList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList(SingleChoiceDialog.DIALOG_SELECTORS_LIST, SelectorItem::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList(SingleChoiceDialog.DIALOG_SELECTORS_LIST)
        }
        this.dialogAdapter = SelectItemAdapter(
            selectorItemArrayList as ArrayList<SelectorItem>,
            columnNum
        )
        this.dialogAdapter.setOnSelectorItemClickListener(
            object : SelectItemAdapter.OnSelectorItemClickListener {
                override fun onItemClick(item: SelectorItem) {
                    val position: Int = selectorItemArrayList.indexOf(item)
                    // 押されたことがわかったら呼び出し元に結果を返す
                    if (isCalledFromFragment) {
                        sendResultToFragment(position)
                    }
                    else {
                        sendResultToActivity(position)
                    }
                }
            }
        )
        val layoutManager: RecyclerView.LayoutManager = if (columnNum > 1)
            GridLayoutManager(requireContext(), columnNum)
        else LinearLayoutManager(requireContext())
        rvSelect.layoutManager = layoutManager
        rvSelect.adapter = this.dialogAdapter

        val dialog: AlertDialog =  AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(arguments?.getBoolean(DIALOG_IS_CANCELABLE)!!)
            .create()
        isCancelable = arguments?.getBoolean(DIALOG_IS_CANCELABLE)!!
        dialog.setCanceledOnTouchOutside(arguments?.getBoolean(DIALOG_IS_CANCELABLE)!!)
        return dialog
    }

    /**
     * Activityから呼び出したときのコールバック処理を指定する
     * @param position クリックしたアイテムのインデックス番号
     */
    private fun sendResultToActivity(position: Int) {
        dismiss()
        val resultArgs = Bundle()
        resultArgs.putInt(SELECTED_INDEX, position)
        this.selectListener!!.receiveResultFromDialog(
            arguments?.getString(DIALOG_REQUEST_KEY)!!,
            0,
            resultArgs
        )
    }

    /**
     * Fragmentから呼び出したときのコールバック処理を指定する
     * @param position クリックしたアイテムのインデックス番号
     */
    private fun sendResultToFragment(position: Int) {
        dismiss()
        val resultArgs = Bundle()
        resultArgs.putInt(SELECTED_INDEX, position)
        setFragmentResult(
            arguments?.getString(DIALOG_REQUEST_KEY)!!,
            resultArgs
        )
    }
}