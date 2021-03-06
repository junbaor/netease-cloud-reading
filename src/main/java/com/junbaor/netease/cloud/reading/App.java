package com.junbaor.netease.cloud.reading;

import com.google.gson.Gson;
import com.junbaor.netease.cloud.reading.model.ArticleContent;
import com.junbaor.netease.cloud.reading.model.BookMain;
import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import net.dongliu.requests.Requests;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class App {

    private static final Gson gson = new Gson();
    private static final Base64 base64 = new Base64();

    public static void main(String[] args) throws EncoderException, IOException {
        String cookieString = "davisit=NaN; ";
        Map<String, String> cookies = stringToCookies(cookieString);

        String readToText = Requests.get("http://yuedu.163.com/getBook.do?id=3d90ff26ca1c4e15a14918a1b5faf948_4").send().readToText();
        BookMain bookMain = gson.fromJson(readToText, BookMain.class);

        Map<String, String> parms = new HashMap<>();
        parms.put("sourceUuid", bookMain.getSourceUuid());

        bookMain.getPortions().remove(0);

        for (BookMain.PortionsBean portionsBean : bookMain.getPortions()) {
            parms.put("articleUuid", portionsBean.getId());
            parms.put("bigContentId", portionsBean.getBigContentId());

            String articleContent = Requests.get("http://yuedu.163.com/getArticleContent.do").params(parms).cookies(cookies).send().readToText();
            ArticleContent article = gson.fromJson(articleContent, ArticleContent.class);

            String markdown = htmlToMarkdown(decode(article.getContent()));
            printToFile(AppUtils.magic(portionsBean.getTitle()), markdown);
        }
    }

    public static String decode(String string) throws UnsupportedEncodingException {
        if (string == null) {
            return "";
        } else {
            return new String(base64.decode(string.getBytes("utf-8")));
        }
    }

    public static void printToFile(String fileName, String string) throws IOException {
        String filePath = "D:/book/" + fileName + ".md";
        IOUtils.write(string, new FileOutputStream(new File(filePath)));
    }

    public static Map<String, String> stringToCookies(String string) {
        Map<String, String> cookies = new HashMap<>();
        String[] split = string.split("; ");

        for (String cookie : split) {
            String[] temp = cookie.split("=");
            cookies.put(temp[0], temp[1]);
        }
        return cookies;
    }

    public static String htmlToMarkdown(String html) {
        Remark remark = new Remark(Options.github());
        String markdown = remark.convertFragment(html);
        return markdown;
    }


}
