package io.github.wysohn.rapidframework3.core.message;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageTest {
    private void assertArr(Message[] target, String... expect){
        for(int i = 0; i < expect.length; i++){
            assertEquals(expect[i], target[i].string);
        }
    }

    @Test
    public void concat() {
        Message[] msg = MessageBuilder.forMessage("")
                .append("a")
                .append("b")
                .build();
        Message[] msg2 = MessageBuilder.forMessage("")
                .append("c")
                .build();

        Message[] concat = Message.concat(msg, msg2);
        assertArr(concat, "a", "b", "c");

        concat = Message.concat(msg, msg);
        assertArr(concat, "a", "b", "a", "b");

        concat = Message.concat(msg2, msg);
        assertArr(concat, "c", "a", "b");
    }

    @Test
    public void toRawString() {
        Message[] msg = MessageBuilder.forMessage("")
                .append("a")
                .append("b")
                .build();

        assertEquals("ab", Message.toRawString(msg));
    }

    @Test
    public void join() {
        Message[] msg = MessageBuilder.forMessage("")
                .append("a")
                .append("b")
                .append("c")
                .append("d")
                .build();

        Message[] join = Message.join(", ", msg);

        assertArr(join, "a", ", ",
                "b", ", ",
                "c", ", ",
                "d");
    }
}