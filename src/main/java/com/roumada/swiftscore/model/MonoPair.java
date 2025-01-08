package com.roumada.swiftscore.model;

import lombok.Getter;
import lombok.ToString;

/**
 * An implementation of a single-type pair.
 *
 * @param <T> preferred pair type
 */
@Getter
@ToString
public class MonoPair<T> {

    private final T left;
    private final T right;

    private MonoPair(T left, T right) {
        this.left = left;
        this.right = right;
    }

    public static <T> MonoPair<T> of(T a, T b) {
        return new MonoPair<>(a, b);
    }

}
