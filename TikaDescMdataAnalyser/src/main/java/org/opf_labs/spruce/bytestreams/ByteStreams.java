/**
 * 
 */
package org.opf_labs.spruce.bytestreams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.opf_labs.spruce.bytestreams.ByteStreamInstance.ByteStreamStatus;

/**
 * TODO: Beter JavaDoc for ByteStreams.<p/>
 * Static factory / utility class for creation of ByteStreamId instances.
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a> <a
 *         href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 20 Jul 2012:02:52:57
 */

public final class ByteStreams {
	/** The length of a valid hex encoded MD5 digest string */
	public static final int HEX_MD5_LENGTH = 32;
	/** The length of a valid hex encoded SHA256 digest string */
	public static final int HEX_SHA256_LENGTH = 64;
	static final String HEX_REGEX_ROOT = "^\\s*([0-9a-fA-F]";
	/** RegEx for an MD5 digest string */
	public static final String HEX_MD5_REGEX = HEX_REGEX_ROOT + "{"
			+ HEX_MD5_LENGTH + "})\\z";
	/** RegEx for a SHA256 digest string */
	public static final String HEX_SHA256_REGEX = HEX_REGEX_ROOT + "{"
			+ HEX_SHA256_LENGTH + "})\\z";
	/** Hex MD5 for a null stream */
	public static final String NULL_MD5 = "d41d8cd98f00b204e9800998ecf8427e";
	/** Hex SHA256 for a null stream */
	public static final String NULL_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
	static final NullPointerException DEFAULT_CAUSE = new NullPointerException(
			"[ByteStreamInstance] is OK.");

	// Buffer size for reading streams
	private static final int BUFFER_SIZE = (32 * 1024);
	// java.security.digest name for the MD5 algorithm
	private static final String MD5_NAME = "MD5";
	// java.security.digest name for the SHA256 algorithm
	private static final String SHA256_NAME = "SHA-256";
	// regex pattern for hex string
	private static final Pattern HEX_MD5_PATTERN = Pattern
			.compile(HEX_MD5_REGEX);
	// regex pattern for hex string
	private static final Pattern HEX_SHA256_PATTERN = Pattern
			.compile(HEX_SHA256_REGEX);

	/** Byte Stream object for null stream */
	public static final ByteStreamId NULL_STREAM = ByteStreams.fromValues(
			0L, NULL_SHA256, NULL_MD5);

	// OK set up the streams for digest calculation, first the digest algs
	private static final MessageDigest MD5;
	private static final MessageDigest SHA256;
	static {
		String tryName = MD5_NAME;
		try {
			// Try for MD5 alg
			MD5 = MessageDigest.getInstance(MD5_NAME);
			tryName = SHA256_NAME;
			// Then for a SHA 256 one
			SHA256 = MessageDigest.getInstance(SHA256_NAME);
		} catch (NoSuchAlgorithmException excep) {
			// If this happens the Java Digest algorithms aren't present, a
			// faulty Java install??
			throw new IllegalStateException(
					"No digest algorithm implementation for " + tryName
							+ ", check you Java installation.");
		}
	}

	private ByteStreams() {
		/** Disable default constructor, this should never happen */
		throw new AssertionError("[ByteStreams] In default constructor.");
	}

	/**
	 * Create a ByteStream instance from the passed values, just a straight arg
	 * check and constructor call.
	 * 
	 * @param length
	 *            the length of the byte stream in bytes
	 * @param sha256
	 *            a hex-encoded sha256 value for the byte stream
	 * @param md5
	 *            a hex-encoded md5 value for the byte stream
	 * @return a new ByteStream instance populated from the passed values
	 */
	public static final ByteStreamId fromValues(long length,
			String sha256, String md5) {
		return ByteStreamDetailsImpl.fromValues(length, sha256, md5);
	}

	/**
	 * Factory method that creates a new ByteStream instance from an
	 * InputStream. Note that the caller is responsible for closing the passed
	 * stream.
	 * 
	 * @param inStream
	 *            a java.io.InputStream from which to create the
	 *            ByteStreamId, must be closed by the caller
	 * @return a new ByteStreamId instance created from the input Stream
	 * @throws IOException
	 *             when the InputStream cannot be read
	 */
	public static final ByteStreamId fromInputStream(InputStream inStream)
			throws IOException {
		if (inStream == null)
			throw new IllegalArgumentException("inStream == null");
		MD5.reset();
		SHA256.reset();
		// Create input streams for digest calculation
		DigestInputStream MD5Stream = new DigestInputStream(inStream, MD5);
		DigestInputStream SHA256Stream = new DigestInputStream(MD5Stream,
				SHA256);
		// Wrap them all in a buffered stream for efficiency
		BufferedInputStream bis = new BufferedInputStream(SHA256Stream);
		byte[] buff = new byte[BUFFER_SIZE];
		long totalBytes = 0L;
		int bytesRead = 0;
		// Read the entire stream while calculating the length
		while ((bytesRead = bis.read(buff, 0, BUFFER_SIZE)) > -1) {
			totalBytes += bytesRead;
		}
		// Return the new instance from the calulated details
		return ByteStreamDetailsImpl.fromValues(totalBytes,
				Hex.encodeHexString(SHA256.digest()),
				Hex.encodeHexString(MD5.digest()));
	}

	/**
	 * Factory method to create a byte sequence from the contents of a file.
	 * 
	 * @param file
	 *            a java.io.File from which to create the ByteStreamId
	 * @return a new ByteStreamId instance created from the file
	 * @throws FileNotFoundException
	 *             when the file cannot be found
	 * @throws IOException
	 *             when the InputStream opened from the file cannot be read
	 */
	public static final ByteStreamId fromFile(File file)
			throws FileNotFoundException, IOException {
		if (file == null)
			throw new IllegalArgumentException("file == null");
		if (file.isDirectory())
			throw new IllegalArgumentException("file.isDirectory() == true");
		InputStream inStream = new FileInputStream(file);
		ByteStreamId bs = fromInputStream(inStream);
		try {
			inStream.close();
		} catch (IOException excep) {
			// Do nothing here, it just won't close
		}
		return bs;
	}

	/**
	 * @param status the instance status
	 * @param byteStream the byte stream details
	 * @param cause the exception cause of any problems
	 * @return a new, OK ByteStreamInstance
	 */
	public static final ByteStreamInstance instanceFromValues(ByteStreamStatus status, ByteStreamId byteStream, Exception cause) {
		if (status == null) throw new IllegalArgumentException("status == null");
		if (byteStream == null) throw new IllegalArgumentException("byteStream == null");
		if (cause == null) throw new IllegalArgumentException("cause == null");
		return ByteStreamInstanceImpl.fromValues(status, byteStream, cause);
	}
	
	/**
	 * @param byteStream the byte stream details
	 * @return a new, OK ByteStreamInstance
	 */
	public static final ByteStreamInstance getOkInstance(ByteStreamId byteStream) {
		if (byteStream == null) throw new IllegalArgumentException("byteStream == null");
		return ByteStreamInstanceImpl.fromValues(ByteStreamStatus.OK, byteStream, DEFAULT_CAUSE);
	}
	
	/**
	 * @param byteStream the byte stream details
	 * @param cause the damage cause
	 * @return a new, OK ByteStreamInstance
	 */
	public static final ByteStreamInstance getDamagedInstance(ByteStreamId byteStream, Exception cause) {
		if (byteStream == null) throw new IllegalArgumentException("byteStream == null");
		if (cause == null) throw new IllegalArgumentException("cause == null");
		return ByteStreamInstanceImpl.fromValues(ByteStreamStatus.DAMAGED, byteStream, cause);
	}
	
	/**
	 * @param cause the damage cause
	 * @return a new, OK ByteStreamInstance
	 */
	public static final ByteStreamInstance getDamagedInstance(Exception cause) {
		if (cause == null) throw new IllegalArgumentException("cause == null");
		return ByteStreamInstanceImpl.fromValues(ByteStreamStatus.DAMAGED, NULL_STREAM, cause);
	}
	
	/**
	 * @param byteStream the byte stream details
	 * @param cause the damage cause
	 * @return a new, OK ByteStreamInstance
	 */
	public static final ByteStreamInstance getLostInstance(ByteStreamId byteStream, Exception cause) {
		if (byteStream == null) throw new IllegalArgumentException("byteStream == null");
		if (cause == null) throw new IllegalArgumentException("cause == null");
		return ByteStreamInstanceImpl.fromValues(ByteStreamStatus.LOST, byteStream, cause);
	}
	
	/**
	 * @param cause the damage cause
	 * @return a new, OK ByteStreamInstance
	 */
	public static final ByteStreamInstance getLostInstance(Exception cause) {
		if (cause == null) throw new IllegalArgumentException("cause == null");
		return ByteStreamInstanceImpl.fromValues(ByteStreamStatus.LOST, NULL_STREAM, cause);
	}

	/**
	 * @param cause the damage cause
	 * @return a new, OK ByteStreamInstance
	 */
	public static final ByteStreamInstance getUncheckedInstance(Exception cause) {
		if (cause == null) throw new IllegalArgumentException("cause == null");
		return ByteStreamInstanceImpl.fromValues(ByteStreamStatus.UNCHECKED, NULL_STREAM, cause);
	}

	/**
	 * @param toTest
	 *            java.lang.String to test to see if it's a hex md5 string
	 * @return true if the string is a hex md5 string
	 */
	public static final boolean isHexMD5(final String toTest) {
		if (toTest == null) throw new IllegalArgumentException("toTest == null");
		Matcher matcher = HEX_MD5_PATTERN.matcher(toTest);
		return matcher.find();
	}

	/**
	 * @param toTest
	 *            java.lang.String to test to see if it's a hex sha256 string
	 * @return true if the string is a hex sha256 string
	 */
	public static final boolean isHexSHA256(final String toTest) {
		if (toTest == null) throw new IllegalArgumentException("toTest == null");
		Matcher matcher = HEX_SHA256_PATTERN.matcher(toTest);
		return matcher.find();
	}

	/**
	 * @param bytes the numer of bytes
	 * @param si 
	 * @return a human readable byte count
	 */
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}}
