package com.android.xiaow.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * �����ļ������Ĺ�����
 */
public class FileUtils {
	private final static int BUFFER = 1024;

	// �����ļ�
	public static void copy(InputStream sourceFileIs, File targetFile)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(targetFile);
		byte[] buffer = new byte[BUFFER];
		int count = 0;
		while ((count = sourceFileIs.read(buffer)) > 0) {
			fos.write(buffer, 0, count);
		}
		fos.close();
		sourceFileIs.close();
	}
}
