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
    private List<Entry> entries = new ArrayList();
    private Integer pageNumber;


    public void parse(String xml) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new ByteArrayInputStream(Charset.forName("UTF-16").encode(xml).array()), null);
        parser.nextTag();
        readPageNumbersSection(parser);

        parser.setInput(new ByteArrayInputStream(Charset.forName("UTF-16").encode(xml).array()), null);
        parser.nextTag();
        readFeed(parser);
    }

    public List<Entry> getEntryList() {
        return entries;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    private void readPageNumbersSection(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "xml");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("paging")) {
                pageNumber = readPageNumber(parser);
                return;
            } else {
                skip(parser);
            }
        }
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
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
    }

    public static class Entry {
        public final Integer pageNumber;
        public final Integer id;
        public final String source;
        public final String title;
        public final Integer height;
        public final Integer width;

        public Entry(Integer pageNumber, Integer id, String source, String title, Integer height, Integer width) {
            this.pageNumber = pageNumber;
            this.id = id;
            this.source = source;
            this.title = title;
            this.height = height;
            this.width = width;
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
        Integer width = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "id":
                    id = readInteger(parser, "id");
                    break;
                case "source":
                    source = readString(parser, "source");
                    break;
                case "title":
                    title = readString(parser, "title");
                    break;
                case "height":
                    height = readInteger(parser, "height");
                    break;
                case "width":
                    width = readInteger(parser, "width");
                    break;
                default:
                    skip(parser);
            }
        }
        if (id == null || source == null || title == null || height == null) {
            return null;
        }

        Log.d("KwejkParser", "id: " + id + ";source: " + source + ";title " + title + ";height " + height + ";width " + width);
        return new Entry(pageNumber, id, source, title, height, width);
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Integer readPageNumber(XmlPullParser parser) throws XmlPullParserException, IOException {
        Integer currentPage = null;

        parser.require(XmlPullParser.START_TAG, ns, "paging");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "current_page":
                    currentPage = readInteger(parser, "current_page");
                    break;
                default:
                    skip(parser);
            }
        }

        Log.d("KwejkParser", "current_page: " + currentPage);
        return currentPage;
    }


    private Integer readInteger(XmlPullParser parser, String field) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, field);
        Integer id = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, field);
        return id;
    }

    private String readString(XmlPullParser parser, String field) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, field);
        String source = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, field);
        return source;
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
