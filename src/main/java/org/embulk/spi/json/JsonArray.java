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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;
import org.msgpack.value.impl.ImmutableArrayValueImpl;

/**
 * Represents an array in JSON.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc8259">RFC 8259 - The JavaScript Object Notation (JSON) Data Interchange Format</a>
 *
 * @since 0.10.42
 */
public final class JsonArray extends AbstractList<JsonValue> implements JsonValue {
    private JsonArray(final JsonValue[] values) {
        this.values = values;
        this.msgpackArrayCache = null;
    }

    private JsonArray(final JsonValue[] values, final ImmutableArrayValueImpl msgpackValue) {
        this.values = values;
        this.msgpackArrayCache = msgpackValue;
    }

    @SuppressWarnings("deprecation")
    static JsonArray fromMsgpack(final ArrayValue msgpackValue) {
        // This cast should always succeed.
        final ImmutableArrayValueImpl immutable = (ImmutableArrayValueImpl) msgpackValue.immutableValue();

        final int size = immutable.size();
        final JsonValue[] values = new JsonValue[size];

        for (int i = 0; i < size; i++) {
            values[i] = JsonValue.fromMsgpack(immutable.get(i));
        }

        return new JsonArray(values, immutable);
    }

    /**
     * Returns a JSON array containing zero JSON values.
     *
     * @return an empty JSON array
     *
     * @since 0.10.42
     */
    public static JsonArray of() {
        return EMPTY;
    }

    /**
     * Returns a JSON array containing an arbitrary number of JSON values.
     *
     * @param values  the JSON values to be contained in the JSON array, not null
     * @return a JSON array containing the specified JSON values
     * @throws NullPointerException  if the array, or any value is {@code null}
     *
     * @since 0.10.42
     */
    public static JsonArray of(final JsonValue... values) {
        if (values == null) {
            throw new NullPointerException("values is null.");
        }
        if (values.length == 0) {
            return EMPTY;
        }
        for (final JsonValue value : values) {
            if (value == null) {
                throw new NullPointerException("values has null.");
            }
        }
        return new JsonArray(Arrays.copyOf(values, values.length));
    }

    /**
     * Returns a JSON array containing an arbitrary number of JSON values.
     *
     * @param values  the JSON values to be contained in the JSON array, not null
     * @return a JSON array containing the specified JSON values
     * @throws NullPointerException  if the list, or any value is {@code null}
     *
     * @since 0.10.42
     */
    public static JsonArray ofList(final List<? extends JsonValue> values) {
        if (values == null) {
            throw new NullPointerException("values is null.");
        }
        if (values.isEmpty()) {
            return EMPTY;
        }
        for (final JsonValue value : values) {
            if (value == null) {
                throw new NullPointerException("values has null.");
            }
        }
        return new JsonArray(values.toArray(new JsonValue[values.size()]));
    }

    /**
     * Returns a JSON array containing the specified array as its internal representation.
     *
     * <p><strong>This method is not safe.</strong> If the specified array is modified after creating a {@link JsonArray}
     * instance with this method, the created {@link JsonArray} instance can unintentionally behave differently.
     *
     * <p>It has no {@code null} checks. If the specified array is {@code null}, or contains {@code null},
     * the created {@link JsonArray} can behave unexpectedly.
     *
     * @param array  the array of JSON values to be the internal representation as-is in the new {@link JsonArray}
     * @return a JSON array containing the specified array as the internal representation
     *
     * @since 0.10.42
     */
    public static JsonArray ofUnsafe(final JsonValue... array) {
        return new JsonArray(array);
    }

    /**
     * Returns {@link JsonValue.EntityType#ARRAY}, which is the entity type of {@link JsonArray}.
     *
     * @return {@link JsonValue.EntityType#ARRAY}, which is the entity type of {@link JsonArray}
     *
     * @since 0.10.42
     */
    @Override
    public EntityType getEntityType() {
        return EntityType.ARRAY;
    }

    /**
     * Returns this value as {@link JsonArray}.
     *
     * @return itself as {@link JsonArray}
     *
     * @since 0.10.42
     */
    @Override
    public JsonArray asJsonArray() {
        return this;
    }

    /**
     * Returns the approximate size of this JSON array in bytes presumed to occupy in {@link org.embulk.spi.Page} as a reference.
     *
     * <p>This approximate size is used only as a threshold whether {@link org.embulk.spi.PageBuilder} is flushed, or not.
     * It is not accurate, it does not need to be accurate, and it is impossible in general to tell an accurate size that
     * a Java object occupies in the Java heap. But, a reasonable approximate would help to keep {@link org.embulk.spi.Page}
     * performant in the Java heap.
     *
     * <p>It is better to flush more frequently for bigger JSON value objects, less often for smaller JSON value objects,
     * but no infinite accumulation even for empty JSON value objects.
     *
     * @return the approximate size of this JSON array in bytes presumed to occupy in {@link org.embulk.spi.Page} as a reference
     *
     * @see "org.embulk.spi.PageBuilderImpl#addRecord"
     */
    @Override
    public int presumeReferenceSizeInBytes() {
        // No clear reason for the number 4.
        // But at least, it should not be 0 so that the approximate size of an empty array would not be 0.
        int sum = 4;

        for (int i = 0; i < this.values.length; i++) {
            sum += this.values[i].presumeReferenceSizeInBytes();
        }
        return sum;
    }

    /**
     * Returns the number of JSON values in this array.
     *
     * @return the number of JSON values in this array
     *
     * @since 0.10.42
     */
    @Override
    public int size() {
        return this.values.length;
    }

    /**
     * Returns the JSON value at the specified position in this array.
     *
     * @param index  index of the JSON value to return
     * @return the JSON value at the specified position in this array
     * @throws IndexOutOfBoundsException  If the index is out of range ({@code index < 0 || index >= size()})
     *
     * @since 0.10.42
     */
    @Override
    public JsonValue get(final int index) {
        return this.values[index];
    }

    /**
     * Returns the stringified JSON representation of this JSON array.
     *
     * @return the stringified JSON representation of this JSON array
     *
     * @since 0.10.42
     */
    @Override
    public String toJson() {
        if (this.values.length == 0) {
            return "[]";
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(this.values[0].toJson());
        for (int i = 1; i < this.values.length; i++) {
            builder.append(",");
            builder.append(this.values[i].toJson());
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Returns the corresponding MessagePack's Array value of this JSON array.
     *
     * @return the corresponding MessagePack's Array value of this JSON array
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
    @SuppressWarnings("deprecation")  // To call #toMsgpack() of other JsonValue.
    @Override
    public Value toMsgpack() {
        if (this.msgpackArrayCache != null) {
            return this.msgpackArrayCache;
        }

        final Value[] msgpackValues = new Value[this.values.length];
        for (int i = 0; i < this.values.length; i++) {
            msgpackValues[i] = this.values[i].toMsgpack();
        }
        this.msgpackArrayCache = new ImmutableArrayValueImpl(msgpackValues);
        return this.msgpackArrayCache;
    }

    /**
     * Returns the string representation of this JSON array.
     *
     * @return the string representation of this JSON array
     *
     * @since 0.10.42
     */
    @Override
    public String toString() {
        if (this.values.length == 0) {
            return "[]";
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(this.values[0].toString());
        for (int i = 1; i < this.values.length; i++) {
            builder.append(",");
            builder.append(this.values[i].toString());
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Compares the specified object with this JSON array for equality.
     *
     * <p>It returns {@code true} if and only if the specified object is also a {@link java.util.List}, both lists have the same
     * size, and all corresponding pairs of elements in the two lists are equal.
     *
     * <p>Note that it can return {@code true} not only for {@link JsonArray}, but also for a general {@link java.util.List},
     * such as {@link java.util.ArrayList}, when the given list contains equal {@link JsonValue} elements in the same order.
     * It follows the general contract of {@link java.util.List#equals(Object)} as {@link JsonArray} is a {@link java.util.List},
     * so that the equality is symmetric with any other {@link java.util.List} implementation.
     *
     * <p>Also note that it does not return {@code true} for a {@link java.util.List} that contains a Java object that is not a
     * {@link JsonValue}. For example, a {@link java.util.List} that contains a Java {@link java.lang.String} instead of a
     * {@link JsonString} is never equal to {@link JsonArray} as {@link JsonString} is never equal to {@link java.lang.String}.
     *
     * <p>It returned {@code true} only for {@link JsonArray} until the Embulk SPI v0.11. However, that {@code equals} behavior
     * violated the symmetry required by its contract. It has changed since the Embulk SPI v0.12.
     *
     * @return {@code true} if the specified object is equal to this JSON array
     *
     * @see java.util.List#equals(Object)
     *
     * @since 0.10.42
     */
    @Override
    public boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        if (otherObject instanceof JsonArray) {
            // A shortcut for JsonArray. It is equivalent to the general contract of List#equals below.
            return Arrays.equals(this.values, ((JsonArray) otherObject).values);
        }

        // Follow the general contract of List#equals so that the equality is symmetric with other List implementations.
        return super.equals(otherObject);
    }

    /**
     * Returns the hash code value for this JSON array.
     *
     * <p>The hash code is calculated as defined in {@link java.util.List#hashCode()} to be consistent with {@link #equals(Object)}.
     *
     * @return the hash code value for this JSON array
     *
     * @see java.util.List#hashCode()
     *
     * @since 0.10.42
     */
    @Override
    public int hashCode() {
        int hash = 1;
        for (int i = 0; i < this.values.length; i++) {
            final JsonValue value = this.values[i];
            hash = 31 * hash + value.hashCode();
        }
        return hash;
    }

    private static final JsonArray EMPTY = new JsonArray(new JsonValue[0]);

    private final JsonValue[] values;

    private ImmutableArrayValueImpl msgpackArrayCache;
}
