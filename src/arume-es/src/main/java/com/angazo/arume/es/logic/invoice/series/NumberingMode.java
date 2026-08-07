package com.angazo.arume.es.logic.invoice.series;

public enum NumberingMode {
    CONTINUE((short) 1),
    RESET_EACH_FISCAL_YEAR((short) 2);

    private final short code;

    NumberingMode(short code) {
        this.code = code;
    }

    public short code() {
        return code;
    }

    public static NumberingMode fromCode(short code) {
        for (var mode : values()) {
            if (mode.code == code) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown numbering mode code: " + code);
    }
}
