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

package org.embulk.spi.type;

/**
 * Base class of singleton type classes.
 *
 * @deprecated Plugins should not refer this class directly.
 *
 * @since 0.4.0
 */
@Deprecated
public abstract class AbstractType implements Type {
    /**
     * @since 0.4.0
     */
    protected AbstractType(final String name, final Class<?> javaType, final int fixedStorageSize) {
        this.name = name;
        this.javaType = javaType;

        this.fixedStorageSize = (byte) fixedStorageSize;
    }

    /**
     * @since 0.4.0
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * @since 0.4.0
     */
    @Deprecated
    @Override
    public Class<?> getJavaType() {
        return this.javaType;
    }

    /**
     * @since 0.4.0
     */
    @Override
    public byte getFixedStorageSize() {
        return this.fixedStorageSize;
    }

    /**
     * @since 0.4.0
     */
    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        return other.getClass().isAssignableFrom(this.getClass());
    }

    /**
     * @since 0.4.0
     */
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    /**
     * @since 0.4.0
     */
    @Override
    public String toString() {
        return this.name;
    }

    private final String name;
    private final Class<?> javaType;

    // TODO: Make it final?
    private byte fixedStorageSize;
}
