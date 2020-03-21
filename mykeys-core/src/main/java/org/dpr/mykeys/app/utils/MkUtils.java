package org.dpr.mykeys.app.utils;

import java.io.*;

public class MkUtils {

	public static void copyFile(InputStream is, File out) throws IOException {


		try (FileOutputStream fos = new FileOutputStream(out)) {
			byte[] buf = new byte[1024];
			int i;
			while ((i = is.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		}
	}

}
