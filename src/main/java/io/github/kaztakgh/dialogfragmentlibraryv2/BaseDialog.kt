package io.github.kaztakgh.dialogfragmentlibraryv2

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult

/**
 * ダイアログで共通する箇所をまとめたクラス
 */
open class BaseDialog : DialogFragment() {
    companion object {
        /**
         * タイトル
         */
        const val DIALOG_TITLE = "title"

        /**
         * テキスト(本文)
         */
        const val DIALOG_TEXT = "text"

        /**
         * 肯定ボタンラベル
         */
        const val DIALOG_POSITIVE_BUTTON_LABEL = "positiveLabel"

        /**
         * 否定ボタンラベル
         */
        const val DIALOG_NEGATIVE_BUTTON_LABEL = "negativeLabel"

        /**
         * 中立ボタンラベル
         */
        const val DIALOG_NEUTRAL_BUTTON_LABEL = "neutralLabel"

        /**
         * リクエストキー
         */
        const val DIALOG_REQUEST_KEY = "reqKey"

        /**
         * タグ名
         */
        const val DIALOG_TAG = "tag"

        /**
         * ダイアログ外のタップでのキャンセル可否
         */
        const val DIALOG_IS_CANCELABLE = "cancelable"

        /**
         * フラグメントからの起動であるかの確認用キー
         */
        const val FRAGMENT_EXIST = "fragmentExist"

        /**
         * 結果を返すためのリクエストキー
         */
        const val DIALOG_RESULT_KEY = "result"
    }

    interface OnDialogSelectListener {
        /**
         * Activityに送信するときのダイアログ選択リスナー
         *
         * @param key 対象ダイアログのリクエストキー
         * @param resultCode 押下ボタンの判定(-1: positive, -2: negative, -3: neutral)
         * @param bundle ダイアログ内の決定データ。押下ボタン以外の情報はここに含まれる。
         */
        fun receiveResultFromDialog(key: String, resultCode: Int, bundle: Bundle)
    }

    /**
     * ダイアログ選択リスナー
     *
     * 再生成時に定義のし直しが必要なため、nullableにしている
     */
    open var selectListener: OnDialogSelectListener? = null

    /**
     * Fragmentから呼び出された時にフラグを立てる
     */
    open var isCalledFromFragment: Boolean = false

    /**
     * ダイアログのビルド関数群
     *
     * 関数に関しては各派生先で実装すること
     */
    abstract class Builder {
        protected var reqKey: String = ""
        protected var tagName: String = javaClass.simpleName
        protected var cancelable: Boolean = true

        /**
         * どのダイアログからの回答かを判別する文字列
         *
         * 空文字列は入力不可
         *
         * @param key リクエストキー
         */
        abstract fun requestKey(key: String): Builder

        /**
         * ダイアログを判別するためのタグ(文字列)
         *
         * @param tag タグ
         */
        abstract fun tag(tag: String): Builder

        /**
         * 表示する内容を決定して、ダイアログのビルドを実行する
         */
        abstract fun build(): BaseDialog
    }

    /**
     * Fragmentからのダイアログの表示
     *
     * @param manager フラグメントマネージャ
     */
    fun show(manager: FragmentManager) {
        this.isCalledFromFragment = true
        super.show(manager, arguments?.getString(DIALOG_TAG))
    }

    /**
     * Activityからのダイアログの表示
     *
     * @param context コンテキスト
     */
    fun show(context: Context) {
        val manager: FragmentManager = (context as FragmentActivity).supportFragmentManager
        super.show(manager, arguments?.getString(DIALOG_TAG))
    }

    /**
     * ダイアログの表示を行うときの初期動作
     *
     * @param context コンテキスト
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Activityの場合はOnDialogSelectListenerとの結びつけが必要
        // Fragmentの場合はsetFragmentResultを使用して送信できるようにする必要がある
        // Fragmentに結果を送信したい場合にActivityとOnDialogSelectListenerが紐づいている場合、
        // Activityに結果を渡してしまうため、Fragmentからであるとわかっている場合は避ける必要がある
        if (this.isCalledFromFragment) return
        this.selectListener = when {
            context is OnDialogSelectListener -> context
            parentFragment is OnDialogSelectListener -> parentFragment as OnDialogSelectListener
            else -> null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            this.isCalledFromFragment = savedInstanceState.getBoolean(FRAGMENT_EXIST, false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FRAGMENT_EXIST, this.isCalledFromFragment)
    }

    override fun onDetach() {
        super.onDetach()
        if (this.selectListener == null) return
        this.selectListener = null
    }

    /**
     * ダイアログの選択を行ったときに応答を呼び出し元のクラスに返す
     *
     * ダイアログによって内容が変化する
     *
     * @param buttonVal 押下ボタンの判定(-1: positive, -2: negative, -3: neutral)
     */
    open fun onButtonClick(buttonVal: Int) {
        dismiss()
        if (this.isCalledFromFragment) {
            this.sendDialogResultToFragment(buttonVal)
        }
        else {
            this.sendDialogResultToActivity(buttonVal)
        }
    }

    /**
     * Activityから呼び出したときのコールバック処理を指定する
     *
     * @param buttonVal 押下ボタンの判定(-1: positive, -2: negative, -3: neutral)
     */
    open fun sendDialogResultToActivity(buttonVal: Int) {
        this.selectListener!!.receiveResultFromDialog(
            arguments?.getString(DIALOG_REQUEST_KEY)!!,
            buttonVal,
            Bundle.EMPTY
        )
    }

    /**
     * Fragmentから呼び出した時のコールバック処理を指定する。
     *
     * 呼び出し元でsetFragmentResultListenerの定義が必要。
     *
     * @param buttonVal 押下ボタンの判定(-1: positive, -2: negative, -3: neutral)
     */
    open fun sendDialogResultToFragment(buttonVal: Int) {
        val resultArgs = bundleOf(DIALOG_RESULT_KEY to buttonVal)
        setFragmentResult(
            arguments?.getString(DIALOG_REQUEST_KEY)!!,
            resultArgs
        )
    }
}