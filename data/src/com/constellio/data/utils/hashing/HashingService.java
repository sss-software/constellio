package com.constellio.data.utils.hashing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;

import com.constellio.data.io.EncodingService;
import com.constellio.data.io.streamFactories.StreamFactory;
import com.constellio.data.utils.Factory;

public class HashingService {

	private String algorithm;
	private ThreadLocal<MessageDigest> messageDigests = new ThreadLocal<>();

	public HashingService(String algorithm, EncodingService encodingService) {

		this.algorithm = algorithm;
	}

	public static HashingService forMD5(EncodingService encodingService) {
		return new HashingService("MD5", encodingService);
	}

	public static HashingService forSHA1(EncodingService encodingService) {
		return new HashingService("SHA1", encodingService);
	}

	public String getHashFromStream(StreamFactory<InputStream> streamFactory)
			throws HashingServiceException {
		InputStream inputStream = null;
		try {
			inputStream = streamFactory.create("getHashFromStream");
		} catch (IOException e) {
			throw new HashingServiceException.CannotReadContent(e);
		}

		try {
			return getHashFromStream(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	public String getHashFromStream(final InputStream stream)
			throws HashingServiceException {
		return getHashFromBytes(new Factory<byte[]>() {

			@Override
			public byte[] get() {
				try {
					return IOUtils.toByteArray(stream);

				} catch (IOException e) {
					throw new HashingServiceRuntimeException.CannotGetHashFromStream(e);

				}

			}

		});
	}

	public String getHashFromReader(final StreamFactory<Reader> readerFactory)
			throws HashingServiceException {

		Reader reader = null;
		try {
			reader = readerFactory.create("getHashFromReader");
		} catch (IOException e) {
			throw new HashingServiceException.CannotReadContent(e);
		}

		try {
			return getHashFromReader(reader);
		} finally {
			IOUtils.closeQuietly(reader);
		}

	}

	public String getHashFromReader(final Reader reader)
			throws HashingServiceException {
		return getHashFromBytes(new Factory<byte[]>() {

			@Override
			public byte[] get() {
				try {
					return IOUtils.toByteArray(reader);

				} catch (IOException e) {
					throw new HashingServiceRuntimeException.CannotGetHashFromReader(e);

				}

			}

		});
	}

	public String getHashFromFile(File file)
			throws HashingServiceException {
		throw new UnsupportedOperationException("TODO");
	}

	public String getHashFromBytes(final byte[] bytes)
			throws HashingServiceException {
		return getHashFromBytes(new Factory<byte[]>() {

			@Override
			public byte[] get() {
				return bytes;
			}

		});
	}

	public String getHashFromBytes(final Factory<byte[]> bytesFactory)
			throws HashingServiceException {

		try {
			byte[] bytes;
			try {
				bytes = bytesFactory.get();
			} catch (RuntimeException e) {
				throw new HashingServiceException.CannotReadContent(e);
			}
			return doHash(bytes);
		} catch (RuntimeException e) {
			throw new HashingServiceException.CannotHashContent(e);
		}
	}

	public String getHashFromString(String string)
			throws HashingServiceException {
		return getHashFromBytes(string.getBytes());
	}

	String doHash(byte[] bytes) {

		MessageDigest messageDigest = messageDigests.get();
		if (messageDigest == null) {

			try {
				messageDigest = MessageDigest.getInstance(algorithm);
			} catch (NoSuchAlgorithmException e) {
				throw new HashingServiceRuntimeException.NoSuchAlgorithm(algorithm, e);
			}
			messageDigests.set(messageDigest);
		}

		byte[] digestBytes = messageDigest.digest(bytes);
		return new EncodingService().encodeToBase64(digestBytes);
	}

}
