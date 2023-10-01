package io.github.kaztakgh.dialogfragmentlibraryv2

/**
 * エラーメッセージの定義
 */
internal object ErrorMessage {
    /**
     * タイトルが存在しない場合
     */
    const val TITLE_MISSING = "Title is required."

    /**
     * テキスト(本文)が空文字の場合
     *
     * MessageDialogのみに使用する
     */
    const val TEXT_MISSING = "Text Message is required."

    /**
     * リクエストキーが定義されていない場合
     */
    const val REQUEST_KEY_MISSING = "Request key can't enter empty or blank word."

    /**
     * positiveLabel, negativeLabel, neutralLabelがどれも定義されていない場合
     */
    const val BUTTON_IS_NOT_EXIST = "Either positiveLabel(), negativeLabel(), or neutralLabel() must be defined"

    /**
     * negativeLabelが定義されていない場合
     *
     * ProgressDialogのみ使用
     */
    const val BUTTON_IS_NOT_EXIST_ON_PROGRESS_DIALOG = "negativeLabel() must be defined"

    /**
     * 選択系のダイアログで、selectorItemに入力した選択肢が存在しない場合
     */
    const val SELECTOR_ITEM_IS_EMPTY = "Function selectorItems() requires at least one SelectorItem element."

    /**
     * quantityMaxで0未満の数値を入力した場合
     *
     * ProgressDialogで使用
     */
    const val CANNOT_ENTER_LESS_THAN_ZERO = "Values less than 0 cannot be entered"
}