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

import java.util.Objects;
import org.msgpack.value.Value;
import org.msgpack.value.impl.ImmutableStringValueImpl;

public final class FakeJsonString implements JsonValue {
    private FakeJsonString(final String value) {
        this.value = new ImmutableStringValueImpl(value);
    }

    public static FakeJsonString of(final String value) {
        return new FakeJsonString(value);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.STRING;
    }

    @Override
    public int presumeReferenceSizeInBytes() {
        return this.value.asString().length() * 2 + 4;
    }

    public String getString() {
        return this.value.asString();
    }

    public CharSequence getChars() {
        return this.value.asString();
    }

    @Override
    public String toJson() {
        return escapeStringForJsonLiteral(this.value.asString());
    }

    @Deprecated
    public Value toMsgpack() {
        return this.value;
    }

    @Override
    public String toString() {
        return escapeStringForJsonLiteral(this.value.asString());
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }

        // Fake!
        if (otherObject instanceof JsonString) {
            return Objects.equals(this.value.asString(), ((JsonString) otherObject).getString());
        }

        // Check by `instanceof` in case against unexpected arbitrary extension of JsonValue.
        if (!(otherObject instanceof FakeJsonString)) {
            return false;
        }

        final FakeJsonString other = (FakeJsonString) otherObject;

        return Objects.equals(this.value.asString(), other.value.asString());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value.asString());
    }

    static void appendEscapedStringForJsonLiteral(final String original, final StringBuilder builder) {
        if (original == null) {
            return;
        }
        if (original.isEmpty()) {
            builder.append("\"\"");
            return;
        }

        builder.append("\"");

        final int length = original.length();
        for (int i = 0; i < length; i++) {
            final char current = original.charAt(i);
            switch (current) {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '"':
                    builder.append("\\\"");
                    break;
                case '\b':  // 0008
                    builder.append("\\b");
                    break;
                case '\f':  // 000c
                    builder.append("\\f");
                    break;
                case '\n':  // 000a
                    builder.append("\\n");
                    break;
                case '\r':  // 000d
                    builder.append("\\r");
                    break;
                case '\t':  // 0009
                    builder.append("\\t");
                    break;
                case '\0':
                case '\u0001':
                case '\u0002':
                case '\u0003':
                case '\u0004':
                case '\u0005':
                case '\u0006':
                case '\u0007':
                case '\u000b':
                case '\u000e':
                case '\u000f':
                case '\u0010':
                case '\u0011':
                case '\u0012':
                case '\u0013':
                case '\u0014':
                case '\u0015':
                case '\u0016':
                case '\u0017':
                case '\u0018':
                case '\u0019':
                case '\u001a':
                case '\u001b':
                case '\u001c':
                case '\u001d':
                case '\u001e':
                case '\u001f':
                    builder.append("\\u00");
                    final String hex = Integer.toHexString(current);
                    builder.append("00", 0, 2 - hex.length());
                    builder.append(hex);
                    break;
                default:
                    builder.append(current);
            }
        }

        builder.append("\"");
    }

    private static String escapeStringForJsonLiteral(final String original) {
        final StringBuilder builder = new StringBuilder();
        appendEscapedStringForJsonLiteral(original, builder);
        return builder.toString();
    }

    private final ImmutableStringValueImpl value;
}
