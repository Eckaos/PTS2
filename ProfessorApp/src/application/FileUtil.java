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
	
	public static String stripStudentName(File file) {
		if (file == null) {
			return null;
		}
		String name = file.getName();

		int posPoint = name.lastIndexOf("_");

		if (posPoint == -1) {
			return name;
		}

		return name.substring(0, posPoint);
	}
	
	public static String stripExerciceName(File file) {
		String name = stripExtension(file);
		if (file == null) {
			return null;
		}

		int posPoint = name.lastIndexOf("_");

		if (posPoint == -1) {
			return name;
		}
		
		return name.substring(posPoint+1, name.length());
	}
}
