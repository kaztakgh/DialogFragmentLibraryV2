package io.github.kaztakgh.dialogfragmentlibraryv2

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectItemAdapter(
    private var selectorItemArrayList: ArrayList<SelectorItem>,
    private var columnNum: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * 選択したときの処理の記述用の関数
     */
    interface OnSelectorItemClickListener {
        fun onItemClick(item: SelectorItem)
    }
    private lateinit var listener: OnSelectorItemClickListener

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
        val layoutId = if (this.columnNum > 1)
                R.layout.layout_select_item_multiple_column
            else
                R.layout.layout_select_item_one_column
        val columnView = layoutInflater.inflate(
            layoutId,
            parent,
            false
        )
        return SelectOnceItemViewHolder(columnView)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int  = this.selectorItemArrayList.count()

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
        val viewHolder: SelectOnceItemViewHolder = holder as SelectOnceItemViewHolder
        this.bindViewHolder(viewHolder, item)
    }

    /**
     * 画面へのデータ埋め込み
     *
     * @param holder ViewHolder
     * @param item 各選択肢の要素
     */
    private fun bindViewHolder(
        holder: SelectOnceItemViewHolder,
        item: SelectorItem
    ) {
        // データの埋め込み
        holder.selectText = item.text
        holder.selectIcon = item.icon
        holder.isEnabled = item.isSelectable
        // こちら側でダイアログに通達する方法はある?
        if (holder.isEnabled) {
            holder.itemView.setOnClickListener {
                this.listener.onItemClick(item)
            }
        }
    }

    /**
     * 呼び出し元から選択した際の処理を記述する
     */
    fun setOnSelectorItemClickListener(listener: OnSelectorItemClickListener) {
        this.listener = listener
    }

    /**
     * レイアウトに対する出力内容を指定する
     *
     * @param view 表示対象レイアウト
     */
    class SelectOnceItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val ivIcon = view.findViewById<ImageView>(R.id.ivIcon)
        private val tvSelectText = view.findViewById<TextView>(R.id.tvSelectText)
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
         * 選択可能状態
         */
        var isEnabled: Boolean
            get() = (this.ivIcon.alpha == this.enabledAlpha || this.tvSelectText.alpha == this.enabledAlpha)
            set(value) {
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
}