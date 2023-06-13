package com.example.abeautifulmind.model.tuple;

/**
 * This exception is thrown when search of Nash equilibrium lead to zero or multiple result.s.
 *
 * @author hovozounkou
 */
public class NashEquilibriumException extends RuntimeException {

    final private NashEquilibriumExceptionType exceptionType;

    public NashEquilibriumException(NashEquilibriumExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public NashEquilibriumExceptionType getExceptionType() {
        return exceptionType;
    }

    public enum NashEquilibriumExceptionType {
        Multiple, None
    }
}