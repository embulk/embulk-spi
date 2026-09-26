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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.msgpack.value.ValueFactory;

public class TestJsonArray {
    @Test
    public void testFinal() {
        // JsonArray must be final.
        assertTrue(Modifier.isFinal(JsonArray.class.getModifiers()));
    }

    @Test
    public void testNull() {
        assertThrows(NullPointerException.class, () -> JsonArray.of((JsonValue[]) null));
        assertThrows(NullPointerException.class, () -> JsonArray.of(null, JsonString.of("foo")));
        assertThrows(NullPointerException.class, () -> JsonArray.ofList(null));

        final ArrayList<JsonValue> valuesNull = new ArrayList<>();
        valuesNull.add(JsonLong.of(987));
        valuesNull.add(null);
        assertThrows(NullPointerException.class, () -> JsonArray.ofList(valuesNull));
    }

    @Test
    public void testEmpty() {
        final JsonArray jsonArray = JsonArray.of();
        assertEquals(JsonValue.EntityType.ARRAY, jsonArray.getEntityType());
        assertFalse(jsonArray.isJsonNull());
        assertFalse(jsonArray.isJsonBoolean());
        assertFalse(jsonArray.isJsonLong());
        assertFalse(jsonArray.isJsonDouble());
        assertFalse(jsonArray.isJsonString());
        assertTrue(jsonArray.isJsonArray());
        assertFalse(jsonArray.isJsonObject());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonNull());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonLong());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonDouble());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonString());
        assertEquals(jsonArray, jsonArray.asJsonArray());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonObject());
        assertEquals(4, jsonArray.presumeReferenceSizeInBytes());
        assertEquals(0, jsonArray.size());
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(0));
        assertEquals("[]", jsonArray.toJson());
        assertEquals("[]", jsonArray.toString());
        assertEquals(JsonArray.of(), jsonArray);

        assertEquals(ValueFactory.emptyArray(), jsonArray.toMsgpack());

        // JsonArray#equals follows List#equals. It is equal to any List with equal elements in the same order,
        // even to a fake imitation of JsonArray.
        assertTrue(jsonArray.equals(FakeJsonArray.of()));
    }

    @Test
    public void testSingle() {
        final JsonArray jsonArray = JsonArray.of(JsonLong.of(987));
        assertEquals(JsonValue.EntityType.ARRAY, jsonArray.getEntityType());
        assertFalse(jsonArray.isJsonNull());
        assertFalse(jsonArray.isJsonBoolean());
        assertFalse(jsonArray.isJsonLong());
        assertFalse(jsonArray.isJsonDouble());
        assertFalse(jsonArray.isJsonString());
        assertTrue(jsonArray.isJsonArray());
        assertFalse(jsonArray.isJsonObject());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonNull());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonLong());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonDouble());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonString());
        assertEquals(jsonArray, jsonArray.asJsonArray());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonObject());
        assertEquals(12, jsonArray.presumeReferenceSizeInBytes());
        assertEquals(1, jsonArray.size());
        assertEquals(JsonLong.of(987), jsonArray.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(1));
        assertEquals("[987]", jsonArray.toJson());
        assertEquals("[987]", jsonArray.toString());
        assertEquals(JsonArray.of(JsonLong.of(987)), jsonArray);

        assertEquals(ValueFactory.newArray(ValueFactory.newInteger(987)), jsonArray.toMsgpack());

        // JsonArray#equals follows List#equals. It is equal to any List with equal elements in the same order,
        // even to a fake imitation of JsonArray.
        assertTrue(jsonArray.equals(FakeJsonArray.of(JsonLong.of(987))));
    }

    @Test
    public void testMultiOfArray() {
        final JsonValue[] values = new JsonValue[3];
        values[0] = JsonLong.of(987);
        values[1] = JsonString.of("foo");
        values[2] = JsonBoolean.TRUE;
        final JsonArray jsonArray = JsonArray.of(values);
        assertEquals(JsonValue.EntityType.ARRAY, jsonArray.getEntityType());
        assertFalse(jsonArray.isJsonNull());
        assertFalse(jsonArray.isJsonBoolean());
        assertFalse(jsonArray.isJsonLong());
        assertFalse(jsonArray.isJsonDouble());
        assertFalse(jsonArray.isJsonString());
        assertTrue(jsonArray.isJsonArray());
        assertFalse(jsonArray.isJsonObject());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonNull());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonLong());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonDouble());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonString());
        assertEquals(jsonArray, jsonArray.asJsonArray());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonObject());
        assertEquals(23, jsonArray.presumeReferenceSizeInBytes());
        assertEquals(3, jsonArray.size());
        assertEquals(JsonLong.of(987), jsonArray.get(0));
        assertEquals(JsonString.of("foo"), jsonArray.get(1));
        assertEquals(JsonBoolean.TRUE, jsonArray.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(3));
        assertEquals("[987,\"foo\",true]", jsonArray.toJson());
        assertEquals("[987,\"foo\",true]", jsonArray.toString());
        assertEquals(JsonArray.of(JsonLong.of(987), JsonString.of("foo"), JsonBoolean.TRUE), jsonArray);

        // Tests that changing the original array does NOT affect the JsonArray instance.
        values[0] = JsonLong.of(1234);

        assertEquals(3, jsonArray.size());
        assertEquals(JsonLong.of(987), jsonArray.get(0));
        assertEquals(JsonString.of("foo"), jsonArray.get(1));
        assertEquals(JsonBoolean.TRUE, jsonArray.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(3));
        assertEquals("[987,\"foo\",true]", jsonArray.toJson());
        assertEquals("[987,\"foo\",true]", jsonArray.toString());
        assertEquals(JsonArray.of(JsonLong.of(987), JsonString.of("foo"), JsonBoolean.TRUE), jsonArray);

        assertEquals(
                ValueFactory.newArray(ValueFactory.newInteger(987), ValueFactory.newString("foo"), ValueFactory.newBoolean(true)),
                jsonArray.toMsgpack());

        // JsonArray#equals follows List#equals. It is equal to any List with equal elements in the same order,
        // even to a fake imitation of JsonArray.
        assertTrue(jsonArray.equals(FakeJsonArray.of(JsonLong.of(987), JsonString.of("foo"), JsonBoolean.TRUE)));
    }

    @Test
    public void testMultiOfList() {
        final ArrayList<JsonValue> values = new ArrayList<>();
        values.add(JsonLong.of(987));
        values.add(JsonString.of("foo"));
        values.add(JsonBoolean.TRUE);
        final JsonArray jsonArray = JsonArray.ofList(values);
        assertEquals(JsonValue.EntityType.ARRAY, jsonArray.getEntityType());
        assertFalse(jsonArray.isJsonNull());
        assertFalse(jsonArray.isJsonBoolean());
        assertFalse(jsonArray.isJsonLong());
        assertFalse(jsonArray.isJsonDouble());
        assertFalse(jsonArray.isJsonString());
        assertTrue(jsonArray.isJsonArray());
        assertFalse(jsonArray.isJsonObject());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonNull());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonLong());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonDouble());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonString());
        assertEquals(jsonArray, jsonArray.asJsonArray());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonObject());
        assertEquals(23, jsonArray.presumeReferenceSizeInBytes());
        assertEquals(3, jsonArray.size());
        assertEquals(JsonLong.of(987), jsonArray.get(0));
        assertEquals(JsonString.of("foo"), jsonArray.get(1));
        assertEquals(JsonBoolean.TRUE, jsonArray.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(3));
        assertEquals("[987,\"foo\",true]", jsonArray.toJson());
        assertEquals("[987,\"foo\",true]", jsonArray.toString());
        assertEquals(JsonArray.of(JsonLong.of(987), JsonString.of("foo"), JsonBoolean.TRUE), jsonArray);

        // Tests that changing the original array does NOT affect the JsonArray instance.
        values.add(JsonLong.of(1234));

        assertEquals(3, jsonArray.size());
        assertEquals(JsonLong.of(987), jsonArray.get(0));
        assertEquals(JsonString.of("foo"), jsonArray.get(1));
        assertEquals(JsonBoolean.TRUE, jsonArray.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(3));
        assertEquals("[987,\"foo\",true]", jsonArray.toJson());
        assertEquals("[987,\"foo\",true]", jsonArray.toString());
        assertEquals(JsonArray.of(JsonLong.of(987), JsonString.of("foo"), JsonBoolean.TRUE), jsonArray);

        assertEquals(
                ValueFactory.newArray(ValueFactory.newInteger(987), ValueFactory.newString("foo"), ValueFactory.newBoolean(true)),
                jsonArray.toMsgpack());

        // JsonArray#equals follows List#equals. It is equal to any List with equal elements in the same order,
        // even to a fake imitation of JsonArray.
        assertTrue(jsonArray.equals(FakeJsonArray.of(JsonLong.of(987), JsonString.of("foo"), JsonBoolean.TRUE)));
    }

    @Test
    public void testMultiUnsafe() {
        final JsonValue[] values = new JsonValue[3];
        values[0] = JsonLong.of(987);
        values[1] = JsonString.of("foo");
        values[2] = JsonBoolean.TRUE;
        final JsonArray jsonArray = JsonArray.ofUnsafe(values);
        assertEquals(JsonValue.EntityType.ARRAY, jsonArray.getEntityType());
        assertFalse(jsonArray.isJsonNull());
        assertFalse(jsonArray.isJsonBoolean());
        assertFalse(jsonArray.isJsonLong());
        assertFalse(jsonArray.isJsonDouble());
        assertFalse(jsonArray.isJsonString());
        assertTrue(jsonArray.isJsonArray());
        assertFalse(jsonArray.isJsonObject());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonNull());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonLong());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonDouble());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonString());
        assertEquals(jsonArray, jsonArray.asJsonArray());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonObject());
        assertEquals(23, jsonArray.presumeReferenceSizeInBytes());
        assertEquals(3, jsonArray.size());
        assertEquals(JsonLong.of(987), jsonArray.get(0));
        assertEquals(JsonString.of("foo"), jsonArray.get(1));
        assertEquals(JsonBoolean.TRUE, jsonArray.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(3));
        assertEquals("[987,\"foo\",true]", jsonArray.toJson());
        assertEquals("[987,\"foo\",true]", jsonArray.toString());
        assertEquals(JsonArray.of(JsonLong.of(987), JsonString.of("foo"), JsonBoolean.TRUE), jsonArray);

        // Tests that changing the original array DOES affect the JsonArray instance that is created by #ofUnsafe.
        values[0] = JsonLong.of(1234);

        assertEquals(3, jsonArray.size());
        assertEquals(JsonLong.of(1234), jsonArray.get(0));  // Updated
        assertEquals(JsonString.of("foo"), jsonArray.get(1));
        assertEquals(JsonBoolean.TRUE, jsonArray.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(3));
        assertEquals("[1234,\"foo\",true]", jsonArray.toJson());  // Updated
        assertEquals("[1234,\"foo\",true]", jsonArray.toString());  // Updated
        assertEquals(JsonArray.of(JsonLong.of(1234), JsonString.of("foo"), JsonBoolean.TRUE), jsonArray);  // Updated

        assertEquals(
                ValueFactory.newArray(ValueFactory.newInteger(1234), ValueFactory.newString("foo"), ValueFactory.newBoolean(true)),
                jsonArray.toMsgpack());

        // JsonArray#equals follows List#equals. It is equal to any List with equal elements in the same order,
        // even to a fake imitation of JsonArray.
        assertTrue(jsonArray.equals(FakeJsonArray.of(JsonLong.of(1234), JsonString.of("foo"), JsonBoolean.TRUE)));
    }

    @Test
    public void testNested() {
        final JsonValue[] values = new JsonValue[3];
        values[0] = JsonLong.of(987);
        values[1] = JsonArray.of(JsonString.of("foo"), JsonString.of("bar"), JsonString.of("baz"));
        values[2] = JsonBoolean.TRUE;
        final JsonArray jsonArray = JsonArray.of(values);
        assertEquals(JsonValue.EntityType.ARRAY, jsonArray.getEntityType());
        assertFalse(jsonArray.isJsonNull());
        assertFalse(jsonArray.isJsonBoolean());
        assertFalse(jsonArray.isJsonLong());
        assertFalse(jsonArray.isJsonDouble());
        assertFalse(jsonArray.isJsonString());
        assertTrue(jsonArray.isJsonArray());
        assertFalse(jsonArray.isJsonObject());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonNull());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonBoolean());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonLong());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonDouble());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonString());
        assertEquals(jsonArray, jsonArray.asJsonArray());
        assertThrows(ClassCastException.class, () -> jsonArray.asJsonObject());
        assertEquals(47, jsonArray.presumeReferenceSizeInBytes());
        assertEquals(3, jsonArray.size());
        assertEquals(JsonLong.of(987), jsonArray.get(0));
        assertEquals(JsonArray.of(JsonString.of("foo"), JsonString.of("bar"), JsonString.of("baz")), jsonArray.get(1));
        assertEquals(JsonBoolean.TRUE, jsonArray.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(3));
        assertEquals("[987,[\"foo\",\"bar\",\"baz\"],true]", jsonArray.toJson());
        assertEquals("[987,[\"foo\",\"bar\",\"baz\"],true]", jsonArray.toString());
        assertEquals(JsonArray.of(JsonLong.of(987), JsonArray.of(JsonString.of("foo"), JsonString.of("bar"), JsonString.of("baz")), JsonBoolean.TRUE), jsonArray);

        values[0] = JsonLong.of(1234);

        assertEquals(3, jsonArray.size());
        assertEquals(JsonLong.of(987), jsonArray.get(0));
        assertEquals(JsonArray.of(JsonString.of("foo"), JsonString.of("bar"), JsonString.of("baz")), jsonArray.get(1));
        assertEquals(JsonBoolean.TRUE, jsonArray.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> jsonArray.get(3));
        assertEquals("[987,[\"foo\",\"bar\",\"baz\"],true]", jsonArray.toJson());
        assertEquals("[987,[\"foo\",\"bar\",\"baz\"],true]", jsonArray.toString());
        assertEquals(JsonArray.of(JsonLong.of(987), JsonArray.of(JsonString.of("foo"), JsonString.of("bar"), JsonString.of("baz")), JsonBoolean.TRUE), jsonArray);

        assertEquals(
                ValueFactory.newArray(
                        ValueFactory.newInteger(987),
                        ValueFactory.newArray(
                                ValueFactory.newString("foo"), ValueFactory.newString("bar"), ValueFactory.newString("baz")),
                        ValueFactory.newBoolean(true)),
                jsonArray.toMsgpack());

        // JsonArray#equals follows List#equals. It is equal to any List with equal elements in the same order,
        // even to a fake imitation of JsonArray.
        assertTrue(jsonArray.equals(
                            FakeJsonArray.of(
                                    JsonLong.of(987),
                                    JsonArray.of(JsonString.of("foo"), JsonString.of("bar"), JsonString.of("baz")),
                                    JsonBoolean.TRUE)));
    }

    @Test
    public void testFromMsgpack() {
        assertEquals(
                JsonArray.of(
                        JsonLong.of(987),
                        JsonArray.of(
                                JsonString.of("foo"), JsonString.of("bar"), JsonString.of("baz")),
                        JsonBoolean.TRUE),
                JsonValue.fromMsgpack(ValueFactory.newArray(
                        ValueFactory.newInteger(987),
                        ValueFactory.newArray(
                                ValueFactory.newString("foo"), ValueFactory.newString("bar"), ValueFactory.newString("baz")),
                        ValueFactory.newBoolean(true))));
    }

    @Test
    public void testEqualityWithGeneralList() {
        final JsonArray jsonArray = JsonArray.of(JsonLong.of(987), JsonString.of("foo"), JsonBoolean.TRUE);

        final ArrayList<JsonValue> arrayList = new ArrayList<>();
        arrayList.add(JsonLong.of(987));
        arrayList.add(JsonString.of("foo"));
        arrayList.add(JsonBoolean.TRUE);
        final LinkedList<JsonValue> linkedList = new LinkedList<>(arrayList);

        // JsonArray#equals follows List#equals. It must be symmetric with other List implementations.
        assertTrue(jsonArray.equals(arrayList));
        assertTrue(arrayList.equals(jsonArray));
        assertTrue(jsonArray.equals(linkedList));
        assertTrue(linkedList.equals(jsonArray));

        // JsonArray#hashCode follows List#hashCode to be consistent with JsonArray#equals.
        assertEquals(arrayList.hashCode(), jsonArray.hashCode());
        assertEquals(linkedList.hashCode(), jsonArray.hashCode());

        // A List with the same elements in a different order is not equal.
        final ArrayList<JsonValue> reordered = new ArrayList<>();
        reordered.add(JsonString.of("foo"));
        reordered.add(JsonLong.of(987));
        reordered.add(JsonBoolean.TRUE);
        assertFalse(jsonArray.equals(reordered));
        assertFalse(reordered.equals(jsonArray));

        // A List with a different size is not equal.
        final List<JsonValue> shorter = arrayList.subList(0, 2);
        assertFalse(jsonArray.equals(shorter));
        assertFalse(shorter.equals(jsonArray));

        // A List of Java objects which are not JsonValue is not equal.
        final ArrayList<Object> javaObjects = new ArrayList<>();
        javaObjects.add(987L);
        javaObjects.add("foo");
        javaObjects.add(true);
        assertFalse(jsonArray.equals(javaObjects));
        assertFalse(javaObjects.equals(jsonArray));

        // A Collection which is not a List is not equal.
        final LinkedHashSet<JsonValue> set = new LinkedHashSet<>(arrayList);
        assertFalse(jsonArray.equals(set));
        assertFalse(set.equals(jsonArray));
    }

    @Test
    public void testNestedEqualityWithGeneralList() {
        final JsonArray jsonArray = JsonArray.of(
                JsonLong.of(987),
                JsonArray.of(JsonString.of("foo"), JsonString.of("bar")),
                JsonObject.of(JsonString.of("baz"), JsonBoolean.FALSE));

        final ArrayList<JsonValue> innerList = new ArrayList<>();
        innerList.add(JsonString.of("foo"));
        innerList.add(JsonString.of("bar"));
        final LinkedHashMap<String, JsonValue> innerMap = new LinkedHashMap<>();
        innerMap.put("baz", JsonBoolean.FALSE);
        final ArrayList<Object> list = new ArrayList<>();
        list.add(JsonLong.of(987));
        list.add(innerList);
        list.add(innerMap);

        // Nested JsonArray and JsonObject also follow List#equals and Map#equals.
        assertTrue(jsonArray.equals(list));
        assertTrue(list.equals(jsonArray));
        assertEquals(list.hashCode(), jsonArray.hashCode());
    }
}
