package com.example.mybrowserapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val INITIAL_WEBSITE = "http://dotinstall.com"

    // javaScriptを有効にしたため、脆弱性がある
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // デフォルトでjavaScriptは無効になっているので、有効にする
        myWebView.settings.javaScriptEnabled = true
        myWebView.webViewClient = object: WebViewClient() {
            // ページの読み込み完了時に実行
            override fun onPageFinished(view: WebView, url: String) {
                // ? :supportActionBarがnullだったらnullを返し、nullじゃなかったら処理を行う
                // !!:b!! って書くと nullでないbの値を返し、bがnull ならNPEを投げる。
                supportActionBar?.subtitle = view.title
                // このサイトのURLが表示される
                urlText.setText(url)
            }
        }
        myWebView.loadUrl(INITIAL_WEBSITE)
    }

    override fun onBackPressed() {
        // 戻れる履歴があるか
        if(myWebView.canGoBack()){
            // 戻れる履歴がある場合は戻る
            myWebView.goBack()
            return
        }
        super.onBackPressed()
    }

    // WebViewはメモリ管理がシビアなので、終了処理をしっかりする
    override fun onDestroy() {
        super.onDestroy()
        if(myWebView != null){
            myWebView.stopLoading()
            myWebView.webViewClient = null
            myWebView.destroy()
        }
        //myWebViewがvalだからnullにできない　どうする？？？
        //myWebView = null
    }

    // オプションメニューが作られる前に呼ばれるメソッド
    // 戻るや前に進むボタンが使えない時にグレーアウトさせる
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val forwardItem: MenuItem = menu!!.findItem(R.id.action_forward)
        val backItem: MenuItem = menu!!.findItem(R.id.action_back)
        forwardItem.setEnabled(myWebView.canGoForward())
        backItem.setEnabled(myWebView.canGoBack())

        return super.onPrepareOptionsMenu(menu)
    }

    // メニュー表示のための関数
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.main_menu, menu)
//        return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            // 更新ボタンを押した時
            R.id.action_reload -> {
                myWebView.reload()
                return true
            }

            // 前へボタンを押した時
            R.id.action_forward -> {
                myWebView.goForward()
                return true
            }

            // 戻るボタンを押した時
            R.id.action_back -> {
                myWebView.goBack()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun showWebSite(view: View){
        var url: String = urlText.text.toString().trim()

        // URLに変な文字列が入っていないかチェック
        // 以下のチェックでは、http://もdotinstall.comもtrueになる
        if(!Patterns.WEB_URL.matcher(url).matches()){
            urlText.setError("Invalid URL")
        } else {
            // もし入力されたURLがhttp://かhttps://で始まっていなかった場合、httpをつけてあげる
            if(!url.startsWith("http://") && !url.startsWith("https://")){
                url = "http://$url"
            }
            myWebView.loadUrl(url)
        }

    }

    fun clearUrl(view: View){
        urlText.setText("")
    }
}

