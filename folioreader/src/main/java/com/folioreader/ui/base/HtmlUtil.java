package com.folioreader.ui.base;

import android.content.Context;

import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 * @author gautam chibde on 14/6/17.
 */

public final class HtmlUtil {

    /**
     * Function modifies input html string by adding extra css,js and font information.
     *
     * @param context     Activity Context
     * @param htmlContent input html raw data
     * @return modified raw html string
     */
    public static String getHtmlContent(Context context, String htmlContent, Config config) {

        String cssPath =
                String.format(context.getString(R.string.css_tag), "file:///android_asset/css/Style.css");

        String jsPath = String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/jsface.min.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/jquery-3.4.1.min.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-core.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-highlighter.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-classapplier.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangy-serializer.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/Bridge.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/rangefix.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag),
                "file:///android_asset/js/readium-cfi.umd.js") + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag_method_call),
                "setMediaOverlayStyleColors('#C0ED72','#C0ED72')") + "\n";

        jsPath = jsPath
                + "<meta name=\"viewport\" content=\"height=device-height, user-scalable=yes\" />" + "\n";

        jsPath = jsPath + String.format(context.getString(R.string.script_tag_method_call), "function playAudio(src) {\n" +
                "    var audioElement = $('#player')[0];\n" +
                "    audioElement.setAttribute('src',src);\n" +
                "    audioElement.load();\n" +
                "    audioElement.play();\n" +
                "    $(audioElement).show();\n" +
                "}") + "\n";

        String toInject = "\n" + cssPath + "\n" + jsPath + "\n</head>";
        htmlContent = htmlContent.replace("</head>", toInject);

        String classes = "";
        String html = "";
        switch (config.getFont()) {
            case Constants.FONT_ANDADA:
                classes = "andada";
                break;
            case Constants.FONT_LATO:
                classes = "lato";
                break;
            case Constants.FONT_LORA:
                classes = "lora";
                break;
            case Constants.FONT_RALEWAY:
                classes = "raleway";
                break;
            default:
                break;
        }

        if (config.isNightMode()) {
            classes += " nightMode";
        }

        switch (config.getFontSize()) {
            case 0:
                classes += " textSizeOne";
                break;
            case 1:
                classes += " textSizeTwo";
                break;
            case 2:
                classes += " textSizeThree";
                break;
            case 3:
                classes += " textSizeFour";
                break;
            case 4:
                classes += " textSizeFive";
                break;
            default:
                break;
        }

        htmlContent = checkClassAttr(htmlContent, classes);


        Document doc = Jsoup.parse(htmlContent, "", Parser.xmlParser());

        Elements audios = doc.getElementsByTag("audio");
        if (audios.size() > 0) {
            Elements elements = doc.select("[onclick]"); //find all elements with onclick.play
            boolean showPlayer = false;
            for (int i = 0; i < elements.size(); i++) {
                String onclick = elements.get(i).attr("onclick"); //$("#TRAC_126")[0].play()
                if (onclick.contains("play()")) {
                    showPlayer = true;
                    for (int y = 0; y < audios.size(); y++) {
                        String src = null;
                        String id = audios.get(y).attr("id");
                        if (onclick.split("\"").length > 1) {//split by quotation
                            String rectAudioId = onclick.split("\"")[1].substring(1); //to extract TRAC_126
                            if (id.equals(rectAudioId)) {
                                src = audios.get(y).attr("src");
                                elements.get(i).attr("onclick", "playAudio('" + src + "')");
                                break;
                            }
                        }
                    }
                }
            }

            if(showPlayer) {
                doc.getElementsByTag("body").append("<audio id=\"player\" disableRemotePlayback controls controlslist=\"nodownload\" style=\"position:fixed;" +
                        "bottom:30px; width:80%; left:50%;margin-left:-40%;\"" + "\n</body>");
            }

            html = doc.html();
        } else {
            html = htmlContent;
        }
        html = html.replace("controls=\"controls\"", "controls=\"controls\" controlslist=\"nodownload\"");
        return html.replace("DOCTYPE html>", "DOCTYPE html [\n" +
                "    <!ENTITY nbsp \"&#160;\"> \n" +
                "]>");
    }

    //This will fix the "Attribute Class Redefined" error
    private static String checkClassAttr(String html, String classes) {
        //get the entry of <html>
        String s1 = html.substring(html.indexOf("<html"));
        String s2 = s1.substring(0, s1.indexOf(">"));
        String classValue = "";

        //check if class attribute exists
        if (s2.contains("class=")) {
            String classVal = s2.substring(s2.indexOf("class="));
            String[] kvPairs = classVal.split(" ");

            //get the value
            for (String kvPair : kvPairs) {
                String[] kv = kvPair.split("=");
                String key = kv[0];

                if (key.equals("class")) {
                    classValue = kv[1]; //get the value of class attribute
                    break;
                }
            }

            //remove the class attribute since it will be readded later to avoid "attribute redefined error"
            html = html.replaceFirst("class=" + classValue, "");
            classValue = classValue.substring(1); //remove the first quotation


        }
        classValue = classValue.equals("") ? "\"" : classValue;
        html = html.replace("<html", "<html class=\"" + classes + " " + classValue + " onclick=\"onClickHtml()\"");
        return html;
    }
}