package application;

import java.io.File;

public class FileUtil {

	public static String stripExtension(File file) {
		if (file == null) {
			return null;
		}
		String name = file.getName();

		int posPoint = name.lastIndexOf(".");

		if (posPoint == -1) {
			return name;
		}

		return name.substring(0, posPoint);
	}

	public static String getExtension(File file) {
		if (file == null) {
			return null;
		}

		String name = file.getName();
		int posPoint = name.lastIndexOf(".");

		if (posPoint == -1) {
			return null;
		}

		return name.substring(posPoint);
	}
}
