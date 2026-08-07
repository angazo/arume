package com.angazo.arume.core.module;

import java.util.Objects;

public record SchemaVersion(int major, int minor, int patch, int sequence) implements Comparable<SchemaVersion> {

    public SchemaVersion {
        if (major < 0 || minor < 0 || patch < 0 || sequence < 0) {
            throw new IllegalArgumentException("Schema version components must not be negative");
        }
    }

    public static SchemaVersion parse(String value) {
        Objects.requireNonNull(value, "value");
        var parts = value.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Schema version must have four numeric components: " + value);
        }
        try {
            return new SchemaVersion(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3])
            );
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Schema version must contain only numbers: " + value, exception);
        }
    }

    @Override
    public int compareTo(SchemaVersion other) {
        var result = Integer.compare(major, other.major);
        if (result != 0) return result;
        result = Integer.compare(minor, other.minor);
        if (result != 0) return result;
        result = Integer.compare(patch, other.patch);
        if (result != 0) return result;
        return Integer.compare(sequence, other.sequence);
    }

    @Override
    public String toString() {
        return "%d.%d.%d.%d".formatted(major, minor, patch, sequence);
    }
}
