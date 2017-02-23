package fr.unice.polytech.invitee.utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nathael on 22/02/17.
 */
public class Utils {
	private Utils() {}

	public static Set<File> listSubFiles(File in) {
		final Set<File> fileSet = new HashSet<>();

		if(in.isDirectory()) {
			File[] subFiles = in.listFiles();

			if(subFiles != null) {
				for (File f : subFiles) {
					if (f != in)
						fileSet.addAll(listSubFiles(f));
				}
			}
		} else if(in.isFile() && in.canRead()) {
			fileSet.add(in);
		}

		return fileSet;
	}

}
