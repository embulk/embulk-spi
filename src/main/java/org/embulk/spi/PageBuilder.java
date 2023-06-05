/*
 * Copyright 2014 The Embulk project
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

package org.embulk.spi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import org.embulk.spi.json.JsonValue;
import org.msgpack.value.Value;

/**
 * Builds {@link Page}s from {@link Schema} and data records with pushing the built {@link Page}s to {@link PageOutput}.
 *
 * @since 0.4.0
 */
public class PageBuilder implements AutoCloseable {
    PageBuilder() {
        this.delegate = null;
    }

    private PageBuilder(final PageBuilder delegate) {
        this.delegate = delegate;
    }

    /**
     * Constructs a {@link PageBuilder} instance.
     *
     * @deprecated The constructor is deprecated although Embulk v0.9-compatible plugins still have to use this.
     *     See <a href="https://github.com/embulk/embulk/issues/1321">GitHub Issue #1321: Deprecate PageBuilder's constructor</a>
     *     for the details.
     *
     * @since 0.4.0
     */
    @Deprecated  // https://github.com/embulk/embulk/issues/1321
    public PageBuilder(BufferAllocator allocator, Schema schema, PageOutput output) {
        this(createImplInstance(allocator, schema, output));
    }

    /**
     * @since 0.4.0
     */
    public Schema getSchema() {
        return this.delegate.getSchema();
    }

    /**
     * @since 0.4.0
     */
    public void setNull(Column column) {
        this.delegate.setNull(column);
    }

    /**
     * @since 0.4.0
     */
    public void setNull(int columnIndex) {
        this.delegate.setNull(columnIndex);
    }

    /**
     * @since 0.4.0
     */
    public void setBoolean(Column column, boolean value) {
        this.delegate.setBoolean(column, value);
    }

    /**
     * @since 0.4.0
     */
    public void setBoolean(int columnIndex, boolean value) {
        this.delegate.setBoolean(columnIndex, value);
    }

    /**
     * @since 0.4.0
     */
    public void setLong(Column column, long value) {
        this.delegate.setLong(column, value);
    }

    /**
     * @since 0.4.0
     */
    public void setLong(int columnIndex, long value) {
        this.delegate.setLong(columnIndex, value);
    }

    /**
     * @since 0.4.0
     */
    public void setDouble(Column column, double value) {
        this.delegate.setDouble(column, value);
    }

    /**
     * @since 0.4.0
     */
    public void setDouble(int columnIndex, double value) {
        this.delegate.setDouble(columnIndex, value);
    }

    /**
     * @since 0.4.0
     */
    public void setString(Column column, String value) {
        this.delegate.setString(column, value);
    }

    /**
     * @since 0.4.0
     */
    public void setString(int columnIndex, String value) {
        this.delegate.setString(columnIndex, value);
    }

    /**
     * Sets a JSON value at the specified column in the {@code msgpack-java} representation.
     *
     * @param column  the column to set the JSON value
     * @param value  the JSON value in the {@code msgpack-java} representation
     * @deprecated Use {@link #setJson(org.embulk.spi.Column, org.embulk.spi.json.JsonValue)} instead.
     *
     * @since 0.8.0
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public void setJson(Column column, Value value) {
        this.delegate.setJson(column, value);
    }

    /**
     * Sets a JSON value at the specified column.
     *
     * @param column  the column to set the JSON value
     * @param value  the JSON value
     *
     * @since 0.10.42
     */
    public void setJson(final Column column, final JsonValue value) {
        this.delegate.setJson(column, value);
    }

    /**
     * Sets a JSON value at the specified column in the {@code msgpack-java} representation.
     *
     * @param columnIndex  the index of the column to set the JSON value
     * @param value  the JSON value in the {@code msgpack-java} representation
     * @deprecated Use {@link #setJson(int, org.embulk.spi.json.JsonValue)} instead.
     *
     * @since 0.8.0
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public void setJson(int columnIndex, Value value) {
        this.delegate.setJson(columnIndex, value);
    }

    /**
     * Sets a JSON value at the specified column.
     *
     * @param columnIndex  the index of the column to set the JSON value
     * @param value  the JSON value
     *
     * @since 0.10.42
     */
    public void setJson(final int columnIndex, final JsonValue value) {
        this.delegate.setJson(columnIndex, value);
    }

    /**
     * @since 0.4.0
     */
    @Deprecated
    @SuppressWarnings("deprecation")  // https://github.com/embulk/embulk/issues/1292
    public void setTimestamp(Column column, org.embulk.spi.time.Timestamp value) {
        this.delegate.setTimestamp(column, value);
    }

    /**
     * @since 0.10.13
     */
    public void setTimestamp(final Column column, final Instant value) {
        this.delegate.setTimestamp(column, value);
    }

    /**
     * @since 0.4.0
     */
    @Deprecated
    @SuppressWarnings("deprecation")  // https://github.com/embulk/embulk/issues/1292
    public void setTimestamp(int columnIndex, org.embulk.spi.time.Timestamp value) {
        this.delegate.setTimestamp(columnIndex, value);
    }

    /**
     * @since 0.10.13
     */
    public void setTimestamp(final int columnIndex, final Instant value) {
        this.delegate.setTimestamp(columnIndex, value);
    }

    /**
     * @since 0.4.0
     */
    public void addRecord() {
        this.delegate.addRecord();
    }

    /**
     * @since 0.4.0
     */
    public void flush() {
        this.delegate.flush();
    }

    /**
     * @since 0.4.0
     */
    public void finish() {
        this.delegate.finish();
    }

    /**
     * @since 0.4.0
     */
    @Override
    public void close() {
        this.delegate.close();
    }

    private static PageBuilder createImplInstance(final BufferAllocator allocator, final Schema schema, final PageOutput output) {
        try {
            return Holder.CONSTRUCTOR.newInstance(allocator, schema, output);
        } catch (final IllegalAccessException | IllegalArgumentException | InstantiationException ex) {
            throw new LinkageError("[FATAL] org.embulk.spi.PageBuilderImpl is invalid.", ex);
        } catch (final InvocationTargetException ex) {
            throwCheckedForcibly(ex.getTargetException());
            return null;  // Should never reach.
        }
    }

    private static class Holder {  // Initialization-on-demand holder idiom.
        private static final Class<PageBuilder> IMPL_CLASS;
        private static final Constructor<PageBuilder> CONSTRUCTOR;

        static {
            try {
                IMPL_CLASS = loadPageBuilderImpl();
            } catch (final ClassNotFoundException ex) {
                throw new LinkageError("[FATAL] org.embulk.spi.PageBuilderImpl is not found.", ex);
            }

            try {
                CONSTRUCTOR = IMPL_CLASS.getConstructor(BufferAllocator.class, Schema.class, PageOutput.class);
            } catch (final NoSuchMethodException ex) {
                throw new LinkageError("[FATAL] org.embulk.spi.PageBuilderImpl does not have an expected constructor.", ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<PageBuilder> loadPageBuilderImpl() throws ClassNotFoundException {
        return (Class<PageBuilder>) CLASS_LOADER.loadClass("org.embulk.spi.PageBuilderImpl");
    }

    private static void throwCheckedForcibly(final Throwable ex) {
        PageBuilder.<RuntimeException>throwCheckedForciblyInternal(ex);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwCheckedForciblyInternal(final Throwable ex) throws E {
        throw (E) ex;
    }

    private static final ClassLoader CLASS_LOADER = PageBuilder.class.getClassLoader();

    private final PageBuilder delegate;
}
