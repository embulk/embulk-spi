/*
 * Copyright 2023 The Embulk project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.embulk.spi.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;
import org.msgpack.value.ValueFactory;

public class TestJsonString {
    @Test
    public void testFinal() {
        // JsonString must be final.
        assertTrue(Modifier.isFinal(JsonString.class.getModifiers()));
    }

    @Test
    public void testNull() {
        assertThrows(NullPointerException.class, () -> JsonString.of(null));
        assertThrows(NullPointerException.class, () -> JsonString.withLiteral(null, "foo"));
    }

    @Test
    public void testBasic() {
        final JsonString string = JsonString.of("hoge");
        assertEquals(JsonValue.EntityType.STRING, string.getEntityType());
        assertFalse(string.isJsonNull());
        assertFalse(string.isJsonBoolean());
        assertFalse(string.isJsonLong());
        assertFalse(string.isJsonDouble());
        assertTrue(string.isJsonString());
        assertFalse(string.isJsonArray());
        assertFalse(string.isJsonObject());
        assertThrows(ClassCastException.class, () -> string.asJsonNull());
        assertThrows(ClassCastException.class, () -> string.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> string.asJsonLong());
        assertThrows(ClassCastException.class, () -> string.asJsonDouble());
        assertEquals(string, string.asJsonString());
        assertThrows(ClassCastException.class, () -> string.asJsonArray());
        assertThrows(ClassCastException.class, () -> string.asJsonObject());
        assertEquals(12, string.presumeReferenceSizeInBytes());
        assertEquals("hoge", string.getString());
        assertEquals("hoge", string.getChars());
        assertEquals("\"hoge\"", string.toJson());
        assertEquals("\"hoge\"", string.toString());
        assertEquals(JsonString.of("hoge"), string);
        assertEquals("hoge".hashCode(), string.hashCode());

        assertEquals(ValueFactory.newString("hoge"), string.toMsgpack());

        // JsonString#equals must normally reject a fake imitation of JsonString.
        assertFalse(string.equals(FakeJsonString.of(string.getString())));
    }

    @Test
    public void testWithLiteral() {
        final JsonString string = JsonString.withLiteral("hoge\n", "\"\\u0068oge\\n\"");
        assertEquals(JsonValue.EntityType.STRING, string.getEntityType());
        assertFalse(string.isJsonNull());
        assertFalse(string.isJsonBoolean());
        assertFalse(string.isJsonLong());
        assertFalse(string.isJsonDouble());
        assertTrue(string.isJsonString());
        assertFalse(string.isJsonArray());
        assertFalse(string.isJsonObject());
        assertThrows(ClassCastException.class, () -> string.asJsonNull());
        assertThrows(ClassCastException.class, () -> string.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> string.asJsonLong());
        assertThrows(ClassCastException.class, () -> string.asJsonDouble());
        assertEquals(string, string.asJsonString());
        assertThrows(ClassCastException.class, () -> string.asJsonArray());
        assertThrows(ClassCastException.class, () -> string.asJsonObject());
        assertEquals(40, string.presumeReferenceSizeInBytes());
        assertEquals("hoge\n", string.getString());
        assertEquals("hoge\n", string.getChars());
        assertEquals("\"\\u0068oge\\n\"", string.toJson());
        assertEquals("\"hoge\\n\"", string.toString());
        assertEquals(JsonString.of("hoge\n"), string);
        assertEquals("hoge\n".hashCode(), string.hashCode());

        assertEquals(ValueFactory.newString("hoge\n"), string.toMsgpack());

        // JsonString#equals must normally reject a fake imitation of JsonString.
        assertFalse(string.equals(FakeJsonString.of(string.getString())));
    }

    @Test
    public void testEscape() {
        final JsonString string = JsonString.of("\\foo\"bar\nbaz\bqux\ffoo\nbar\rbaz\tqux\0foo\u0001bar\u0002baz\u001fqux");
        assertEquals(JsonValue.EntityType.STRING, string.getEntityType());
        assertFalse(string.isJsonNull());
        assertFalse(string.isJsonBoolean());
        assertFalse(string.isJsonLong());
        assertFalse(string.isJsonDouble());
        assertTrue(string.isJsonString());
        assertFalse(string.isJsonArray());
        assertFalse(string.isJsonObject());
        assertThrows(ClassCastException.class, () -> string.asJsonNull());
        assertThrows(ClassCastException.class, () -> string.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> string.asJsonLong());
        assertThrows(ClassCastException.class, () -> string.asJsonDouble());
        assertEquals(string, string.asJsonString());
        assertThrows(ClassCastException.class, () -> string.asJsonArray());
        assertThrows(ClassCastException.class, () -> string.asJsonObject());
        assertEquals(100, string.presumeReferenceSizeInBytes());
        assertEquals("\\foo\"bar\nbaz\bqux\ffoo\nbar\rbaz\tqux\0foo\u0001bar\u0002baz\u001fqux", string.getString());
        assertEquals("\\foo\"bar\nbaz\bqux\ffoo\nbar\rbaz\tqux\0foo\u0001bar\u0002baz\u001fqux", string.getChars());
        assertEquals(
                "\"\\\\foo\\\"bar\\nbaz\\bqux\\ffoo\\nbar\\rbaz\\tqux\\u0000foo\\u0001bar\\u0002baz\\u001fqux\"",
                string.toJson());
        assertEquals(
                "\"\\\\foo\\\"bar\\nbaz\\bqux\\ffoo\\nbar\\rbaz\\tqux\\u0000foo\\u0001bar\\u0002baz\\u001fqux\"",
                string.toString());
        assertEquals(JsonString.of("\\foo\"bar\nbaz\bqux\ffoo\nbar\rbaz\tqux\0foo\u0001bar\u0002baz\u001fqux"), string);
        assertEquals("\\foo\"bar\nbaz\bqux\ffoo\nbar\rbaz\tqux\0foo\u0001bar\u0002baz\u001fqux".hashCode(), string.hashCode());

        assertEquals(
                ValueFactory.newString("\\foo\"bar\nbaz\bqux\ffoo\nbar\rbaz\tqux\0foo\u0001bar\u0002baz\u001fqux"),
                string.toMsgpack());

        // JsonString#equals must normally reject a fake imitation of JsonString.
        assertFalse(string.equals(FakeJsonString.of(string.getString())));
    }

    @SuppressWarnings("checkstyle:IllegalTokenText")
    @Test
    public void testExample() {
        final JsonString string = JsonString.withLiteral("foo\n", "\"foo\\u000a\"");
        assertEquals("foo\n", string.getString());
        assertEquals("\"foo\\n\"", string.toString());
        assertEquals("\"foo\\u000a\"", string.toJson());
    }

    @Test
    public void testFromMsgpack() {
        assertEquals(JsonString.of(""), JsonValue.fromMsgpack(ValueFactory.newString("")));
        assertEquals(JsonString.of("hogehoge"), JsonValue.fromMsgpack(ValueFactory.newString("hogehoge")));
        assertEquals(JsonString.of("\n"), JsonValue.fromMsgpack(ValueFactory.newString("\n")));
        assertEquals(JsonString.of("\0"), JsonValue.fromMsgpack(ValueFactory.newString("\0")));
    }
}
