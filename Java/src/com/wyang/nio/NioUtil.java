package com.wyang.nio;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class NioUtil {
	private static int BUFFER_SIZE = 32;
	private static int LINE_BREAK = 10;
	private static int ENTER = 13;
	private static String ENCODE = "UTF-8";

	/**
	 * This is a method to read line of a file by nio
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public static void readLineByNio(String filePath) throws Exception {
		FileChannel fileChannel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ);

		ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

		byte[] restContent = new byte[0];
		
		while (fileChannel.read(byteBuffer) != -1) {
			int contentSize = byteBuffer.position();
			byte[] contentByteArr = new byte[contentSize];
			byteBuffer.rewind();
			byteBuffer.get(contentByteArr);
			byteBuffer.clear();

			int startIndex = 0;
			boolean hasLF = false;
			for (int i = 0; i < contentByteArr.length; i++) {
				if (contentByteArr[i] == LINE_BREAK) {
					hasLF = true;
					int resetContentSize = restContent.length;
					int currentContentSize = i - startIndex;

					byte[] currentContentArr = new byte[resetContentSize + currentContentSize];
					System.arraycopy(restContent, 0, currentContentArr, 0, resetContentSize);
					System.arraycopy(contentByteArr, startIndex, currentContentArr, resetContentSize,
							currentContentSize);
					restContent = new byte[0];

					String line = new String(currentContentArr, 0, currentContentArr.length, ENCODE);
					System.err.println(line);

					if (i + 1 < contentSize && contentByteArr[i + 1] == ENTER) {
						startIndex = i + 2;
					} else {
						startIndex = i + 1;
					}
				}
			}
			
			if(hasLF){  
				restContent = new byte[contentByteArr.length - startIndex];  
				System.arraycopy(contentByteArr, startIndex, restContent, 0, restContent.length);
			} else {
				// For the case buffer is not big enough and can't cover the
				// whole line;
				byte[] toRestContent = new byte[restContent.length + contentSize];
				System.arraycopy(restContent, 0, toRestContent, 0, restContent.length);
				System.arraycopy(contentByteArr, 0, toRestContent, restContent.length, contentByteArr.length);
				restContent = toRestContent;
			}
		}
	}
}
