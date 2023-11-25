# DialogFragmentLibraryV2

Android開発者向け

DialogFragmentの表示をActivityとFragmentの両方でできるようにしたライブラリ

## 表示可能なダイアログ種類

### MessageDialog(メッセージダイアログ)

- メッセージの表示

### ItemSelectDialog(1回だけ選択可能なダイアログ)

- 選択肢を選ぶとそれに対応した応答を返す

### SingleChoiceDialog(単数選択ダイアログ)

- ラジオボタンで表示される選択肢を1つだけ選択する

- AlertDialog.Builder.setSingleChoiceDialogとは違い、Drawableで選択肢を表示することが可能になっている

### MultiChoiceDialog(複数選択ダイアログ)

- 複数選択が可能なダイアログを表示

- SingleChoiceDialogと同様にDrawableでの選択肢表示が可能

### DateSelectDialog(日付選択ダイアログ)

- AndroidのDateDialogを利用

- LocalTimeで初期設定の日付を指定可能にしている

### TimeSelectDialog(時刻選択ダイアログ)

- AndroidのTimeDialogを利用

- LocalTimeで初期設定の時刻を指定可能にしている

### ProgressDialog(プログレスダイアログ)

- 待機が必要な処理で使用する

- Runnableなobjectなどで表示の更新を行う。更新時は`ProgressDialog.DIALOG_UPDATE`をダイアログのIntentへ送信すること。

- `ProgressDialog.DIALOG_CLOSE`をダイアログのIntentへ送信することによりダイアログを自動的に閉じることが可能

## 更新履歴

- 2023-11-25
  - ReadMe.md作成

## License

MIT License

Copyright (c) 2023 kaztakgh
