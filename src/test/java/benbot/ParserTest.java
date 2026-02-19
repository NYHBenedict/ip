package benbot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {

    @Test
    void parse_normalCommand_splitsKeywordAndRest() {
        Parser p = new Parser();
        Command c = p.parse("delete 3");

        assertEquals("delete", c.keyword);
        assertEquals("3", c.rest);
    }

    @Test
    void parse_extraSpaces_trimsCorrectly() {
        Parser p = new Parser();
        Command c = p.parse("   todo    read book   ");

        assertEquals("todo", c.keyword);
        assertEquals("read book", c.rest);
    }

    @Test
    void parse_emptyInput_returnsEmptyKeywordAndRest() {
        Parser p = new Parser();
        Command c = p.parse("");

        assertEquals("", c.keyword);
        assertEquals("", c.rest);
    }

    @Test
    void parse_singleWord_restIsEmpty() {
        Parser p = new Parser();
        Command c = p.parse("list");

        assertEquals("list", c.keyword);
        assertEquals("", c.rest);
    }
}
