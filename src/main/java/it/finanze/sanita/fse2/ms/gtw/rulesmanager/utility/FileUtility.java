/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * The Class FileUtils.
 *
 * @author vincenzoingenito
 * 
 */
@Slf4j
public final class FileUtility {

	/**
	 * Max size chunk.
	 */
	private static final int CHUNK_SIZE = 16384;

	/**
	 * Constructor.
	 */
	private FileUtility() {
	}

	/**
	 * Save on filesystem.
	 *
	 * @param content	content
	 * @param fileName	path
	 */
	public static void saveToFile(final byte[] content, final String fileName) {
		try (BufferedOutputStream bs = new BufferedOutputStream(new FileOutputStream(new File(fileName)))){
		    bs.write(content);
		} catch (Exception e) {
			log.error("FILE UTILS saveToFile(): Error saving a file on the filesystem. ", e);
		} 
	}

	/**
	 * Metodo per il recupero del contenuto di un file da file system.
	 *
	 * @param filename	nome del file
	 * @return			contenuto del file
	 */
	public static byte[] getFileFromFS(final String filename) {
		byte[] b = null;
		try {
			File f = new File(filename);
			InputStream is = new FileInputStream(f);
			b = getByteFromInputStream(is);
			is.close();
		} catch (Exception e) {
			log.error("FILE UTILS getFileFromFS():Error retrieving the contents of a file from the file system ", e);
		}
		return b;
	}

	/**
	 * Metodo per il recupero del contenuto di un file dalla folder interna "/src/main/resources".
	 *
	 * @param filename	nome del file
	 * @return			contenuto del file
	 */
	public static byte[] getFileFromInternalResources(final String filename) {
		byte[] b = null;
		InputStream is = null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
			b = getByteFromInputStream(is);
			is.close();
		} catch (Exception e) {
			log.error("FILE UTILS getFileFromInternalResources(): Error while retrieving the contents of a file from the folder  '/src/main/resources'. ", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(""+e);
				}
			}
		}
		return b;
	}

	/**
	 * Recupero contenuto file da input stream.
	 *
	 * @param is
	 *            input stream
	 * @return contenuto file
	 */
	private static byte[] getByteFromInputStream(final InputStream is) {
		byte[] b;
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[CHUNK_SIZE];

			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			b = buffer.toByteArray();
		} catch (Exception e) {
			log.error("Errore durante il trasform da InputStream a byte[]: ", e);
			throw new BusinessException(e);
		}
		return b;
	}

}
