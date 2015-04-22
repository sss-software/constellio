/*Constellio Enterprise Information Management

Copyright (c) 2015 "Constellio inc."

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.constellio.data.io.services.facades;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import com.constellio.data.io.streamFactories.CloseableStreamFactory;
import com.constellio.data.io.streamFactories.StreamFactory;
import com.constellio.data.io.streamFactories.impl.CopyInputStreamFactory;
import com.constellio.data.io.streamFactories.impl.CopyInputStreamFactoryRuntimeException;
import com.constellio.data.io.streamFactories.services.StreamFactoriesServices;
import com.constellio.data.io.streamFactories.services.one.StreamOperation;
import com.constellio.data.io.streamFactories.services.one.StreamOperationReturningValue;
import com.constellio.data.io.streamFactories.services.one.StreamOperationReturningValueOrThrowingException;
import com.constellio.data.io.streamFactories.services.one.StreamOperationThrowingException;
import com.constellio.data.io.streamFactories.services.two.TwoStreamsOperation;
import com.constellio.data.io.streamFactories.services.two.TwoStreamsOperationReturningValue;
import com.constellio.data.io.streamFactories.services.two.TwoStreamsOperationReturningValueOrThrowingException;
import com.constellio.data.io.streamFactories.services.two.TwoStreamsOperationThrowingException;
import com.constellio.data.io.streams.factories.StreamsServices;
import com.constellio.data.utils.ImpossibleRuntimeException;
import com.constellio.data.utils.Octets;

public class IOServices {

	private final File tempFolder;
	private final FileService fileServices;
	private final StreamsServices streamsServices;
	private final StreamFactoriesServices streamFactoriesServices;

	public IOServices(File tempFolder) {
		super();
		this.tempFolder = tempFolder;
		this.fileServices = new FileService(tempFolder);
		this.streamsServices = new StreamsServices();
		this.streamFactoriesServices = new StreamFactoriesServices(streamsServices);
	}

	public byte[] readBytes(InputStream inputStream)
			throws IOException {
		return streamsServices.readBytes(inputStream);
	}

	public void copyAndClose(InputStream inputStream, OutputStream outputStream)
			throws IOException {

		try {
			copy(inputStream, outputStream);

		} finally {
			closeQuietly(inputStream);
			closeQuietly(outputStream);
		}
	}

	public void copy(InputStream inputStream, OutputStream outputStream)
			throws IOException {
		streamsServices.copy(inputStream, outputStream);
	}

	public void copyLarge(InputStream inputStream, OutputStream outputStream)
			throws IOException {
		streamsServices.copyLarge(inputStream, outputStream);
	}

	public void deleteQuietly(File file) {
		fileServices.deleteQuietly(file);
	}

	public void closeQuietly(Closeable closeable) {
		streamsServices.closeQuietly(closeable);
	}

	public InputStream newByteInputStream(byte[] theContentBytes, String name) {
		return streamsServices.newByteArrayInputStream(theContentBytes, uniqueIdWith(name));
	}

	public InputStream newBufferedInputStream(InputStream inputStream, String name) {
		return streamsServices.newBufferedInputStream(inputStream, uniqueIdWith(name));
	}

	public BufferedReader newBufferedReader(Reader reader, String name) {
		return streamsServices.newBufferedReader(reader, uniqueIdWith(name));
	}

	public BufferedReader newBufferedFileReader(File file, String name) {
		return streamsServices.newFileReader(file, uniqueIdWith(name));
	}

	public BufferedReader newFileReader(File file, String name) {
		return streamsServices.newFileReader(file, uniqueIdWith(name));
	}

	public StreamFactory<Reader> newFileReaderFactory(File file)
			throws FileNotFoundException {
		return streamsServices.newFileReaderFactory(file);
	}

	public InputStream newFileInputStream(File file, String name)
			throws FileNotFoundException {
		return streamsServices.newFileInputStream(file, uniqueIdWith(name));
	}

	public InputStream newBufferedByteArrayInputStream(byte[] byteArray, String name) {
		return streamsServices.newBufferedByteArrayInputStream(byteArray, uniqueIdWith(name));
	}

	public InputStream newBufferedFileInputStream(File file, String name)
			throws FileNotFoundException {
		return streamsServices.newBufferedFileInputStream(file, uniqueIdWith(name));
	}

	public BufferedInputStream newBufferedFileInputStreamWithoutExpectableFileNotFoundException(File file, String name) {
		try {
			return streamsServices.newBufferedFileInputStream(file, uniqueIdWith(name));
		} catch (FileNotFoundException e) {
			throw new ImpossibleRuntimeException(e);
		}
	}

	public ByteArrayInputStream newByteArrayInputStream(byte[] byteArray, String name) {
		return streamsServices.newByteArrayInputStream(byteArray, uniqueIdWith(name));
	}

	public OutputStream newBufferedOutputStream(OutputStream outputStream, String name) {
		return streamsServices.newBufferedOutputStream(outputStream, uniqueIdWith(name));
	}

	public OutputStream newBufferedFileOutputStream(File file, String name)
			throws FileNotFoundException {
		return streamsServices.newBufferedFileOutputStream(file, uniqueIdWith(name));
	}

	public OutputStream newBufferedFileOutputStreamWithoutExpectableFileNotFoundException(File file, String name) {
		try {
			return streamsServices.newBufferedFileOutputStream(file, uniqueIdWith(name));
		} catch (FileNotFoundException e) {
			throw new ImpossibleRuntimeException(e);
		}
	}

	public OutputStream newFileOutputStream(File file, String name)
			throws FileNotFoundException {
		return streamsServices.newFileOutputStream(file, uniqueIdWith(name));
	}

	public ByteArrayOutputStream newByteArrayOutputStream(String name) {
		return streamsServices.newByteArrayOutputStream(uniqueIdWith(name));
	}

	public StreamFactory<InputStream> newByteArrayStreamFactory(final byte[] bytes, String name) {
		return streamsServices.newByteArrayStreamFactory(bytes, uniqueIdWith(name));
	}

	public <F extends Closeable> void execute(StreamOperation<F> operation, StreamFactory<F> closeableStreamFactory)
			throws IOException {
		streamFactoriesServices.execute(operation, closeableStreamFactory);
	}

	public <F extends Closeable, R> R execute(StreamOperationReturningValue<F, R> operation,
			StreamFactory<F> closeableStreamFactory)
			throws IOException {
		return streamFactoriesServices.execute(operation, closeableStreamFactory);
	}

	public <F extends Closeable, R, E extends Exception> R execute(
			StreamOperationReturningValueOrThrowingException<F, R, E> operation, StreamFactory<F> closeableStreamFactory)
			throws E, IOException {
		return streamFactoriesServices.execute(operation, closeableStreamFactory);
	}

	public <F extends Closeable, E extends Exception> void execute(StreamOperationThrowingException<F, E> operation,
			StreamFactory<F> closeableStreamFactory)
			throws E, IOException {
		streamFactoriesServices.execute(operation, closeableStreamFactory);
	}

	public <F extends Closeable, S extends Closeable> void execute(TwoStreamsOperation<F, S> operation,
			StreamFactory<F> firstCloseableStreamFactory, StreamFactory<S> secondCloseableStreamFactory)
			throws IOException {
		streamFactoriesServices.execute(operation, firstCloseableStreamFactory, secondCloseableStreamFactory);
	}

	public <F extends Closeable, S extends Closeable, R> R execute(TwoStreamsOperationReturningValue<F, S, R> operation,
			StreamFactory<F> firstCloseableStreamFactory, StreamFactory<S> secondCloseableStreamFactory)
			throws IOException {
		return streamFactoriesServices.execute(operation, firstCloseableStreamFactory, secondCloseableStreamFactory);
	}

	public <F extends Closeable, S extends Closeable, R, E extends Exception> R execute(
			TwoStreamsOperationReturningValueOrThrowingException<F, S, R, E> operation,
			StreamFactory<F> firstCloseableStreamFactory, StreamFactory<S> secondCloseableStreamFactory)
			throws E, IOException {
		return streamFactoriesServices.execute(operation, firstCloseableStreamFactory, secondCloseableStreamFactory);
	}

	public <F extends Closeable, S extends Closeable, E extends Exception> void execute(
			TwoStreamsOperationThrowingException<F, S, E> operation, StreamFactory<F> firstCloseableStreamFactory,
			StreamFactory<S> secondCloseableStreamFactory)
			throws E, IOException {
		streamFactoriesServices.execute(operation, firstCloseableStreamFactory, secondCloseableStreamFactory);
	}

	public void closeQuietly(CloseableStreamFactory<InputStream> inputStreamFactory) {
		streamFactoriesServices.closeQuietly(inputStreamFactory);
	}

	public CloseableStreamFactory<InputStream> copyToReusableStreamFactory(InputStream inputStream)
			throws CopyInputStreamFactoryRuntimeException {
		CopyInputStreamFactory copyInputStreamFactory = new CopyInputStreamFactory(this, Octets.megaoctets(10));
		try {
			copyInputStreamFactory.saveInputStreamContent(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return copyInputStreamFactory;
	}

	public void copyDirectory(File srcDir, File destDir)
			throws IOException {
		fileServices.copyDirectory(srcDir, destDir);
	}

	public void copyDirectoryWithoutExpectableIOException(File srcDir, File destDir) {
		fileServices.copyDirectoryWithoutExpectableIOException(srcDir, destDir);
	}

	public void copyFile(File srcFile, File destFile)
			throws IOException {
		fileServices.copyFile(srcFile, destFile);
	}

	public void copyFileWithoutExpectableIOException(File srcFile, File destFile) {
		fileServices.copyFileWithoutExpectableIOException(srcFile, destFile);
	}

	public String readFileToString(File file)
			throws IOException {
		return fileServices.readFileToString(file);
	}

	public String readFileToStringWithoutExpectableIOException(File file) {
		return fileServices.readFileToStringWithoutExpectableIOException(file);
	}

	public List<String> readFileToLinesWithoutExpectableIOException(File file) {
		return fileServices.readFileToLinesWithoutExpectableIOException(file);
	}

	public void replaceFileContent(File file, String data)
			throws IOException {
		fileServices.replaceFileContent(file, data);
	}

	public void appendFileContent(File file, String data)
			throws IOException {
		fileServices.appendFileContent(file, data);
	}

	public void ensureWritePermissions(File file)
			throws IOException {
		fileServices.ensureWritePermissions(file);
	}

	public Collection<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
		return fileServices.listFiles(directory, fileFilter, dirFilter);
	}

	public Collection<File> listRecursiveFiles(File directory) {
		return fileServices.listRecursiveFiles(directory);
	}

	public Collection<File> listRecursiveFiles(File directory, IOFileFilter fileFilter) {
		return fileServices.listRecursiveFiles(directory, fileFilter);
	}

	public Collection<File> listRecursiveFilesWithName(File directory, String name) {
		return fileServices.listRecursiveFilesWithName(directory, name);
	}

	public void deleteDirectory(File directory)
			throws IOException {
		fileServices.deleteDirectory(directory);
	}

	public void deleteDirectoryWithoutExpectableIOException(File directory) {
		fileServices.deleteDirectoryWithoutExpectableIOException(directory);
	}

	public File newTemporaryFolder(String resourceName) {
		return fileServices.newTemporaryFolder(resourceName);
	}

	public File newTemporaryFolderWithoutExpectableIOException(String resourceName) {
		return fileServices.newTemporaryFolder(resourceName);
	}

	public String readStreamToStringWithoutExpectableIOException(InputStream inputStream) {
		return fileServices.readStreamToStringWithoutExpectableIOException(inputStream);
	}

	public String readStreamToString(InputStream inputStream)
			throws IOException {
		return fileServices.readStreamToString(inputStream);
	}

	public List<String> readStreamToLines(InputStream inputStream)
			throws IOException {
		return fileServices.readStreamToLines(inputStream);
	}

	public StreamFactory<InputStream> newInputStreamFactory(File file, String name) {
		return streamsServices.newInputStreamFactory(file, uniqueIdWith(name));
	}

	public StreamFactory<OutputStream> newOutputStreamFactory(File file, String name) {
		return streamsServices.newOutputStreamFactory(file, uniqueIdWith(name));
	}

	public StreamFactory<InputStream> newInputStreamFactory(String string) {
		return streamsServices.newInputStreamFactory(string);
	}

	private String uniqueIdWith(String name) {
		return name + "-" + UUID.randomUUID().toString();
	}

	public File newTemporaryFile(String resourceName) {
		return fileServices.newTemporaryFile(resourceName);
	}

	public void touch(File file) {
		try {
			FileUtils.touch(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
