package fr.univrennes.istic.l2gen.io.csv.parser;

import java.io.StringReader;
import org.junit.Test;

import fr.univrennes.istic.l2gen.io.csv.model.CSVRow;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestCSVParser {

    @Test
    public void testParseSingleRow() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("a,b,c");
        assertEquals(1, table.rows().size());

        CSVRow row = table.rows().get(0);
        assertEquals("a", row.cell(0).get());
        assertEquals("b", row.cell(1).get());
        assertEquals("c", row.cell(2).get());
    }

    @Test
    public void testParseMultipleRows() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("a,b,c\n1,2,3\n4,5,6");
        assertEquals(3, table.rows().size());
    }

    @Test
    public void testParseEmptyString() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("");
        assertEquals(0, table.rows().size());
        assertFalse(table.header().isPresent());
    }

    @Test
    public void testSkipsBlankLines() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("a,b\n\n1,2\n\n3,4");
        assertEquals(3, table.rows().size());
    }

    @Test
    public void testSingleCell() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("hello");
        assertEquals(1, table.rows().size());
        assertEquals("hello", table.rows().get(0).cell(0).get());
    }

    @Test
    public void testEmptyFields() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("a,,c");
        CSVRow row = table.rows().get(0);
        assertEquals("a", row.cell(0).get());
        assertTrue("", row.cell(1).isEmpty());
        assertEquals("c", row.cell(2).get());
    }

    @Test
    public void testAllEmptyFields() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse(",,");
        CSVRow row = table.rows().get(0);
        assertTrue("", row.cell(0).isEmpty());
        assertTrue("", row.cell(1).isEmpty());
        assertTrue("", row.cell(2).isEmpty());
    }

    @Test
    public void testNoHeadersByDefault() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("name,age\nAlice,30");
        assertTrue(table.header().isEmpty());
        assertEquals(2, table.rows().size());
    }

    @Test
    public void testWithHeaders() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', true, true).parse("name,age\nAlice,30");
        assertNotNull(table.header());
        assertEquals("name", table.header().get().cell(0).get());
        assertEquals("age", table.header().get().cell(1).get());
        assertEquals(1, table.rows().size());
        assertEquals("Alice", table.rows().get(0).cell(0).get());
    }

    @Test
    public void testHeaderOnlyNoDataRows() throws CSVParseException {
        CSVTable table = new CSVParser('"', '"', true, true).parse("name,age");
        assertNotNull(table.header());
        assertEquals(0, table.rows().size());
    }

    @Test
    public void testQuotedField() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("\"hello world\",b");
        assertEquals("hello world", table.rows().get(0).cell(0).get());
        assertEquals("b", table.rows().get(0).cell(1).get());
    }

    @Test
    public void testQuotedFieldWithDelimiterInside() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("\"a,b,c\",d");
        assertEquals("a,b,c", table.rows().get(0).cell(0).get());
        assertEquals("d", table.rows().get(0).cell(1).get());
    }

    @Test
    public void testEscapedQuoteInsideQuotedField() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("\"say \"\"hello\"\"\",b");
        assertEquals("say \"hello\"", table.rows().get(0).cell(0).get());
    }

    @Test
    public void testUnclosedQuoteThrows() {
        assertThrows(CSVParseException.class, () -> new CSVParser(',', '"', false, true).parse("\"unclosed,b"));
    }

    @Test
    public void testUnexpectedQuoteInFieldThrows() {
        assertThrows(CSVParseException.class, () -> new CSVParser(',', '"', false, true).parse("ab\"c,d"));
    }

    @Test
    public void testTrimWhitespaceEnabledByDefault() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse("  a  ,  b  ,  c  ");
        CSVRow row = table.rows().get(0);
        assertEquals("a", row.cell(0).get());
        assertEquals("b", row.cell(1).get());
        assertEquals("c", row.cell(2).get());
    }

    @Test
    public void testTrimWhitespaceDisabled() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, false).parse("  a  ,  b  ");
        assertEquals("  a  ", table.rows().get(0).cell(0).get());
        assertEquals("  b  ", table.rows().get(0).cell(1).get());
    }

    @Test
    public void testSemicolonDelimiter() throws CSVParseException {
        CSVTable table = new CSVParser(';', '"', true, true).parse("a;b;c");
        CSVRow row = table.rows().get(0);
        assertEquals("a", row.cell(0).get());
        assertEquals("b", row.cell(1).get());
        assertEquals("c", row.cell(2).get());
    }

    @Test
    public void testTabDelimiter() throws CSVParseException {
        CSVTable table = new CSVParser('\t', '"', true, true).parse("a\tb\tc");
        CSVRow row = table.rows().get(0);
        assertEquals("a", row.cell(0).get());
        assertEquals("b", row.cell(1).get());
        assertEquals("c", row.cell(2).get());
    }

    @Test
    public void testPipeDelimiter() throws CSVParseException {
        CSVTable table = new CSVParser('|', '"', false, true).parse("a|b|c");
        CSVRow row = table.rows().get(0);
        assertEquals("a", row.cell(0).get());
        assertEquals("b", row.cell(1).get());
        assertEquals("c", row.cell(2).get());
    }

    @Test
    public void testCustomQuoteChar() throws CSVParseException {

        CSVTable table = new CSVParser(',', '\'', false, true).parse("'a,b',c");
        assertEquals("a,b", table.rows().get(0).cell(0).get());
        assertEquals("c", table.rows().get(0).cell(1).get());
    }

    @Test
    public void testSemicolonDelimitedFactory() throws CSVParseException {
        CSVTable table = new CSVParser(';', '"', false, true).parse("a;b;c");
        assertEquals("a", table.rows().get(0).cell(0).get());
        assertEquals("b", table.rows().get(0).cell(1).get());
        assertEquals("c", table.rows().get(0).cell(2).get());
    }

    @Test
    public void testTabDelimitedFactory() throws CSVParseException {
        CSVTable table = new CSVParser('\t', '"', false, true).parse("a\tb\tc");
        assertEquals("a", table.rows().get(0).cell(0).get());
    }

    @Test
    public void testPipeDelimitedFactory() throws CSVParseException {
        CSVTable table = new CSVParser('|', '"', false, true).parse("a|b|c");
        assertEquals("a", table.rows().get(0).cell(0).get());
    }

    @Test
    public void testParseFromReader() throws CSVParseException {
        CSVTable table = new CSVParser(',', '"', false, true).parse(new StringReader("a,b,c\n1,2,3"));
        assertEquals(2, table.rows().size());
        assertEquals("1", table.rows().get(1).cell(0).get());
    }

    @Test
    public void testFluentBuilderChaining() throws CSVParseException {
        CSVTable table = new CSVParser(';', '\'', true, true).parse("name;city\nAlice;'Paris, France'\nBob;Lyon");

        assertNotNull(table.header());
        assertEquals("name", table.header().get().cell(0).get());
        assertEquals("Paris, France", table.rows().get(0).cell(1).get());
        assertEquals("Lyon", table.rows().get(1).cell(1).get());
    }

}