package com.alifabdulrahman.malaysiakinireader.Activity.Enter.ArticleView;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.alifabdulrahman.malaysiakinireader.model.ArticleData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MKScraper {
    private Activity activity;
    private Context ctx;
    private WebView webView;
    private boolean loading;
    boolean firstLoad = true;

    public MKScraper(Activity activity, Context ctx, WebView webView, ttsFunctionButton ttsFunctionButton, ttsController ttsController) {
        this.activity = activity;
        this.ctx = ctx;
        this.webView = webView;

        this.webView.addJavascriptInterface(new GetHTML(activity, ctx, ttsFunctionButton, ttsController), "Scrap");
    }

    public void scrap() {
        webView.loadUrl("javascript:window.Scrap.getHTML" +
                "(document.getElementsByTagName('html')[0].outerHTML);");
    }

    public class GetHTML{
        private Context ctx;
        private Activity activity;
        private saver saver;
        private loader loader;
        private ttsFunctionButton ttsFunctionButton;
        private ttsController ttsController;

        public GetHTML(Activity activity, Context ctx, ttsFunctionButton ttsFunctionButton, ttsController ttsController) {
            this.activity = activity;
            this.ctx = ctx;
            saver = new saver(this.activity, this.ctx);
            loader = new loader(this.activity, this.ctx);
            this.ttsFunctionButton = ttsFunctionButton;
            this.ttsController = ttsController;
        }

        @JavascriptInterface
        public void getHTML(String html) {

            Document doc = Jsoup.parse(html);

            ArrayList<String> tempList = new ArrayList<>();

            Elements classContents = doc.select("div[id $= full-content-container]");

            Elements contents = classContents.select("p, li");

            if (classContents == null || classContents.isEmpty()) {
                classContents = doc.select("div[id $= __next]");
                contents = classContents.select("p, li");
            }

            for (Element content : contents) {

                if (!(content.text().equals(""))) {

                    if (!tempList.contains(content.text())) {
                        tempList.add(content.text());
                    }
                }

                    /*
                    if (!firstLoad){
                        Toast.makeText(ctx, "Please sign in to get full content", Toast.LENGTH_SHORT).show();
                        saver.saveText(tempList);
                        tts = new TTS(ctx, loader, saver);
                        ttsController.setTts(tts);
                        ttsFunctionButton.enable();
                    }
                    firstLoad = false;


                     */

            }
            if (checkLoading(tempList)) {
                Toast.makeText(ctx, "Getting contents. Please wait...", Toast.LENGTH_SHORT).show();
                ttsController.stop();
                saver.clearText();

            }
            else {
                Toast.makeText(ctx, "Finished getting content", Toast.LENGTH_SHORT).show();
                ttsFunctionButton.enable();
                saver.saveText(tempList);
                ttsController.init();
            }
        }

        public boolean checkLoading(ArrayList<String> tempList){
            String lastString = (tempList.get(tempList.size()-1));
            loading = (lastString.contains("..."));
            return loading;
        }
    }

    public void setFirstLoad(){
        firstLoad = true;
    }
}

