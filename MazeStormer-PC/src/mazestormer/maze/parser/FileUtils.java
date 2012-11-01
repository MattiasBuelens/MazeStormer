package mazestormer.maze.parser;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class FileUtils {

	private FileUtils() {
	}

	/**
	 * Load the contents of the file at the given file path.
	 * 
	 * @param path
	 * 			The file path.
	 * 
	 * @effect	The file associated with the given file path
	 * 			is loaded.
	 * 			| load(new File(path))
	 * 
	 * @throws	IllegalArgumentException
	 * 			If the given path is not effective.
	 * 			| path == null
	 * @throws	IOException
	 * 			If the given file could not be read.
	 */
	public static CharBuffer load(String path) throws IllegalArgumentException, IOException {
		checkArgument(path == null, "Path must be effective.");

		return load(new File(path));
	}

	/**
	 * Load the contents of the given file.
	 * 
	 * @param file
	 * 			The file.
	 * 
	 * @return	The read file contents.
	 * 
	 * @throws	IllegalArgumentException
	 * 			If the given file is not effective.
	 * 			| file == null
	 * @throws	IOException
	 * 			If the given file could not be read.
	 */
	public static CharBuffer load(File file) throws IllegalArgumentException, IOException {
		checkArgument(file == null, "File must be effective.");

		// Read the entire file contents into a buffer
		return readFile(file);
	}

	/**
	 * Read the file contents of the given file
	 * using the default character set.
	 * 
	 * @param file
	 * 			The file to read.
	 * 
	 * @return	A character buffer containing the file contents.
	 * 
	 * @throws	IOException
	 * 			If the given file could not be read.
	 */
	private static CharBuffer readFile(File file) throws IOException {
		FileChannel channel = null;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			channel = stream.getChannel();
			ByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			CharBuffer charBuffer = Charset.defaultCharset().newDecoder().decode(byteBuffer);
			return charBuffer;
		} finally {
			if (stream != null)
				stream.close();
		}
	}
}
