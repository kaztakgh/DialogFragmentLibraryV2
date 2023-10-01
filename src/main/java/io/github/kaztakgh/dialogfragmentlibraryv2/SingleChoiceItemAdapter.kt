package io.github.kaztakgh.dialogfragmentlibraryv2

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

/**
 * 単一選択ダイアログに指定した選択肢の内容を反映させる
 *
 * @param selectorItemArrayList 選択肢リスト
 * @param columnNum 表示列数
 */
class SingleChoiceItemAdapter(
    private var selectorItemArrayList: ArrayList<SelectorItem>,
    private var columnNum: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var lastClickPos: Int = -1

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        // カラム数によって使用するレイアウトを変更
        when {
            this.columnNum > 1 -> {
                val multiColumnView = layoutInflater.inflate(
                    R.layout.layout_single_choice_multiple_column,
                    parent,
                    false
                )
                return MultipleColumnViewHolder(multiColumnView)
            }
            else -> {
                val singleColumnView = layoutInflater.inflate(
                    R.layout.layout_single_choice_one_column,
                    parent,
                    false
                )
                return SingleColumnViewHolder(singleColumnView)
            }
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = this.selectorItemArrayList.count()

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: SelectorItem = this.selectorItemArrayList[position]
        when {
            this.columnNum > 1 -> {
                val multipleColumnViewHolder = holder as MultipleColumnViewHolder
                bindMultipleColumnViewHolder(multipleColumnViewHolder, item)
            }
            else -> {
                val singleColumnViewHolder = holder as SingleColumnViewHolder
                bindSingleColumnViewHolder(singleColumnViewHolder, item)
            }
        }
    }

    /**
     * 1列の場合の画面へのデータ埋め込み
     *
     * @param holder 1列の場合のViewHolder
     * @param item 各選択肢の要素
     */
    private fun bindSingleColumnViewHolder(
        holder: SingleColumnViewHolder,
        item: SelectorItem
    ) {
        // データの埋め込み
        holder.selectText = item.text
        holder.selectIcon = item.icon
        holder.isChecked = item.isChecked
        holder.isEnabled = item.isSelectable

        // ラジオボタンまたは選択肢のテキストをクリックしたときの処理
        if (holder.isEnabled) {
            holder.rbState.setOnClickListener {
                val clickPos: Int = holder.adapterPosition
                this.changeRadioButtonCheck(clickPos)
            }
            holder.tvSelectText.setOnClickListener {
                val clickPos: Int = holder.adapterPosition
                this.changeRadioButtonCheck(clickPos)
            }
        }
    }

    /**
     * 2列以上の場合の画面へのデータ埋め込み
     *
     * @param holder 2列以上の場合のViewHolder
     * @param item 各選択肢の要素
     */
    private fun bindMultipleColumnViewHolder(
        holder: MultipleColumnViewHolder,
        item: SelectorItem
    ) {
        // データの埋め込み
        holder.selectText = item.text
        holder.selectIcon = item.icon
        holder.isChecked = item.isChecked
        holder.isEnabled = item.isSelectable

        // アイコンまたは選択肢のテキストをクリックしたときの処理
        if (holder.isEnabled) {
            holder.ivIcon.setOnClickListener {
                val clickPos: Int = holder.adapterPosition
                this.changeRadioButtonCheck(clickPos)
            }
            holder.tvSelectText.setOnClickListener {
                val clickPos: Int = holder.adapterPosition
                this.changeRadioButtonCheck(clickPos)
            }
        }
    }

    /**
     * ラジオボタンの表示の切り替え
     *
     * @param clickItemPos クリックした選択肢の箇所。selectorItemArrayListの順序に対応。
     */
    private fun changeRadioButtonCheck(clickItemPos: Int) {
        // 前回クリック位置が初期状態の場合
        if (this.lastClickPos == -1) {
            // 現在のチェック状態を取得
            this.lastClickPos = this.radioButtonCheckPosition
        }
        // 前回クリック箇所と異なる場合
        if (clickItemPos != this.lastClickPos) {
            if (this.lastClickPos != -1) {
                this.selectorItemArrayList[this.lastClickPos].isChecked = false
                notifyItemChanged(this.lastClickPos)
            }
            this.selectorItemArrayList[clickItemPos].isChecked = true
            notifyItemChanged(clickItemPos)
        }
        this.lastClickPos = clickItemPos
    }

    /**
     * SingleSelectDialogでの選択しているものの位置を取得する
     *
     * @return isCheck=trueの位置。見つからない場合は-1を返す。
     */
    internal val radioButtonCheckPosition: Int
        get() {
            val isCheckList: List<Boolean> = this.selectorItemArrayList.map { it.isChecked }
            return isCheckList.indexOf(true)
        }

    /**
     * レイアウトに対する出力内容を指定する(1列)
     *
     * @param view 表示対象レイアウト
     */
    class SingleColumnViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val rbState: RadioButton = view.findViewById(R.id.rbState)
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvSelectText: TextView = view.findViewById(R.id.tvSelectText)
        private val enabledAlpha = 1.0f
        private val disabledAlpha = 0.5f

        /**
         * 選択肢テキスト
         */
        var selectText: String
            get() = this.tvSelectText.text.toString()
            set(value) {
                if (value.isNotBlank() || value.isNotEmpty()) this.tvSelectText.text = value
                else this.tvSelectText.visibility = View.GONE
            }

        /**
         * 選択肢画像
         */
        var selectIcon: Drawable?
            get() = this.ivIcon.drawable
            set(value) {
                if (value != null) this.ivIcon.setImageDrawable(value)
                else this.ivIcon.visibility = View.GONE
            }

        /**
         * 選択状態
         */
        var isChecked: Boolean
            get() = this.rbState.isChecked
            set(value) {
                this.rbState.isChecked = value
            }

        /**
         * 選択可能状態
         */
        var isEnabled: Boolean
            get() = this.rbState.isEnabled
            set(value) {
                this.rbState.isEnabled = value
                // 状態によって、テキストとアイコンの表示状態を変更する
                if (value) {
                    this.ivIcon.alpha = this.enabledAlpha
                    this.tvSelectText.alpha = this.enabledAlpha
                }
                else {
                    this.ivIcon.alpha = this.disabledAlpha
                    this.tvSelectText.alpha = this.disabledAlpha
                }
            }
    }

    /**
     * レイアウトに対する出力内容を指定する(2列以上)
     *
     * @param view 表示対象レイアウト
     */
    class MultipleColumnViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val rbState: RadioButton = view.findViewById(R.id.rbState)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvSelectText: TextView = view.findViewById(R.id.tvSelectText)
        private val layoutBg: ConstraintLayout = view.findViewById(R.id.layoutBackGround)
        private val viewContext: Context = view.context
        private val enabledAlpha = 1.0f
        private val disabledAlpha = 0.5f

        /**
         * 選択肢テキスト
         */
        var selectText: String
            get() = this.tvSelectText.text.toString()
            set(value) {
                this.tvSelectText.text = value
            }

        /**
         * 選択肢画像
         */
        var selectIcon: Drawable?
            get() = this.ivIcon.drawable
            set(value) {
                if (value != null) this.ivIcon.setImageDrawable(value) else this.ivIcon.visibility = View.GONE
            }

        /**
         * 選択状態
         */
        var isChecked: Boolean
            get() = this.rbState.isChecked
            set(value) {
                this.rbState.isChecked = value
                // 背景に選択状態を示すグリッドを表示させる
                if (value) this.layoutBg.background = AppCompatResources.getDrawable(viewContext, R.drawable.select_border)
                else this.layoutBg.background = null
            }

        /**
         * 選択可能状態
         */
        var isEnabled: Boolean
            get() = this.rbState.isEnabled
            set(value) {
                // 状態によって、テキストとアイコンの表示状態を変更する
                this.rbState.isEnabled = value
                if (value) {
                    this.ivIcon.alpha = this.enabledAlpha
                    this.tvSelectText.alpha = this.enabledAlpha
                }
                else {
                    this.ivIcon.alpha = this.disabledAlpha
                    this.tvSelectText.alpha = this.disabledAlpha
                }
            }
    }
}