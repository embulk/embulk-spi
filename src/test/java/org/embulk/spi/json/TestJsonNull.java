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

public class TestJsonNull {
    @Test
    public void testFinal() {
        // JsonNull must be final.
        assertTrue(Modifier.isFinal(JsonNull.class.getModifiers()));
    }

    @Test
    public void test() {
        final JsonNull jsonNull = JsonNull.of();
        assertEquals(JsonValue.EntityType.NULL, jsonNull.getEntityType());
        assertTrue(jsonNull.isJsonNull());
        assertFalse(jsonNull.isJsonBoolean());
        assertFalse(jsonNull.isJsonLong());
        assertFalse(jsonNull.isJsonDouble());
        assertFalse(jsonNull.isJsonString());
        assertFalse(jsonNull.isJsonArray());
        assertFalse(jsonNull.isJsonObject());
        assertEquals(jsonNull, jsonNull.asJsonNull());
        assertThrows(ClassCastException.class, () -> jsonNull.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> jsonNull.asJsonLong());
        assertThrows(ClassCastException.class, () -> jsonNull.asJsonDouble());
        assertThrows(ClassCastException.class, () -> jsonNull.asJsonString());
        assertThrows(ClassCastException.class, () -> jsonNull.asJsonArray());
        assertThrows(ClassCastException.class, () -> jsonNull.asJsonObject());
        assertEquals(1, jsonNull.presumeReferenceSizeInBytes());
        assertEquals("null", jsonNull.toJson());
        assertEquals("null", jsonNull.toString());
        assertEquals(JsonNull.NULL, jsonNull);
        assertEquals(JsonNull.of(), jsonNull);

        assertEquals(ValueFactory.newNil(), jsonNull.toMsgpack());

        // JsonNull#equals must normally reject a fake imitation of JsonNull.
        assertFalse(jsonNull.equals(FakeJsonNull.ofFake()));
    }

    @Test
    public void testFromMsgpack() {
        assertEquals(JsonNull.NULL, JsonValue.fromMsgpack(ValueFactory.newNil()));
    }
}
