/*
 * Copyright 2022 The Embulk project
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

import java.math.BigDecimal;
import java.math.BigInteger;
import org.msgpack.value.FloatValue;
import org.msgpack.value.Value;
import org.msgpack.value.impl.ImmutableDoubleValueImpl;

/**
 * Represents a number in JSON, represented by a Java primitive {@code double}, which is the same as Embulk's {@code DOUBLE} column type.
 *
 * <p>It does not accept {@code NaN} (Not-a-Number) and the infinity.
 *
 * <p>However, a {@link JsonDouble} instance can have {@code NaN} or the infinity when it is created through
 * {@link JsonValue#fromMsgpack(Value)} from MessagePack's Float value that has {@code NaN} or the infinity.
 * Note that {@link #toJson()} and {@link #toString ()} may not return a valid JSON representation for
 * a {@link JsonDouble} instance that has {@code NaN} or the infinity.
 *
 * <p>{@link #of(double)} and {@link #withLiteral(double, String)} still never accept {@code NaN} and the infinity.
 * {@link JsonDouble} is designed to work consistently even with {@code NaN} and the infinity for such an instance,
 * and for the future possibility to accept {@code NaN} and the infinity.
 *
 * <p>Note that a {@link JsonDouble} instance cannot represent every integral number whose absolute value is greater than
 * 2<sup>53</sup>. For such a large number, {@link #longValue()}, {@link #bigIntegerValue()}, {@link #bigDecimalValue()},
 * and their {@code Exact} variants may return numbers that are different from each other in their less significant digits,
 * while they are the same in the precision of {@code double}.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc8259">RFC 8259 - The JavaScript Object Notation (JSON) Data Interchange Format</a>
 *
 * @since 0.10.42
 */
public final class JsonDouble implements JsonNumber {
    private JsonDouble(final double value, final String literal) {
        if (Double.isNaN(value)) {
            throw new ArithmeticException("JsonDouble does not accept NaN.");
        }
        if (Double.isInfinite(value)) {
            throw new ArithmeticException("JsonDouble does not accept the infinity.");
        }
        this.value = value;
        this.literal = literal;
        this.msgpackDoubleCache = null;
    }

    private JsonDouble(final ImmutableDoubleValueImpl msgpackValue) {
        // This constructor is internal only for JsonValue#fromMsgpack. It accepts NaN and the infinity as-is,
        // while the constructor above for #of and #withLiteral never accepts NaN and the infinity.
        this.value = msgpackValue.toDouble();
        this.literal = null;
        this.msgpackDoubleCache = msgpackValue;
    }

    static JsonDouble fromMsgpack(final FloatValue msgpackValue) {
        // This cast should always succeed.
        return new JsonDouble((ImmutableDoubleValueImpl) msgpackValue.immutableValue());
    }

    /**
     * Returns a JSON number represented by the specified Java primitive {@code double}.
     *
     * @param value  the number
     * @return a JSON number represented by the specified Java primitive {@code double}
     * @throws ArithmeticException  if the specified number is {@code NaN} (Not-a-Number) or infinite
     *
     * @since 0.10.42
     */
    public static JsonDouble of(final double value) {
        return new JsonDouble(value, null);
    }

    /**
     * Returns a JSON number that is represented by the specified Java primitive {@code double}, with the specified JSON literal.
     *
     * <p>The literal is just subsidiary information used when stringifying this JSON number as JSON by {@link #toJson()}.
     *
     * @param value  the number
     * @param literal  the JSON literal of the number
     * @return a JSON number represented by the specified Java primitive {@code double}
     * @throws ArithmeticException  if the specified number is {@code NaN} (Not-a-Number) or infinite
     *
     * @since 0.10.42
     */
    public static JsonDouble withLiteral(final double value, final String literal) {
        return new JsonDouble(value, literal);
    }

    /**
     * Returns {@link JsonValue.EntityType#DOUBLE}, which is the entity type of {@link JsonDouble}.
     *
     * @return {@link JsonValue.EntityType#DOUBLE}, which is the entity type of {@link JsonDouble}
     *
     * @since 0.10.42
     */
    @Override
    public EntityType getEntityType() {
        return EntityType.DOUBLE;
    }

    /**
     * Returns this value as {@link JsonDouble}.
     *
     * @return itself as {@link JsonDouble}
     *
     * @since 0.10.42
     */
    @Override
    public JsonDouble asJsonDouble() {
        return this;
    }

    /**
     * Returns {@code 8} for the size of {@code double} in bytes presumed to occupy in {@link org.embulk.spi.Page} as a reference.
     *
     * <p>This approximate size is used only as a threshold whether {@link org.embulk.spi.PageBuilder} is flushed, or not.
     * It is not accurate, it does not need to be accurate, and it is impossible in general to tell an accurate size that
     * a Java object occupies in the Java heap. But, a reasonable approximate would help to keep {@link org.embulk.spi.Page}
     * performant in the Java heap.
     *
     * <p>It is better to flush more frequently for bigger JSON value objects, less often for smaller JSON value objects,
     * but no infinite accumulation even for empty JSON value objects.
     *
     * @return {@code 8}
     *
     * @see "org.embulk.spi.PageBuilderImpl#addRecord"
     *
     * @see <a href="https://github.com/airlift/slice/blob/0.9/src/main/java/io/airlift/slice/SizeOf.java#L42">SIZE_OF_DOUBLE in Airlift's Slice</a>
     */
    @Override
    public int presumeReferenceSizeInBytes() {
        if (this.literal == null) {
            return 8;
        }
        return this.literal.length() * 2 + 8;
    }

    /**
     * Returns {@code true} if this JSON number is integral.
     *
     * <p>Note that it does not guarantee this JSON number can be represented as a Java primitive exact {@code long}.
     * This JSON number can be out of the range of the Java primitive {@code long}.
     *
     * <p>It returns {@code false} for a {@link JsonDouble} instance that has {@code NaN} or the infinity, which can
     * happen when the instance is created through {@link JsonValue#fromMsgpack(Value)}. Note that it returned
     * {@code true} for the infinity in the Embulk SPI v0.11 and earlier.
     *
     * @return {@code true} if this JSON number is integral
     *
     * @since 0.10.42
     */
    @Override
    public boolean isIntegral() {
        // |this.value| should not be NaN nor the infinity, but it can have NaN nor the infinity when it is created
        // through JsonValue#fromMsgpack.
        //
        // NaN and the infinity are not integral.
        //
        // this.value == Math.rint(this.value) is false for NaN because Math.rint returns NaN for NaN.
        // On the other hand, the infinity needs the explicit check because Math.rint returns the infinity for the infinity.
        return !Double.isInfinite(this.value) && this.value == Math.rint(this.value);
    }

    /**
     * Returns {@code true} if the JSON number is integral in the range of {@code byte}, [-2<sup>7</sup> to 2<sup>7</sup>-1].
     *
     * @return {@code true} if the JSON number is integral in the range of {@code byte}
     *
     * @since 0.10.42
     */
    @Override
    public boolean isByteValue() {
        return this.isIntegral() && ((double) Byte.MIN_VALUE) <= this.value && this.value <= ((double) Byte.MAX_VALUE);
    }

    /**
     * Returns {@code true} if the JSON number is integral in the range of {@code short}, [-2<sup>15</sup> to 2<sup>15</sup>-1].
     *
     * @return {@code true} if the JSON number is integral in the range of {@code short}
     *
     * @since 0.10.42
     */
    @Override
    public boolean isShortValue() {
        return this.isIntegral() && ((double) Short.MIN_VALUE) <= this.value && this.value <= ((double) Short.MAX_VALUE);
    }

    /**
     * Returns {@code true} if the JSON number is integral in the range of {@code int}, [-2<sup>31</sup> to 2<sup>31</sup>-1].
     *
     * @return {@code true} if the JSON number is integral in the range of {@code int}
     *
     * @since 0.10.42
     */
    @Override
    public boolean isIntValue() {
        return this.isIntegral() && ((double) Integer.MIN_VALUE) <= this.value && this.value <= ((double) Integer.MAX_VALUE);
    }

    /**
     * Returns {@code true} if the JSON number is integral in the range of {@code long}, [-2<sup>63</sup> to 2<sup>63</sup>-1].
     *
     * <p>Note that it returned {@code true} for 2<sup>63</sup>, which is out of the range of {@code long}, in the Embulk SPI
     * v0.11 and earlier.
     *
     * @return {@code true} if the JSON number is integral in the range of {@code long}
     *
     * @since 0.10.42
     */
    @Override
    public boolean isLongValue() {
        // The upper bound must be exclusive with 2^63, not inclusive with (double) Long.MAX_VALUE.
        //
        // Long.MAX_VALUE, 2^63-1, cannot be represented exactly in double. (double) Long.MAX_VALUE is rounded up to 2^63,
        // which is out of the range of long.
        return this.isIntegral() && -0x1p63 <= this.value && this.value < 0x1p63;
    }

    /**
     * Returns this JSON number as a Java primitive {@code byte}.
     *
     * <p>It narrows down {@code double} to {@code byte} as a Java primitive. Note that this conversion can lose information
     * about the magnitude of this JSON number, precision, and range.
     *
     * @return the {@code byte} representation of this JSON number
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.3">Java Language Specification - 5.1.3. Narrowing Primitive Conversion</a>
     *
     * @since 0.10.42
     */
    @Override
    public byte byteValue() {
        return (byte) this.value;
    }

    /**
     * Returns this JSON number as a Java primitive {@code byte}.
     *
     * <p>It throws {@link ArithmeticException} if the JSON number is out of the range of {@code byte}, or has a
     * non-zero fractional part.
     *
     * @return the {@code byte} representation of this JSON number
     * @throws ArithmeticException  if the JSON number is out of the range of {@code byte}, or has a non-zero fractional part
     *
     * @since 0.10.42
     */
    @Override
    public byte byteValueExact() {
        if (!this.isByteValue()) {
            throw new ArithmeticException("Out of the range of byte, or not integral: " + this.value);
        }
        return (byte) this.value;
    }

    /**
     * Returns this JSON number as a Java primitive {@code short}.
     *
     * <p>It narrows down {@code double} to {@code short} as a Java primitive. Note that this conversion can lose information
     * about the magnitude of this JSON number, precision, and range.
     *
     * @return the {@code short} representation of this JSON number
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.3">Java Language Specification - 5.1.3. Narrowing Primitive Conversion</a>
     *
     * @since 0.10.42
     */
    @Override
    public short shortValue() {
        return (short) this.value;
    }

    /**
     * Returns this JSON number as a Java primitive {@code short}.
     *
     * <p>It throws {@link ArithmeticException} if the JSON number is out of the range of {@code short}, or has a
     * non-zero fractional part.
     *
     * @return the {@code short} representation of this JSON number
     * @throws ArithmeticException  if the JSON number is out of the range of {@code short}, or has a non-zero fractional part
     *
     * @since 0.10.42
     */
    @Override
    public short shortValueExact() {
        if (!this.isShortValue()) {
            throw new ArithmeticException("Out of the range of short, or not integral: " + this.value);
        }
        return (short) this.value;
    }

    /**
     * Returns this JSON number as a Java primitive {@code int}.
     *
     * <p>It narrows down {@code double} to {@code int} as a Java primitive. Note that this conversion can lose information
     * about the magnitude of this JSON number, precision, and range.
     *
     * @return the {@code int} representation of this JSON number
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.3">Java Language Specification - 5.1.3. Narrowing Primitive Conversion</a>
     *
     * @since 0.10.42
     */
    @Override
    public int intValue() {
        return (int) this.value;
    }

    /**
     * Returns this JSON number as a Java primitive {@code int}.
     *
     * <p>It throws {@link ArithmeticException} if the JSON number is out of the range of {@code int}, or has a
     * non-zero fractional part.
     *
     * @return the {@code int} representation of this JSON number
     * @throws ArithmeticException  if the JSON number is out of the range of {@code int}, or has a non-zero fractional part
     *
     * @since 0.10.42
     */
    @Override
    public int intValueExact() {
        if (!this.isIntValue()) {
            throw new ArithmeticException("Out of the range of int, or not integral: " + this.value);
        }
        return (int) this.value;
    }

    /**
     * Returns this JSON number as a Java primitive {@code long}.
     *
     * <p>It narrows down {@code double} to {@code long} as a Java primitive. Note that this conversion can lose information
     * about the magnitude of this JSON number, precision, and range.
     *
     * @return the {@code long} representation of this JSON number
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.3">Java Language Specification - 5.1.3. Narrowing Primitive Conversion</a>
     *
     * @since 0.10.42
     */
    @Override
    public long longValue() {
        return (long) this.value;
    }

    /**
     * Returns this JSON number as a Java primitive {@code long}.
     *
     * <p>It throws {@link ArithmeticException} if the JSON number is out of the range of {@code long}, or has a
     * non-zero fractional part.
     *
     * <p>Note that it returned {@link Long#MAX_VALUE} for 2<sup>63</sup>, which is out of the range of {@code long},
     * without throwing {@link ArithmeticException}, in the Embulk SPI v0.11 and earlier.
     *
     * @return the {@code long} representation of this JSON number
     * @throws ArithmeticException  if the JSON number is out of the range of {@code long}, or has a non-zero fractional part
     *
     * @since 0.10.42
     */
    @Override
    public long longValueExact() {
        if (!this.isLongValue()) {
            throw new ArithmeticException("Out of the range of long, or not integral: " + this.value);
        }
        return (long) this.value;
    }

    /**
     * Returns this JSON number as a {@link java.math.BigInteger}.
     *
     * <p>Note that this conversion loses the fractional part and the precision of the number.
     * This is a convenience method for {@code bigDecimalValue().toBigInteger()}.
     *
     * <p>It throws {@link ArithmeticException} for a {@link JsonDouble} instance that has {@code NaN} or the infinity.
     * Note that it threw {@link NumberFormatException} for {@code NaN} and the infinity in the Embulk SPI v0.11 and earlier.
     *
     * @return the {@link java.math.BigInteger} representation of this JSON number
     * @throws ArithmeticException  if the JSON number is {@code NaN} or the infinity
     *
     * @since 0.10.42
     */
    @Override
    public BigInteger bigIntegerValue() {
        return this.bigDecimalValueInternal().toBigInteger();
    }

    /**
     * Returns this JSON number as a {@link java.math.BigInteger}.
     *
     * <p>It throws {@link ArithmeticException} if the JSON number has a non-zero fractional part.
     *
     * <p>It also throws {@link ArithmeticException} for a {@link JsonDouble} instance that has {@code NaN} or the infinity.
     * Note that it threw {@link NumberFormatException} for {@code NaN} and the infinity in the Embulk SPI v0.11 and earlier.
     *
     * @return the {@link java.math.BigInteger} representation of this JSON number
     * @throws ArithmeticException  if the JSON number has a non-zero fractional part, or if the JSON number is {@code NaN} or the infinity
     *
     * @since 0.10.42
     */
    @Override
    public BigInteger bigIntegerValueExact() {
        return this.bigDecimalValueInternal().toBigIntegerExact();
    }

    /**
     * Returns this JSON number as a Java primitive {@code float}.
     *
     * <p>It narrows down {@code double} to {@code float} as a Java primitive. Note that this conversion can lose information
     * about the magnitude and the precision of the number.
     *
     * @return the {@code float} representation of this JSON number
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.3">Java Language Specification - 5.1.3. Na
rrowing Primitive Conversion</a>
     *
     * @since 0.10.42
     */
    @Override
    public float floatValue() {
        return (float) this.value;
    }

    /**
     * Returns this JSON number as a Java primitive {@code double}, as-is.
     *
     * <p>This method does not lose any information because {@link JsonDouble} represents the number as a Java primitive
     * {@code double} inside.
     *
     * @return the {@code double} representation of this JSON number
     */
    @Override
    public double doubleValue() {
        return this.value;
    }

    /**
     * Returns this JSON number as {@link java.math.BigDecimal}.
     *
     * <p>It throws {@link ArithmeticException} for a {@link JsonDouble} instance that has {@code NaN} or the infinity.
     * Note that it threw {@link NumberFormatException} for {@code NaN} and the infinity in the Embulk SPI v0.11 and earlier.
     *
     * @return the {@link java.math.BigDecimal} representation of this JSON number
     * @throws ArithmeticException  if the JSON number is {@code NaN} or the infinity
     */
    @Override
    public BigDecimal bigDecimalValue() {
        return this.bigDecimalValueInternal();
    }

    /**
     * Returns the stringified JSON representation of this JSON number.
     *
     * <p>If this JSON number is created with a literal by {@link #withLiteral(double, String)}, it returns the literal.
     *
     * <p>Note that it may not return a valid JSON representation for a {@link JsonDouble} instance
     * that has {@code NaN} or the infinity. The representation for {@code NaN} and the infinity may change in the future.
     *
     * @return the stringified JSON representation of this JSON number
     *
     * @since 0.10.42
     */
    @Override
    public String toJson() {
        // A JsonDouble instance can have NaN or the infinity when it is created through JsonValue#fromMsgpack.
        //
        // It returns "NaN", "Infinity", or "-Infinity" as-is for NaN and the infinity for now.
        // Note that "NaN", "Infinity", and "-Infinity" are not valid JSON representations.
        //
        // TODO: Reconsider the output for NaN and the infinity.
        if (this.literal != null) {
            return this.literal;
        }
        return Double.toString(this.value);
    }

    /**
     * Returns the corresponding MessagePack's Float value of this JSON number.
     *
     * @return the corresponding MessagePack's Float value of this JSON number
     *
     * @see <a href="https://github.com/embulk/embulk/pull/1538">Draft EEP: JSON Column Type</a>
     *
     * @deprecated Do not use this method. It is to be removed at some point after Embulk v1.0.0.
     *     It is here only to ensure a migration period from MessagePack-based JSON values to new
     *     JSON values of {@link JsonValue}.
     *
     * @since 0.10.42
     */
    @Deprecated
    @Override
    public Value toMsgpack() {
        if (this.msgpackDoubleCache != null) {
            return this.msgpackDoubleCache;
        }

        this.msgpackDoubleCache = new ImmutableDoubleValueImpl(this.value);
        return this.msgpackDoubleCache;
    }

    /**
     * Returns the string representation of this JSON number.
     *
     * <p>Note that it may not return a valid JSON representation for a {@link JsonDouble} instance
     * that has {@code NaN} or the infinity. The representation for {@code NaN} and the infinity may change in the future.
     *
     * @return the string representation of this JSON number
     *
     * @since 0.10.42
     */
    @Override
    public String toString() {
        // A JsonDouble instance can have NaN or the infinity when it is created through JsonValue#fromMsgpack.
        //
        // It returns "NaN", "Infinity", or "-Infinity" as-is for NaN and the infinity for now.
        // Note that "NaN", "Infinity", and "-Infinity" are not valid JSON representations.
        //
        // TODO: Reconsider the output for NaN and the infinity.
        return Double.toString(this.value);
    }

    /**
     * Compares the specified object with this JSON number for equality.
     *
     * <p>A {@link JsonDouble} instance and a {@link JsonLong} instance are NOT considered to be equal by {@link #equals(Object)}
     * since the Embulk SPI v0.12, even if they represent the same integral number, such as {@code 1.0} and {@code 1}.
     *
     * <p>Such instances that represent the same integral number were considered to be equal in the Embulk SPI v0.11 and earlier.
     *
     * <p>Two different {@link JsonDouble} instances that have {@code NaN} are considered to be equal by {@link #equals(Object)}
     * since the Embulk SPI v0.12, in the same manner as {@link Double#equals(Object)}.
     *
     * <p>Note that such {@code NaN} instances were usually considered NOT to be equal in the Embulk SPI v0.11 and earlier.
     * They were equal only when they were created from the same {@link org.msgpack.value.ImmutableFloatValue} instance.
     *
     * <p>Two {@link JsonDouble} instances that have {@code 0.0} and {@code -0.0} are NOT considered to be equal by
     * {@link #equals(Object)} since the Embulk SPI v0.12, in the same manner as {@link Double#equals(Object)}.
     *
     * <p>Such instances of {@code 0.0} and {@code -0.0} were considered to be equal in the Embulk SPI v0.11 and earlier.
     *
     * <p>Two different {@link JsonDouble} instances that have the infinity are considered to be equal by {@link #equals(Object)}
     * when both have the positive infinity, or when both have the negative infinity. A combination of the positive infinity
     * and the negative infinity is considered not to be equal.
     *
     * @return {@code true} if the specified object is equal to this JSON number
     *
     * @since 0.10.42
     */
    @Override
    public boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        // Check by `instanceof` in case against unexpected arbitrary extension of JsonValue.
        if (otherObject instanceof JsonDouble) {
            final JsonDouble other = (JsonDouble) otherObject;

            // It compares the bit patterns in the same manner as Double#equals, not by == of primitive double.
            //
            // * NaN is considered to be equal to NaN, while NaN != NaN as primitive double.
            // * 0.0 and -0.0 are considered NOT to be equal, while 0.0 == -0.0 as primitive double.
            //
            // Double#doubleToLongBits returns the same bit pattern for any NaN. It is consistent with #hashCode.
            return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.value);
        }

        // JsonDouble is never equal to JsonLong even if they represent the same integral number.
        return false;
    }

    /**
     * Returns the hash code value for this JSON number.
     *
     * @return the hash code value for this JSON number
     *
     * @since 0.10.42
     */
    @Override
    public int hashCode() {
        // It is the same as the hash code of MessagePack's Float value.
        return Double.hashCode(this.value);
    }

    private BigDecimal bigDecimalValueInternal() {
        // A JsonDouble instance can have NaN or the infinity when it is created through JsonValue#fromMsgpack.
        // BigDecimal cannot represent NaN and the infinity. BigDecimal.valueOf throws NumberFormatException for NaN and
        // the infinity. It throws ArithmeticException instead, consistently with the other methods of JsonDouble.
        if (Double.isNaN(this.value) || Double.isInfinite(this.value)) {
            throw new ArithmeticException("Not a finite number: " + this.value);
        }
        return BigDecimal.valueOf(this.value);
    }

    private final double value;

    private final String literal;

    private ImmutableDoubleValueImpl msgpackDoubleCache;
}
