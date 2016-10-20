package edu.csupomona.cs585.ibox;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;

import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;
import edu.csupomona.cs585.ibox.sync.GoogleDriveServiceProvider;

/**
 * @author Shobi
 *
 */
public class GoogleDriveFileSyncManagerIntegrationTest {

	GoogleDriveFileSyncManager googleDriveFileSyncManager;
	File localFile;

	@Before
	public void setUp() throws Exception {
		googleDriveFileSyncManager = new GoogleDriveFileSyncManager(
				GoogleDriveServiceProvider.get().getGoogleDriveClient());
		localFile = new File("C:/Users/Shobi Alahesh/Desktop/CS585ibox/cs585");
	}

	@Test
	public void testAddFile() {
		try {
			localFile.getParentFile().mkdirs();
			localFile.createNewFile();
			googleDriveFileSyncManager.addFile(localFile);
			assertNotNull(googleDriveFileSyncManager.getFileId(localFile.getName()));
			googleDriveFileSyncManager.deleteFile(localFile);

		} catch (IOException e) {
			System.out.println("Exception occurs when creating a file: " + e);
		} finally {
			localFile.delete();
		}

	}

	@Test
	public void testUpdateFile() {
		long lengthAtEnd = 0;
		long lengthAtStart = 0;
		try {
			localFile.getParentFile().mkdirs();
			localFile.createNewFile();
			googleDriveFileSyncManager.addFile(localFile);

			lengthAtStart = getFileSize(localFile);

			// write content into the file
			FileWriter fw = new FileWriter(localFile);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			pw.write("Hello");
			pw.close();

			googleDriveFileSyncManager.updateFile(localFile);

			lengthAtEnd = getFileSize(localFile);
			assertNotEquals(lengthAtStart, lengthAtEnd);

			googleDriveFileSyncManager.deleteFile(localFile);

		} catch (IOException e) {
			System.out.println("Exception occurs when creating a file: " + e);
		} finally {
			localFile.delete();
		}

	}

	@Test
	public void testDeleteFile() {
		try {
			localFile.getParentFile().mkdirs();
			localFile.createNewFile();
			googleDriveFileSyncManager.addFile(localFile);

			googleDriveFileSyncManager.deleteFile(localFile);
			String id = googleDriveFileSyncManager.getFileId(localFile.getName());
			assertFalse(fileExists(localFile));


		} catch (IOException e) {
			System.out.println("Exception occurs when creating a file: " + e);
		} finally {
			localFile.delete();
		}

	}

	public long getFileSize(File localFile) {
		String id = "";
		long lenghtAtStart = 0;

		try {
			id = googleDriveFileSyncManager.getFileId(localFile.getName());
			lenghtAtStart = googleDriveFileSyncManager.service.files().get(id).execute().getFileSize();

		} catch (IOException e) {
			System.out.println("Exception occurs when generating the length of the file");
			e.printStackTrace();
		}

		return lenghtAtStart;
	}

	public boolean fileExists(File localFile) {
		String id = "";
		boolean result = false;
		try {
			id = googleDriveFileSyncManager.getFileId(localFile.getName());
			result = googleDriveFileSyncManager.service.files().get(id).isEmpty();
		} catch (IOException e) {
			System.out.println("Exception occurs when checking whether file exists or not " +e);
			
		}
		return result;
	}
}
