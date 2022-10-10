package de.zwisler.cfvis.util;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Tuple<X, Y> {
    private X left;
    private Y right;

    public Tuple(X left, Y right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
