package org.dpr.mykeys.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MkUtils {

	public MkUtils() {
		// TODO Auto-generated constructor stub
	}

	public static void copyFile(InputStream is, File out) throws Exception {
		// InputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = is.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (is != null)
				is.close();
			if (fos != null)
				fos.close();
		}
	}

}
