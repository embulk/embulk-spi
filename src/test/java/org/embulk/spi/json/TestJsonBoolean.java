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

public class TestJsonBoolean {
    @Test
    public void testFinal() {
        // JsonBoolean must be final.
        assertTrue(Modifier.isFinal(JsonBoolean.class.getModifiers()));
    }

    @Test
    public void testFalse() {
        final JsonBoolean jsonBoolean = JsonBoolean.of(false);
        assertEquals(JsonValue.EntityType.BOOLEAN, jsonBoolean.getEntityType());
        assertFalse(jsonBoolean.isJsonNull());
        assertTrue(jsonBoolean.isJsonBoolean());
        assertFalse(jsonBoolean.isJsonLong());
        assertFalse(jsonBoolean.isJsonDouble());
        assertFalse(jsonBoolean.isJsonString());
        assertFalse(jsonBoolean.isJsonArray());
        assertFalse(jsonBoolean.isJsonObject());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonNull());
        assertEquals(jsonBoolean, jsonBoolean.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonLong());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonDouble());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonString());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonArray());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonObject());
        assertEquals(1, jsonBoolean.presumeReferenceSizeInBytes());
        assertEquals(false, jsonBoolean.booleanValue());
        assertEquals("false", jsonBoolean.toJson());
        assertEquals("false", jsonBoolean.toString());
        assertEquals(JsonBoolean.FALSE, jsonBoolean);
        assertTrue(JsonBoolean.FALSE == jsonBoolean);

        assertEquals(ValueFactory.newBoolean(false), jsonBoolean.toMsgpack());

        // JsonBoolean#equals must normally reject a fake imitation of JsonBoolean.
        assertFalse(jsonBoolean.equals(FakeJsonBoolean.FAKE_FALSE));
        assertFalse(jsonBoolean.equals(FakeJsonBoolean.FAKE_TRUE));
    }

    @Test
    public void testTrue() {
        final JsonBoolean jsonBoolean = JsonBoolean.of(true);
        assertEquals(JsonValue.EntityType.BOOLEAN, jsonBoolean.getEntityType());
        assertFalse(jsonBoolean.isJsonNull());
        assertTrue(jsonBoolean.isJsonBoolean());
        assertFalse(jsonBoolean.isJsonLong());
        assertFalse(jsonBoolean.isJsonDouble());
        assertFalse(jsonBoolean.isJsonString());
        assertFalse(jsonBoolean.isJsonArray());
        assertFalse(jsonBoolean.isJsonObject());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonNull());
        assertEquals(jsonBoolean, jsonBoolean.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonLong());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonDouble());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonString());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonArray());
        assertThrows(ClassCastException.class, () -> jsonBoolean.asJsonObject());
        assertEquals(1, jsonBoolean.presumeReferenceSizeInBytes());
        assertEquals(true, jsonBoolean.booleanValue());
        assertEquals("true", jsonBoolean.toJson());
        assertEquals("true", jsonBoolean.toString());
        assertEquals(JsonBoolean.TRUE, jsonBoolean);
        assertTrue(JsonBoolean.TRUE == jsonBoolean);

        assertEquals(ValueFactory.newBoolean(true), jsonBoolean.toMsgpack());

        // JsonBoolean#equals must normally reject a fake imitation of JsonBoolean.
        assertFalse(jsonBoolean.equals(FakeJsonBoolean.FAKE_FALSE));
        assertFalse(jsonBoolean.equals(FakeJsonBoolean.FAKE_TRUE));
    }

    @Test
    public void testFromMsgpack() {
        assertEquals(JsonBoolean.FALSE, JsonValue.fromMsgpack(ValueFactory.newBoolean(false)));
        assertEquals(JsonBoolean.TRUE, JsonValue.fromMsgpack(ValueFactory.newBoolean(true)));
    }
}
