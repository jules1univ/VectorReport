package fr.univrennes.istic.l2gen.io.xml.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import fr.univrennes.istic.l2gen.io.xml.model.XMLTag;

public class TestXMLParser {

    private XMLTag parse(String xml) throws XMLParseException, IOException {
        return new XMLParser(xml).parse();
    }

    @Test
    public void testSimpleSelfClosingTag() throws XMLParseException, IOException {
        XMLTag tag = parse("<rect/>");
        assertEquals("rect", tag.getTagName());
        assertTrue(tag.getChildrenCount() == 0);
        assertTrue(tag.getTextContent().isEmpty());
    }

    @Test
    public void testSimpleOpenCloseTag() throws XMLParseException, IOException {
        XMLTag tag = parse("<svg></svg>");
        assertEquals("svg", tag.getTagName());
        assertTrue(tag.getChildrenCount() == 0);

    }

    @Test
    public void testTagWithTextContent() throws XMLParseException, IOException {
        XMLTag tag = parse("<text>Hello</text>");
        assertEquals("text", tag.getTagName());
        assertTrue(tag.getTextContent().isPresent());
        assertEquals("Hello", tag.getTextContent().get());
    }

    @Test
    public void testTagWithWhitespaceTextContentIsIgnored() throws XMLParseException, IOException {
        XMLTag tag = parse("<g>   </g>");
        assertTrue(tag.getTextContent().isEmpty());
    }

    @Test
    public void testNestedTags() throws XMLParseException, IOException {
        XMLTag tag = parse("<svg><rect/><circle/></svg>");
        assertEquals("svg", tag.getTagName());
        assertTrue(tag.getChildrenCount() == 2);

        assertEquals("rect", tag.getChildAt(0).get().getTagName());
        assertEquals("circle", tag.getChildAt(1).get().getTagName());
    }

    @Test
    public void testDeeplyNestedTags() throws XMLParseException, IOException {
        XMLTag tag = parse("<a><b><c><d/></c></b></a>");
        XMLTag b = tag.getChildAt(0).get();
        XMLTag c = b.getChildAt(0).get();
        XMLTag d = c.getChildAt(0).get();
        assertEquals("b", b.getTagName());
        assertEquals("c", c.getTagName());
        assertEquals("d", d.getTagName());
    }

    @Test
    public void testSingleAttribute() throws XMLParseException, IOException {
        XMLTag tag = parse("<rect width=\"100\"/>");
        assertEquals(1, tag.getAttributeCount());
        assertTrue(tag.hasAttribute("width"));
        assertEquals("100", tag.getAttribute("width").value());
    }

    @Test
    public void testMultipleAttributes() throws XMLParseException, IOException {
        XMLTag tag = parse("<rect x=\"10\" y=\"20\" width=\"100\" height=\"50\"/>");
        assertEquals(4, tag.getAttributeCount());
        assertTrue(tag.hasAttribute("x"));
        assertTrue(tag.hasAttribute("y"));
        assertTrue(tag.hasAttribute("width"));
        assertTrue(tag.hasAttribute("height"));
        assertEquals("10", tag.getAttribute("x").value());
    }

    @Test
    public void testAttributeWithSingleQuotes() throws XMLParseException, IOException {
        XMLTag tag = parse("<rect id='myRect'/>");
        assertTrue(tag.hasAttribute("id"));
        assertEquals("myRect", tag.getAttribute("id").value());
    }

    @Test
    public void testAttributeWithSpacesAroundEquals() throws XMLParseException, IOException {
        XMLTag tag = parse("<rect width = \"100\"/>");
        assertTrue(tag.hasAttribute("width"));
        assertEquals("100", tag.getAttribute("width").value());
    }

    @Test
    public void testAttributeWithEmptyValue() throws XMLParseException, IOException {
        XMLTag tag = parse("<rect id=\"\"/>");
        assertTrue(tag.hasAttribute("id"));
        assertEquals("", tag.getAttribute("id").value());
    }

    @Test
    public void testUnescapeLtGtInAttributeValue() throws XMLParseException, IOException {
        XMLTag tag = parse("<tag attr=\"&lt;value&gt;\"/>");
        assertEquals("<value>", tag.getAttribute("attr").value());
    }

    @Test
    public void testUnescapeAmpInAttributeValue() throws XMLParseException, IOException {
        XMLTag tag = parse("<tag attr=\"a&amp;b\"/>");
        assertEquals("a&b", tag.getAttribute("attr").value());
    }

    @Test
    public void testUnescapeQuotInAttributeValue() throws XMLParseException, IOException {
        XMLTag tag = parse("<tag attr=\"say &quot;hi&quot;\"/>");
        assertEquals("say \"hi\"", tag.getAttribute("attr").value());
    }

    @Test
    public void testUnescapeAposInAttributeValue() throws XMLParseException, IOException {
        XMLTag tag = parse("<tag attr=\"it&apos;s\"/>");
        assertEquals("it's", tag.getAttribute("attr").value());
    }

    @Test
    public void testUnescapeEntitiesInTextContent() throws XMLParseException, IOException {
        XMLTag tag = parse("<text>1 &lt; 2 &amp; 3 &gt; 0</text>");
        assertTrue(tag.getTextContent().isPresent());
        assertEquals("1 < 2 & 3 > 0", tag.getTextContent().get());
    }

    @Test
    public void testXmlDeclarationSkipped() throws XMLParseException, IOException {
        XMLTag tag = parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?><svg/>");
        assertEquals("svg", tag.getTagName());
    }

    @Test
    public void testDoctypeDeclarationSkipped() throws XMLParseException, IOException {
        XMLTag tag = parse("<!DOCTYPE svg><svg/>");
        assertEquals("svg", tag.getTagName());
    }

    @Test
    public void testCommentBeforeRootSkipped() throws XMLParseException, IOException {
        XMLTag tag = parse("<!-- this is a comment --><rect/>");
        assertEquals("rect", tag.getTagName());
    }

    @Test
    public void testCommentBetweenChildrenSkipped() throws XMLParseException, IOException {
        XMLTag tag = parse("<svg><!-- comment --><rect/></svg>");
        assertEquals(1, tag.getChildrenCount());
        assertEquals("rect", tag.getChildAt(0).get().getTagName());
    }

    @Test
    public void testMultilineComment() throws XMLParseException, IOException {
        XMLTag tag = parse("<!--\n  multiline\n  comment\n--><circle/>");
        assertEquals("circle", tag.getTagName());
    }

    @Test
    public void testLeadingWhitespaceBeforeRoot() throws XMLParseException, IOException {
        XMLTag tag = parse("   \n\t<rect/>");
        assertEquals("rect", tag.getTagName());
    }

    @Test
    public void testWhitespaceBetweenTags() throws XMLParseException, IOException {
        XMLTag tag = parse("<svg>\n  <rect/>\n  <circle/>\n</svg>");
        assertEquals(2, tag.getChildrenCount());
    }

    @Test
    public void testAttributesWithNewlines() throws XMLParseException, IOException {
        XMLTag tag = parse("<rect\n  x=\"10\"\n  y=\"20\"\n/>");
        assertEquals(2, tag.getAttributeCount());
    }

    @Test
    public void testConstructorWithReader() throws XMLParseException, IOException {
        XMLTag tag = new XMLParser(new StringReader("<line/>")).parse();
        assertEquals("line", tag.getTagName());
    }

    @Test
    public void testConstructorWithBufferedReader() throws XMLParseException, IOException {
        XMLTag tag = new XMLParser(new BufferedReader(new StringReader("<path/>"))).parse();
        assertEquals("path", tag.getTagName());
    }

    @Test
    public void testMixedTextAndChildren() throws XMLParseException, IOException {
        XMLTag tag = parse("<text>Hello<tspan>world</tspan></text>");
        assertEquals("Hello", tag.getTextContent().get());
        assertEquals(1, tag.getChildrenCount());
        assertEquals("tspan", tag.getChildAt(0).get().getTagName());
        assertEquals("world", tag.getChildAt(0).get().getTextContent().get());
    }

    @Test
    public void testRealSVGSnippet() throws XMLParseException, IOException {
        String svg = """
                <?xml version="1.0" encoding="UTF-8"?>
                <svg xmlns="http://www.w3.org/2000/svg" width="200" height="200">
                    <!-- background -->
                    <rect x="0" y="0" width="200" height="200" fill="white"/>
                    <circle cx="100" cy="100" r="50" stroke="black" fill="red"/>
                    <text x="100" y="190">Label</text>
                </svg>
                """;

        XMLTag root = new XMLParser(svg).parse();

        assertEquals("svg", root.getTagName());
        assertEquals(3, root.getChildrenCount());

        XMLTag rect = root.getChildAt(0).get();
        XMLTag circle = root.getChildAt(1).get();
        XMLTag text = root.getChildAt(2).get();

        assertEquals("rect", rect.getTagName());
        assertEquals("200", rect.getAttribute("width").value());

        assertEquals("circle", circle.getTagName());
        assertEquals("50", circle.getAttribute("r").value());
        assertEquals("text", text.getTagName());

        assertTrue(text.getTextContent().isPresent());
        assertEquals("Label", text.getTextContent().get());
    }

    @Test
    public void testMissingOpeningBracketThrows() {
        assertThrows(XMLParseException.class, () -> parse("rect/>"));
    }

    @Test
    public void testMismatchedClosingTagThrows() {
        assertThrows(XMLParseException.class, () -> parse("<rect></circle>"));
    }

    @Test
    public void testUnclosedTagThrows() {
        assertThrows(XMLParseException.class, () -> parse("<rect>"));
    }

    @Test
    public void testUnclosedCommentThrows() {
        assertThrows(XMLParseException.class, () -> parse("<!-- unclosed <rect/>"));
    }

    @Test
    public void testUnclosedAttributeValueThrows() {
        assertThrows(XMLParseException.class, () -> parse("<rect id=\"unclosed/>"));
    }

    @Test
    public void testMissingEqualsInAttributeThrows() {
        assertThrows(XMLParseException.class, () -> parse("<rect id\"value\"/>"));
    }

    @Test
    public void testEmptyInputThrows() {
        assertThrows(Exception.class, () -> parse(""));
    }

}
