package com.example.tmagiera.flipbook;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmagiera on 2015-02-19.
 *
 * This XML file does not appear to have any style information associated with it. The document tree is shown below.
 <xml>
 <paging>
 <current_page>17011</current_page>
 <latest_page>17011</latest_page>
 </paging>
 <img>
 <id>2246167</id>
 <source>
 http://i1.kwejk.pl/k/obrazki/2015/02/0ccc1bbc56b6b051ccd83c61a271c989.jpg
 </source>
 <title>Nie będę powtarzał :D</title>
 <link>/obrazek/2246167/nie-bede-powtarzal-d.html</link>
 <width>610</width>
 <height>495</height>
 <size/>
 </img>
 <img>
    ..
 </img>
 </xml>
 */
public class KwejkXmlParser {

    private static final String ns = null;

    public List<Entry> parse(String xml) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(Charset.forName("UTF-16").encode(xml).array()), null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
        }
    }

    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "xml");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("img")) {
                Entry entry = readEntry(parser);
                if (entry != null) {
                    entries.add(entry);
                }
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class Entry {
        public final Integer id;
        public final String source;
        public final String title;
        public final Integer height;

        public Entry(Integer id, String source, String title, Integer height) {
            this.id = id;
            this.source = source;
            this.title = title;
            this.height = height;
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "img");
        Integer id = null;
        String source = null;
        String title = null;
        Integer height = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "id":
                    id = readId(parser);
                    break;
                case "source":
                    source = readSource(parser);
                    break;
                case "title":
                    title = readTitle(parser);
                    break;
                case "height":
                    height = readHeight(parser);
                    break;
                default:
                    skip(parser);
            }
        }
        if (id == null || source == null || title == null || height == null) {
            return null;
        }

        Log.d("KwejkParser", "id: " + id + ";source: " + source + ";title " + title + ";height " + height);
        return new Entry(id, source, title, height);
    }

    private Integer readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        Integer id = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return id;
    }

    private String readSource(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "source");
        String source = readText(parser);
//        String relType = parser.getAttributeValue(null, "rel");
//        if (tag.equals("link")) {
//            if (relType.equals("alternate")){
//                link = parser.getAttributeValue(null, "href");
//                parser.nextTag();
//            }
//        }
        parser.require(XmlPullParser.END_TAG, ns, "source");
        return source;
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    private Integer readHeight(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "height");
        Integer id = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "height");
        return id;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
