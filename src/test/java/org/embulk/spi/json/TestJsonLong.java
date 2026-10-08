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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

public class TestJsonLong {
    @Test
    public void testFinal() {
        // JsonLong must be final.
        assertTrue(Modifier.isFinal(JsonLong.class.getModifiers()));
    }

    @Test
    public void testBasicByteValue() {
        final JsonLong integer = JsonLong.of(123L);
        assertEquals(JsonValue.EntityType.LONG, integer.getEntityType());
        assertFalse(integer.isJsonNull());
        assertFalse(integer.isJsonBoolean());
        assertTrue(integer.isJsonLong());
        assertFalse(integer.isJsonDouble());
        assertFalse(integer.isJsonString());
        assertFalse(integer.isJsonArray());
        assertFalse(integer.isJsonObject());
        assertThrows(ClassCastException.class, () -> integer.asJsonNull());
        assertThrows(ClassCastException.class, () -> integer.asJsonBoolean());
        assertEquals(integer, integer.asJsonLong());
        assertThrows(ClassCastException.class, () -> integer.asJsonDouble());
        assertThrows(ClassCastException.class, () -> integer.asJsonString());
        assertThrows(ClassCastException.class, () -> integer.asJsonArray());
        assertThrows(ClassCastException.class, () -> integer.asJsonObject());
        assertEquals(8, integer.presumeReferenceSizeInBytes());
        assertTrue(integer.isIntegral());
        assertTrue(integer.isByteValue());
        assertTrue(integer.isShortValue());
        assertTrue(integer.isIntValue());
        assertTrue(integer.isLongValue());
        assertEquals((byte) 123, integer.byteValue());
        assertEquals((byte) 123, integer.byteValueExact());
        assertEquals((short) 123, integer.shortValue());
        assertEquals((short) 123, integer.shortValueExact());
        assertEquals((int) 123, integer.intValue());
        assertEquals((int) 123, integer.intValueExact());
        assertEquals(123L, integer.longValue());
        assertEquals(123L, integer.longValueExact());
        assertEquals(BigInteger.valueOf(123L), integer.bigIntegerValue());
        assertEquals(BigInteger.valueOf(123L), integer.bigIntegerValueExact());
        assertEquals(123.0, integer.floatValue(), 0.001);
        assertEquals(123.0, integer.doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(123L), integer.bigDecimalValue());
        assertEquals("123", integer.toJson());
        assertEquals("123", integer.toString());
        assertEquals(JsonLong.of(123L), integer);
        assertNotEquals(JsonLong.of(122L), integer);
        assertNotEquals(JsonDouble.of(123.0), integer);
        assertNotEquals(JsonDouble.of(123.00000001), integer);

        assertEquals(ValueFactory.newInteger(123L), integer.toMsgpack());

        // JsonLong#equals must normally reject a fake imitation of JsonLong.
        assertFalse(integer.equals(FakeJsonLong.of(integer.longValue())));
    }

    @Test
    public void testBasicShortValue() {
        final JsonLong integer = JsonLong.of(12345L);
        assertEquals(JsonValue.EntityType.LONG, integer.getEntityType());
        assertFalse(integer.isJsonNull());
        assertFalse(integer.isJsonBoolean());
        assertTrue(integer.isJsonLong());
        assertFalse(integer.isJsonDouble());
        assertFalse(integer.isJsonString());
        assertFalse(integer.isJsonArray());
        assertFalse(integer.isJsonObject());
        assertThrows(ClassCastException.class, () -> integer.asJsonNull());
        assertThrows(ClassCastException.class, () -> integer.asJsonBoolean());
        assertEquals(integer, integer.asJsonLong());
        assertThrows(ClassCastException.class, () -> integer.asJsonDouble());
        assertThrows(ClassCastException.class, () -> integer.asJsonString());
        assertThrows(ClassCastException.class, () -> integer.asJsonArray());
        assertThrows(ClassCastException.class, () -> integer.asJsonObject());
        assertEquals(8, integer.presumeReferenceSizeInBytes());
        assertTrue(integer.isIntegral());
        assertFalse(integer.isByteValue());
        assertTrue(integer.isShortValue());
        assertTrue(integer.isIntValue());
        assertTrue(integer.isLongValue());
        assertEquals((byte) 57, integer.byteValue());  // Overflow
        assertThrows(ArithmeticException.class, () -> integer.byteValueExact());
        assertEquals((short) 12345, integer.shortValue());
        assertEquals((short) 12345, integer.shortValueExact());
        assertEquals((int) 12345, integer.intValue());
        assertEquals((int) 12345, integer.intValueExact());
        assertEquals(12345L, integer.longValue());
        assertEquals(12345L, integer.longValueExact());
        assertEquals(BigInteger.valueOf(12345L), integer.bigIntegerValue());
        assertEquals(BigInteger.valueOf(12345L), integer.bigIntegerValueExact());
        assertEquals(12345.0, integer.floatValue(), 0.001);
        assertEquals(12345.0, integer.doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(12345L), integer.bigDecimalValue());
        assertEquals("12345", integer.toJson());
        assertEquals("12345", integer.toString());
        assertEquals(JsonLong.of(12345L), integer);
        assertNotEquals(JsonLong.of(12346L), integer);
        assertNotEquals(JsonDouble.of(12345.0), integer);
        assertNotEquals(JsonDouble.of(12345.00000001), integer);

        assertEquals(ValueFactory.newInteger(12345L), integer.toMsgpack());

        // JsonLong#equals must normally reject a fake imitation of JsonLong.
        assertFalse(integer.equals(FakeJsonLong.of(integer.longValue())));
    }

    @Test
    public void testBasicIntValue() {
        final JsonLong integer = JsonLong.of(1234567890L);
        assertEquals(JsonValue.EntityType.LONG, integer.getEntityType());
        assertFalse(integer.isJsonNull());
        assertFalse(integer.isJsonBoolean());
        assertTrue(integer.isJsonLong());
        assertFalse(integer.isJsonDouble());
        assertFalse(integer.isJsonString());
        assertFalse(integer.isJsonArray());
        assertFalse(integer.isJsonObject());
        assertThrows(ClassCastException.class, () -> integer.asJsonNull());
        assertThrows(ClassCastException.class, () -> integer.asJsonBoolean());
        assertEquals(integer, integer.asJsonLong());
        assertThrows(ClassCastException.class, () -> integer.asJsonDouble());
        assertThrows(ClassCastException.class, () -> integer.asJsonString());
        assertThrows(ClassCastException.class, () -> integer.asJsonArray());
        assertThrows(ClassCastException.class, () -> integer.asJsonObject());
        assertEquals(8, integer.presumeReferenceSizeInBytes());
        assertTrue(integer.isIntegral());
        assertFalse(integer.isByteValue());
        assertFalse(integer.isShortValue());
        assertTrue(integer.isIntValue());
        assertTrue(integer.isLongValue());
        assertEquals((byte) -46, integer.byteValue());  // Overflow
        assertThrows(ArithmeticException.class, () -> integer.byteValueExact());
        assertEquals((short) 722, integer.shortValue());  // Overflow
        assertThrows(ArithmeticException.class, () -> integer.shortValueExact());
        assertEquals((int) 1234567890, integer.intValue());
        assertEquals((int) 1234567890, integer.intValueExact());
        assertEquals(1234567890L, integer.longValue());
        assertEquals(1234567890L, integer.longValueExact());
        assertEquals(BigInteger.valueOf(1234567890L), integer.bigIntegerValue());
        assertEquals(BigInteger.valueOf(1234567890L), integer.bigIntegerValueExact());
        assertEquals(1234567890.0, integer.floatValue(), 1024.0);  // Large delta
        assertEquals(1234567890.0, integer.doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(1234567890L), integer.bigDecimalValue());
        assertEquals("1234567890", integer.toJson());
        assertEquals("1234567890", integer.toString());
        assertEquals(JsonLong.of(1234567890L), integer);
        assertNotEquals(JsonLong.of(1234567891L), integer);
        assertNotEquals(JsonDouble.of(1234567890.0), integer);
        assertNotEquals(JsonDouble.of(1234567890.00001), integer);

        assertEquals(ValueFactory.newInteger(1234567890L), integer.toMsgpack());

        // JsonLong#equals must normally reject a fake imitation of JsonLong.
        assertFalse(integer.equals(FakeJsonLong.of(integer.longValue())));
    }

    @Test
    public void testBasicLongValue() {
        final JsonLong integer = JsonLong.of(1234567890123456L);
        assertEquals(JsonValue.EntityType.LONG, integer.getEntityType());
        assertFalse(integer.isJsonNull());
        assertFalse(integer.isJsonBoolean());
        assertTrue(integer.isJsonLong());
        assertFalse(integer.isJsonDouble());
        assertFalse(integer.isJsonString());
        assertFalse(integer.isJsonArray());
        assertFalse(integer.isJsonObject());
        assertThrows(ClassCastException.class, () -> integer.asJsonNull());
        assertThrows(ClassCastException.class, () -> integer.asJsonBoolean());
        assertEquals(integer, integer.asJsonLong());
        assertThrows(ClassCastException.class, () -> integer.asJsonDouble());
        assertThrows(ClassCastException.class, () -> integer.asJsonString());
        assertThrows(ClassCastException.class, () -> integer.asJsonArray());
        assertThrows(ClassCastException.class, () -> integer.asJsonObject());
        assertEquals(8, integer.presumeReferenceSizeInBytes());
        assertTrue(integer.isIntegral());
        assertFalse(integer.isByteValue());
        assertFalse(integer.isShortValue());
        assertFalse(integer.isIntValue());
        assertTrue(integer.isLongValue());
        assertEquals((byte) -64, integer.byteValue());  // Overflow
        assertThrows(ArithmeticException.class, () -> integer.byteValueExact());
        assertEquals((short) -17728, integer.shortValue());  // Overflow
        assertThrows(ArithmeticException.class, () -> integer.shortValueExact());
        assertEquals((int) 1015724736, integer.intValue());  // Overflow
        assertThrows(ArithmeticException.class, () -> integer.intValueExact());
        assertEquals(1234567890123456L, integer.longValue());
        assertEquals(1234567890123456L, integer.longValueExact());
        assertEquals(BigInteger.valueOf(1234567890123456L), integer.bigIntegerValue());
        assertEquals(BigInteger.valueOf(1234567890123456L), integer.bigIntegerValueExact());
        assertEquals(1234567890123456.0, integer.floatValue(), 67108864.0);  // Large delta
        assertEquals(1234567890123456.0, integer.doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(1234567890123456L), integer.bigDecimalValue());
        assertEquals("1234567890123456", integer.toJson());
        assertEquals("1234567890123456", integer.toString());
        assertEquals(JsonLong.of(1234567890123456L), integer);
        assertNotEquals(JsonLong.of(1234567890123457L), integer);
        assertNotEquals(JsonDouble.of(1234567890123456.0), integer);
        assertNotEquals(JsonDouble.of(1234567890123457.0), integer);

        assertEquals(ValueFactory.newInteger(1234567890123456L), integer.toMsgpack());

        // JsonLong#equals must normally reject a fake imitation of JsonLong.
        assertFalse(integer.equals(FakeJsonLong.of(integer.longValue())));
    }

    @Test
    public void testWithLiteral() {
        final JsonLong integer = JsonLong.withLiteral(1234567890123456L, "999999999999999999991234567890123456");
        assertEquals(JsonValue.EntityType.LONG, integer.getEntityType());
        assertFalse(integer.isJsonNull());
        assertFalse(integer.isJsonBoolean());
        assertTrue(integer.isJsonLong());
        assertFalse(integer.isJsonDouble());
        assertFalse(integer.isJsonString());
        assertFalse(integer.isJsonArray());
        assertFalse(integer.isJsonObject());
        assertThrows(ClassCastException.class, () -> integer.asJsonNull());
        assertThrows(ClassCastException.class, () -> integer.asJsonBoolean());
        assertEquals(integer, integer.asJsonLong());
        assertThrows(ClassCastException.class, () -> integer.asJsonDouble());
        assertThrows(ClassCastException.class, () -> integer.asJsonString());
        assertThrows(ClassCastException.class, () -> integer.asJsonArray());
        assertThrows(ClassCastException.class, () -> integer.asJsonObject());
        assertEquals(80, integer.presumeReferenceSizeInBytes());
        assertTrue(integer.isIntegral());
        assertFalse(integer.isByteValue());
        assertFalse(integer.isShortValue());
        assertFalse(integer.isIntValue());
        assertTrue(integer.isLongValue());
        assertEquals((byte) -64, integer.byteValue());  // Overflow
        assertThrows(ArithmeticException.class, () -> integer.byteValueExact());
        assertEquals((short) -17728, integer.shortValue());  // Overflow
        assertThrows(ArithmeticException.class, () -> integer.shortValueExact());
        assertEquals((int) 1015724736, integer.intValue());  // Overflow
        assertThrows(ArithmeticException.class, () -> integer.intValueExact());
        assertEquals(1234567890123456L, integer.longValue());
        assertEquals(1234567890123456L, integer.longValueExact());
        assertEquals(BigInteger.valueOf(1234567890123456L), integer.bigIntegerValue());
        assertEquals(BigInteger.valueOf(1234567890123456L), integer.bigIntegerValueExact());
        assertEquals(1234567890123456.0, integer.floatValue(), 67108864.0);  // Large delta
        assertEquals(1234567890123456.0, integer.doubleValue(), 0.001);
        assertEquals(BigDecimal.valueOf(1234567890123456L), integer.bigDecimalValue());
        assertEquals("999999999999999999991234567890123456", integer.toJson());
        assertEquals("1234567890123456", integer.toString());
        assertEquals(JsonLong.of(1234567890123456L), integer);
        assertNotEquals(JsonLong.of(1234567890123457L), integer);
        assertNotEquals(JsonDouble.of(1234567890123456.0), integer);
        assertNotEquals(JsonDouble.of(1234567890123457.0), integer);

        assertEquals(ValueFactory.newInteger(1234567890123456L), integer.toMsgpack());

        // JsonLong#equals must normally reject a fake imitation of JsonLong.
        assertFalse(integer.equals(FakeJsonLong.of(integer.longValue())));
    }

    @Test
    public void testFromMsgpack() {
        assertEquals(JsonLong.of(0), JsonValue.fromMsgpack(ValueFactory.newInteger(0)));
        assertEquals(JsonLong.of(-0), JsonValue.fromMsgpack(ValueFactory.newInteger(-0)));
        assertEquals(JsonLong.of(42), JsonValue.fromMsgpack(ValueFactory.newInteger(42)));
        assertEquals(JsonLong.of(Long.MAX_VALUE), JsonValue.fromMsgpack(ValueFactory.newInteger(Long.MAX_VALUE)));
        assertEquals(JsonLong.of(Long.MAX_VALUE), JsonValue.fromMsgpack(ValueFactory.newInteger(BigInteger.valueOf(Long.MAX_VALUE))));
        assertThrows(IllegalArgumentException.class, () -> JsonValue.fromMsgpack(ValueFactory.newInteger(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE))));
    }

    @Test
    public void testToMsgpack() {
        final JsonLong integer = JsonLong.of(42L);
        assertEquals(ValueFactory.newInteger(42L), integer.toMsgpack());
        assertSame(integer.toMsgpack(), integer.toMsgpack());

        // JsonLong#toMsgpack returns the same MessagePack value as-is that is given to JsonValue#fromMsgpack.
        final Value msgpackInteger = ValueFactory.newInteger(42L);
        assertSame(msgpackInteger, JsonValue.fromMsgpack(msgpackInteger).toMsgpack());

        final Value msgpackBigInteger = ValueFactory.newInteger(BigInteger.valueOf(Long.MAX_VALUE));
        final JsonValue fromBigInteger = JsonValue.fromMsgpack(msgpackBigInteger);
        assertEquals(ValueFactory.newInteger(Long.MAX_VALUE), fromBigInteger.toMsgpack());
        assertSame(fromBigInteger.toMsgpack(), fromBigInteger.toMsgpack());
    }

    @Test
    public void testHashCode() {
        // JsonLong#hashCode returns the same hash code as Long#hashCode.
        assertEquals(0, JsonLong.of(0L).hashCode());
        assertEquals(42, JsonLong.of(42L).hashCode());
        assertEquals(0, JsonLong.of(-1L).hashCode());
        assertEquals(41, JsonLong.of(-42L).hashCode());

        // Note that JsonLong#hashCode returned the same hash code as the corresponding MessagePack's Integer value in the Embulk
        // SPI v0.11 and earlier. It is different from Long#hashCode for a negative integer in the range of int.
        assertEquals(-1, ValueFactory.newInteger(-1L).hashCode());
        assertEquals(-42, ValueFactory.newInteger(-42L).hashCode());

        final long[] values = {
            0L,
            1L,
            -1L,
            42L,
            -42L,
            (long) Integer.MAX_VALUE,
            (long) Integer.MIN_VALUE,
            ((long) Integer.MAX_VALUE) + 1L,
            ((long) Integer.MIN_VALUE) - 1L,
            1234567890123456L,
            -1234567890123456L,
            Long.MAX_VALUE,
            Long.MIN_VALUE,
        };
        for (final long value : values) {
            assertEquals(Long.hashCode(value), JsonLong.of(value).hashCode());
            assertEquals(Long.valueOf(value).hashCode(), JsonLong.of(value).hashCode());
            assertEquals(Long.hashCode(value), JsonValue.fromMsgpack(ValueFactory.newInteger(value)).hashCode());
        }
    }

    @Test
    public void testEqualsAndHashCode() {
        final long[] values = {
            0L,
            1L,
            -1L,
            42L,
            -42L,
            (long) Integer.MAX_VALUE,
            (long) Integer.MIN_VALUE,
            ((long) Integer.MAX_VALUE) + 1L,
            ((long) Integer.MIN_VALUE) - 1L,
            1234567890123456L,
            -1234567890123456L,
            Long.MAX_VALUE,
            Long.MIN_VALUE,
        };
        for (final long left : values) {
            for (final long right : values) {
                final JsonLong jsonLeft = JsonLong.of(left);

                // It does not matter how they are created.
                final JsonValue[] jsonRights = {
                    JsonLong.of(right),
                    JsonLong.withLiteral(right, "literal"),
                    JsonValue.fromMsgpack(ValueFactory.newInteger(right)),
                };
                for (final JsonValue jsonRight : jsonRights) {
                    // JsonLong#equals is in the same manner as Long#equals.
                    assertEquals(Long.valueOf(left).equals(Long.valueOf(right)), jsonLeft.equals(jsonRight));
                    assertEquals(Long.valueOf(right).equals(Long.valueOf(left)), jsonRight.equals(jsonLeft));

                    // Equal instances must have the same hash code.
                    if (jsonLeft.equals(jsonRight)) {
                        assertEquals(jsonLeft.hashCode(), jsonRight.hashCode());
                    }
                }
            }
        }
    }
}
