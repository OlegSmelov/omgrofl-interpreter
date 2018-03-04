package omgrofl.interpreter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import omgrofl.interpreter.exceptions.ScriptParseException;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class ScriptParserTest {

    private ScriptParser scriptParser;
    private Memory memory;

    @Before
    public void setUp() {
        this.scriptParser = new ScriptParser();
        this.memory = new Memory();
    }

    @After
    public void tearDown() {
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testAlphabet() throws ScriptParseException {
        String input = "w00t outputs the alphabet\n"
                + "\n"
                + "lool iz 97\n"
                + "loool iz 122\n"
                + "\n"
                + "w00t Initial and end values can be constants.\n"
                + "w00t Using variables here for testing purposes.\n"
                + "\n"
                + "4 lol iz lool 2 loool\n"
                + "    rofl lol\n"
                + "brb\n"
                + "\n"
                + "w00t put an endline at the end\n"
                + "looool iz 13\n"
                + "rofl looool\n"
                + "\n"
                + "looool iz 10\n"
                + "rofl looool";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(out);
        System.setOut(stream);

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertEquals(out.toString(), "abcdefghijklmnopqrstuvwxyz\r\n");

        assertEquals(123, memory.getVariable("lol"));
        assertEquals(97, memory.getVariable("lool"));
        assertEquals(122, memory.getVariable("loool"));
        assertEquals(10, memory.getVariable("looool"));
    }

    @Test
    public void testInvalidCommand() throws ScriptParseException {
        expectedEx.expect(ScriptParseException.class);
        expectedEx.expectMessage("Unknown command invalid");

        scriptParser.parse("invalid command", memory);
    }

    @Test
    public void testConditions() throws ScriptParseException {
        String input = "lol iz 5\n"
                + "wtf lol iz liek 4\n"
                + "    lool iz 1\n"
                + "brb\n"
                + "wtf lol iz liek 5\n"
                + "    loool iz 2\n"
                + "brb\n"
                + "wtf lol iz nope liek 4\n"
                + "    looool iz 3\n"
                + "brb\n"
                + "wtf lol iz uber 3\n"
                + "    loooool iz 4\n"
                + "brb\n"
                + "wtf lol iz nope uber 3\n"
                + "    looooool iz 5\n"
                + "brb";

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertEquals(5, memory.getVariable("lol"));
        assertEquals(null, memory.getVariable("lool"));
        assertEquals(2, memory.getVariable("loool"));
        assertEquals(3, memory.getVariable("looool"));
        assertEquals(4, memory.getVariable("loooool"));
        assertEquals(null, memory.getVariable("looooool"));
    }

    @Test
    public void testUninitializedVariable() throws ScriptParseException {
        String input = "lmao lol";
        Script script = scriptParser.parse(input, memory);

        expectedEx.expect(ScriptRuntimeException.class);
        expectedEx.expectMessage("Attempt to read uninitialized variable lol");

        script.run();
    }

    @Test
    public void testIncrementVariable() throws ScriptParseException {
        String input = "lmao lol";
        Script script = scriptParser.parse(input, memory);
        memory.setVariable("lol", 1);

        script.run();

        assertEquals(2, memory.getVariable("lol"));
    }

    @Test
    public void testDecrementVariable() throws ScriptParseException {
        String input = "roflmao lol";
        Script script = scriptParser.parse(input, memory);
        memory.setVariable("lol", 1);

        script.run();

        assertEquals(0, memory.getVariable("lol"));
    }

    @Test
    public void testInfiniteLoop() throws ScriptParseException {
        String input = "lol iz 64\n"
                + "rtfm\n"
                + "    wtf lol iz uber 65\n"
                + "        tldr\n"
                + "    brb\n"
                + "    lmao lol\n"
                + "brb";

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertEquals(66, memory.getVariable("lol"));
    }

    @Test
    public void testForLoop() throws ScriptParseException {
        String input = "lol to /dev/null\n"
                + "4 lool iz 15 2 20\n"
                + "    lmao lol\n"
                + "brb";

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertEquals(6, memory.getVariable("lol"));
        assertEquals(21, memory.getVariable("lool"));
    }

    @Test
    public void testForLoopIndexMutation() throws ScriptParseException {
        String input = "lol to /dev/null\n"
                + "4 lool iz 15 2 20\n"
                + "    lmao lol\n"
                + "    lmao lool\n"
                + "brb";

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertEquals(3, memory.getVariable("lol"));
        assertEquals(21, memory.getVariable("lool"));
    }

    @Test
    public void testReverseForLoop() throws ScriptParseException {
        String input = "lol to /dev/null\n"
                + "4 lool iz 20 2 15\n"
                + "    lmao lol\n"
                + "brb";

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertEquals(6, memory.getVariable("lol"));
        assertEquals(14, memory.getVariable("lool"));
    }

    @Test
    public void testValueToLarge() throws ScriptParseException {
        expectedEx.expect(ScriptParseException.class);
        expectedEx.expectMessage("Value 256 is out of bounds (0-255)");

        scriptParser.parse("lol iz 256", memory);
    }

    @Test
    public void testValueTooSmall() throws ScriptParseException {
        expectedEx.expect(ScriptParseException.class);
        expectedEx.expectMessage("Value -1 is out of bounds (0-255)");

        scriptParser.parse("lol iz -1", memory);
    }

    @Test
    public void testRootBreak() throws ScriptParseException {
        String input = "lol iz 0\n"
                + "wtf lol iz liek 0\n"
                + "tldr\n"
                + "brb";

        expectedEx.expect(ScriptParseException.class);
        expectedEx.expectMessage("Break operator outside of a loop is not allowed");

        Script script = scriptParser.parse(input, memory);
        script.run();
    }

    @Test
    public void testCharInput() throws ScriptParseException {
        String input = "stfw lol\n"
                + "stfw lool";

        ByteArrayInputStream in = new ByteArrayInputStream(
                new byte[]{'a', '5'}
        );
        System.setIn(in);

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertEquals(97, memory.getVariable("lol"));
        assertEquals(53, memory.getVariable("lool"));
    }

    @Test
    public void testCharOutput() throws ScriptParseException {
        String input = "lol iz 97\n"
                + "rofl lol\n"
                + "rofl lol";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(out);
        System.setOut(stream);

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertEquals(out.toString(), "aa");
    }

    @Test
    public void testStack() throws ScriptParseException {
        String input = "lol iz 5\n"
                + "n00b lol\n"
                + "l33t lool\n"
                + "l33t loool\n";

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertEquals(5, memory.getVariable("lol"));
        assertEquals(5, memory.getVariable("lool"));
        assertEquals(0, memory.getVariable("loool"));
    }

    @Test
    public void testQueue() throws ScriptParseException {
        String input = "lol iz 5\n"
                + "n00b lol\n"
                + "lmao lol\n"
                + "n00b lol\n"
                + "haxor lool\n"
                + "haxor loool\n";

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertEquals(6, memory.getVariable("lol"));
        assertEquals(5, memory.getVariable("lool"));
        assertEquals(6, memory.getVariable("loool"));
    }

    @Test
    public void testExit() throws ScriptParseException {
        String input = "stfu\n"
                + "lol iz 5";

        Script script = scriptParser.parse(input, memory);
        script.run();

        assertNull(memory.getVariable("lol"));
    }

    @Test
    public void testSleep() throws ScriptParseException {
        String input = "lol iz 5\n"
                + "afk lol";

        Script script = scriptParser.parse(input, memory);
        script.run();

        // No assertion, not sure how to test this without rewriting
    }
}
