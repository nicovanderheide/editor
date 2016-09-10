package nl.nicovanderheide.locale.properties.editor.support;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward F. de Graaf
 */
public class ZipCreator {

	private static final int ZIP_COMPRESSION_METHOD = ZipOutputStream.DEFLATED;
	private static final int ZIP_COMPRESSION_LEVEL = 9;
	private final static Logger logger = LoggerFactory.getLogger(ZipCreator.class.getName());

	/**
	 * Create an zipfile that wil be written to an outputstream
	 * 
	 * @param files
	 *            the file to be zipped
	 * @param ous
	 *            the outputstream that is used to be written to
	 * @return true if an zip file was successfully created
	 */
	public boolean createZip(List<ZipFileEntry> files, OutputStream ous) {
		boolean retValue = false;

		if (files != null && files.size() > 0) {
			retValue = true;

			List<String> fileNames = new ArrayList<String>();

			ZipOutputStream zos = new ZipOutputStream(ous);

			zos.setMethod(ZIP_COMPRESSION_METHOD);
			zos.setLevel(ZIP_COMPRESSION_LEVEL);

			for (ZipFileEntry file : files) {
				ZipEntry entry = new ZipEntry(getUniqueFileName(fileNames, file.getName()));

				try {
					zos.putNextEntry(entry);
					IOUtils.copyLarge(file.getIs(), zos);
					zos.closeEntry();
				} catch (IOException e) {
					logger.error("Error creating zip entry. Filename : " + entry.getName(), e);
					retValue = false;
				}
			}

			try {
				zos.close();
			} catch (IOException e) {
				logger.error("Error closing zipoutputstream", e);
				retValue = false;

			}
		}

		return retValue;
	}

	/**
	 * 
	 * @param filenames
	 *            list of filenames that already present in the zipfile
	 * @param filename
	 *            the filename to be checked
	 * @return the unique filename within the zipfile
	 */
	private String getUniqueFileName(List<String> filenames, String fileName) {
		int i = 1;

		while (filenames.contains(fileName)) {
			if (i > 1) {
				/* remove laatste haken set */
				int startPos = fileName.lastIndexOf("(");
				int endPos = fileName.lastIndexOf(")") + 1;

				fileName = fileName.substring(0, startPos) + fileName.substring(endPos);
			}

			if (fileName.lastIndexOf(".") == -1) {
				fileName = fileName + "(" + i++ + ")";
			} else {
				fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "(" + i++ + ")" + fileName.substring(fileName.lastIndexOf("."));
			}
		}

		filenames.add(fileName);

		return fileName;
	}
}
