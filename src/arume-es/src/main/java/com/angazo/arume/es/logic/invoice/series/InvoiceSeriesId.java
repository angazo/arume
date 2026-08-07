package com.angazo.arume.es.logic.invoice.series;

public record InvoiceSeriesId(long value) {

    public InvoiceSeriesId {
        if (value < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
    }

    public static InvoiceSeriesId unassigned() {
        return new InvoiceSeriesId(0);
    }

    public boolean isAssigned() {
        return value > 0;
    }
}
