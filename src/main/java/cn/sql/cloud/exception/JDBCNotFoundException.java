package cn.sql.cloud.exception;

/**
 * 没有找到JDBC连接异常
 * @author TQ
 *
 */
public class JDBCNotFoundException extends SQLCloudCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public JDBCNotFoundException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public JDBCNotFoundException(String message) {
        super(message);
    }
}
