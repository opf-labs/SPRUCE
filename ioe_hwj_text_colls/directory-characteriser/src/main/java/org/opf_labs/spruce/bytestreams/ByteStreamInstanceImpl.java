/**
 * 
 */
package org.opf_labs.spruce.bytestreams;


/**
 * TODO JavaDoc for ByteStreamInstanceImpl.</p>
 * TODO Tests for ByteStreamInstanceImpl.</p>
 * TODO Implementation for ByteStreamInstanceImpl.</p>
 * 
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 * Created 13 Sep 2012:10:04:38
 */

class ByteStreamInstanceImpl implements ByteStreamInstance {
	private final ByteStreamStatus status;
	private final ByteStreamId byteStream;
	private final Exception cause;

	private ByteStreamInstanceImpl() {
		/** Disable default constructor */
		throw new AssertionError(
				"[ByteStreamInstanceImpl] default constructor should never be called.");
	}
	
	private ByteStreamInstanceImpl(ByteStreamStatus status, ByteStreamId byteStream, Exception cause) {
		if (byteStream == null)
			throw new IllegalArgumentException("byteStream == null");
		if (cause == null)
			throw new IllegalArgumentException("cause == null");
		if (status == null)
			throw new IllegalArgumentException("status == null");
		this.status = status;
		this.byteStream = byteStream;
		this.cause = cause;
		if (cause == ByteStreams.DEFAULT_CAUSE) {
			// Cause should NOT be the default cause if the status is LOST, or
			// DAMAGED
			if ((status == ByteStreamStatus.DAMAGED)
					|| (status == ByteStreamStatus.LOST))
				throw new AssertionError("(status == "
						+ ByteStreamStatus.DAMAGED + ") || (status == "
						+ ByteStreamStatus.LOST + ") but cause == "
						+ ByteStreams.DEFAULT_CAUSE);
		} else {
			// Cause should be the default cause if the status is OK, or
			// UNCHECKED
			if ((status == ByteStreamStatus.OK)
					|| (status == ByteStreamStatus.UNCHECKED))
				throw new AssertionError("(status == " + ByteStreamStatus.OK
						+ ") || (status == " + ByteStreamStatus.UNCHECKED
						+ ") but cause != " + ByteStreams.DEFAULT_CAUSE);
		}
	}
	
	static final ByteStreamInstanceImpl fromValues(ByteStreamStatus status, ByteStreamId byteStream, Exception cause) {
		return new ByteStreamInstanceImpl(status, byteStream, cause);
	}
	
	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.bytestream.ByteStreamInstance#getByteStreamStatus()
	 */
	@Override
	public final ByteStreamStatus getByteStreamStatus() {
		return this.status;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.bytestream.ByteStreamInstance#isChecked()
	 */
	@Override
	public final boolean isChecked() {
		return this.status != ByteStreamStatus.UNCHECKED;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.bytestream.ByteStreamInstance#isOK()
	 */
	@Override
	public final boolean isOK() {
		return this.status == ByteStreamStatus.OK;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.bytestream.ByteStreamInstance#getByteStreamId()
	 */
	@Override
	public final ByteStreamId getByteStreamId() {
		return this.byteStream;
	}

	/**
	 * @see uk.ac.ox.bodleian.beam.filesystem.bytestream.ByteStreamInstance#getException()
	 */
	@Override
	public final Exception getException() {
		return this.cause;
	}

}
