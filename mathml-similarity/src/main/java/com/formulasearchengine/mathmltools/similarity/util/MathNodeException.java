package com.formulasearchengine.mathmltools.similarity.util;

import com.formulasearchengine.mathmltools.similarity.node.MathNode;

/**
 * Just an explicit exception thrown during the {@link MathNode} generation.
 *
 * @author Vincent Stange
 */
public class MathNodeException extends Exception {

    public MathNodeException(String message, Throwable throwable) {
        super(message, throwable);
    }

}