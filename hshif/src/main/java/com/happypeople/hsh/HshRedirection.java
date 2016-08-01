/**
 */
package com.happypeople.hsh;

import java.io.File;

/**
 * Objects of this class encapsulate the (meta) information given in one
 * redirection, ie ">outfile" or "0<&3". Immutable.
 * TODO refactor this class to be an interface
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public class HshRedirection {
    /**
     * Type of the target of this redirection.
     */
    private final TargetType targettype;

    /**
     * If targettype==FILE, this is the files name.
     */
    private final File targetfile;

    /**
     * If targettype==ANOTHER_FD, this is the number of it.
     */
    private final Integer targetfd;

    /**
     * This is the number of the redirected FD.
     */
    private final int redirectedfd;

    /**
     * This is the operation which should be executed on the redirection.
     */
    private final OperationType opertype;

    /**
     * Internal initalization.
     * @param target The target specifier
     * @param redirectedfd The redirected FD
     */
    private HshRedirection(
        final Target target,
        final int redirectedfd) {
        this.targettype = target.targettype;
        this.redirectedfd = this.checkRedirectfd(redirectedfd);
        this.opertype = target.opertype;
        this.targetfile = target.targetfile;
        this.targetfd = target.targetfd;
    }

    /**
     * Creates a redirection to read from a targetfile or write to a targetfile
     * or append to a targetfile.
     * @param redirectedfd The FD which should be redirected
     * @param opertype The operation of the FD which should be redirected
     * @param targetfile The files name
     */
    public HshRedirection(final int redirectedfd, final OperationType opertype,
        final File targetfile) {
        this(createTarget(opertype, targetfile), redirectedfd);
    }

    /**
     * Creates a redirection to read from or write to another FD.
     * @param redirectedfd The redirected field desctiptor
     * @param opertype The operation type
     * @param otherio The target of the operation
     */
    public HshRedirection(final int redirectedfd, final OperationType opertype,
        final Integer otherio) {
        this(createTarget(opertype, otherio), redirectedfd);
    }

    /**
     * If target type is FILE this method returns the File, else null.
     * @return The target File to read from, or write or append to
     */
    public final File getTargetFile() {
        return this.targetfile;
    }

    /**
     * If target type is ANOTHER_FD this method returns the FD of the target. If
     * target type is not ANOTHER_FD, this method returns most likely null
     * @return The target FD
     */
    public final Integer getTargetFd() {
        return this.targetfd;
    }

    /**
     * Every HshRedirection redirects exactly one FD.
     * @return The FD of this HshRedirection
     */
    public final int getRedirectedFd() {
        return this.redirectedfd;
    }

    /**
     * Query the target type, file or stream.
     * @return The target type
     */
    public final TargetType getTargetType() {
        return this.targettype;
    }

    /**
     * Query the operation type in, out or out/append.
     * @return The operation type
     */
    public final OperationType getOperationType() {
        return this.opertype;
    }

    @Override
    public final String toString() {
        return new StringBuilder("targettype: ").append(this.targettype)
            .append(" operationtype: ").append(this.opertype)
            .append(" redirectedfd: ").append(this.redirectedfd)
            .append(" targetfd: ").append(this.targetfd)
            .append(" file: ").append(this.targetfile)
            .toString();
    }

    /**
     * Creates a Target of type FILE.
     * @param opertype The operation type
     * @param targetfile The target file
     * @return A newly created Target object
     */
    private static Target createTarget(final OperationType opertype,
        final File targetfile) {
        final Target target = new Target();
        target.opertype = opertype;
        target.targetfile = targetfile;
        target.targettype = TargetType.FILE;
        if (targetfile == null) {
            throw new IllegalArgumentException(
                "targetfile must not be null with this constructor"
            );
        }
        return target;
    }

    /**
     * Creates a Target of type ANOTHER_FD.
     * @param opertype The operation type
     * @param otherio The field descriptor
     * @return A newly created Target object
     */
    private static Target createTarget(final OperationType opertype,
        final Integer otherio) {
        final Target target = new Target();
        target.opertype = opertype;
        target.targetfd = otherio;
        target.targettype = TargetType.ANOTHER_FD;
        if (opertype == OperationType.APPEND) {
            throw new IllegalArgumentException(
                "cannot APPEND to FD, use READ or WRITE or a file"
            );
        }
        if (otherio == null) {
            throw new IllegalArgumentException(
                "targetfd must not be null with this constructor"
            );
        }
        return target;
    }

    /**
     * Checks parameter redirectedfd.
     * @param redirectedfd The redirected field descriptor
     * @return And returns it
     */
    private static int checkRedirectfd(final int redirectedfd) {
        if (redirectedfd < 0) {
            throw new IllegalArgumentException("fd must not be less than 0");
        }
        return redirectedfd;
    }

    /**
     * Redirection may go to a file or to another FD, ie "2>err.txt" or "3>&1".
     */
    public enum TargetType {
        /** Redirection from or to a targetfile.
         */
        FILE,
        /** Redirection to another FD.
         */
        ANOTHER_FD,
    }

    /**
     * According to the redirection symbols "<", ">" and ">>" there are three
     * types of operations.
     */
    public enum OperationType {
        /** Type reading.
         */
        READ,
        /** Type writing.
         */
        WRITE,
        /** Type appending (no overwrite).
         */
        APPEND
    }

    /**
     * Type to be used in constructor call (to make the parameter
     * count less than 5).
     */
    private static class Target {
        /**
         * The target type.
         */
        private TargetType targettype;
        /**
         * The operation type.
         */
        private OperationType opertype;
        /**
         * The target file.
         */
        private File targetfile;
        /**
         * The target FD.
         */
        private Integer targetfd;

        /**
         * X.
         * @return The targetype
         */
        private TargetType getTargettype() {
            return this.targettype;
        }
        /**
         * X.
         * @param ttype The targettype
         */
        private void setTargettype(final TargetType ttype) {
            this.targettype = ttype;
        }
        /**
         * X.
         * @return The operation type
         */
        private OperationType getOpertype() {
            return this.opertype;
        }
        /**
         * X.
         * @param otype The operation type
         */
        private void setOpertype(final OperationType otype) {
            this.opertype = otype;
        }
        /**
         * X.
         * @return The target file
         */
        private File getTargetfile() {
            return this.targetfile;
        }
        /**
         * X.
         * @param tfile The target file
         */
        private void setTargetfile(final File tfile) {
            this.targetfile = tfile;
        }
        /**
         * X.
         * @return The target field descriptor
         */
        private Integer getTargetfd() {
            return this.targetfd;
        }
        /**
         * X.
         * @param tfd The target field descriptor
         */
        private void setTargetfd(final Integer tfd) {
            this.targetfd = tfd;
        }
    }
}
