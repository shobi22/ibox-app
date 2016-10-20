/**
 * 
 */
package edu.csupomona.cs585.ibox;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Delete;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.Drive.Files.Update;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;

/**
 * @author Shobi
 *
 */
public class GoogleDriveFileSyncManagerTest {

	private GoogleDriveFileSyncManager googleDriveFileSyncManager; // variable
																	// to hold
																	// the
																	// object
																	// ofGoogleDriveFileSyncManager
																	// class
	private Drive mockService; // Variable to hold the mock of the Drive class
	private ByteArrayOutputStream outContent; // variable to hold the output
												// from the console
	private java.io.File localFile;
	private Files mock_files;
	private Insert mock_inserts;
	private Update mock_updates;
	private List mock_lists;
	private Delete mock_deletes;

	@Before
	public void setUp() throws Exception {
		mockService = mock(Drive.class);
		googleDriveFileSyncManager = new GoogleDriveFileSyncManager(mockService);
		outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));

		localFile = new java.io.File("C:/Users/Shobi Alahesh/Desktop/CS585ibox/cs585");
		mock_files = mock(Files.class);
		mock_inserts = mock(Insert.class);
		mock_updates = mock(Update.class);
		mock_lists = mock(List.class);
		mock_deletes = mock(Delete.class);
	}

	@Test
	public void testGoogleDriveFileSyncManager() {
		assertNotNull(googleDriveFileSyncManager.service);
	}

	@Test
	public void testAddFile_Without_Exception() throws IOException {
		File body = new File();
		body.setId("cs585_id");

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.insert(Mockito.any(File.class), Mockito.any(FileContent.class))).thenReturn(mock_inserts);
		when(mock_inserts.execute()).thenReturn(body);

		googleDriveFileSyncManager.addFile(localFile);

		verify(mock_inserts).execute();

		assertEquals("File ID: " + body.getId().trim(), outContent.toString().trim());

	}

	@Test(expected = IOException.class)
	public void testAddFile_With_IO_Exception() throws IOException {
		File body = new File();
		body.setId("cs585_id");

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.insert(Mockito.any(File.class), Mockito.any(FileContent.class))).thenReturn(mock_inserts);
		when(mock_inserts.execute()).thenThrow(new IOException());

		googleDriveFileSyncManager.addFile(localFile);

	}

	@Test
	public void testUpdateFile_When_FileId_IS_Not_Null() throws IOException {
		FileList fileList = new FileList();
		File file = new File();
		file.setId("cs585");
		file.setTitle("cs585");

		ArrayList<File> list = new ArrayList<File>();
		list.add(file);
		fileList.setItems(list);

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.list()).thenReturn(mock_lists);
		when(mock_lists.execute()).thenReturn(fileList);
		when(mock_files.update(Mockito.any(String.class), Mockito.any(File.class), Mockito.any(FileContent.class)))
				.thenReturn(mock_updates);
		when(mock_updates.execute()).thenReturn(file);

		googleDriveFileSyncManager.updateFile(localFile);
		assertEquals("File ID: cs585", outContent.toString().trim());

		verify(mock_files).list();
		verify(mock_lists).execute();
		verify(mock_updates).execute();

	}

	@Test(expected = IOException.class)
	public void testUpdateFile_With_IOException() throws IOException {
		FileList fileList = new FileList();
		File file = new File();
		file.setId("cs585");
		file.setTitle("cs585");

		ArrayList<File> list = new ArrayList<File>();
		list.add(file);
		fileList.setItems(list);

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.list()).thenReturn(mock_lists);
		when(mock_lists.execute()).thenReturn(fileList);
		when(mock_files.update(Mockito.any(String.class), Mockito.any(File.class), Mockito.any(FileContent.class)))
				.thenReturn(mock_updates);
		when(mock_updates.execute()).thenThrow(new IOException());

		googleDriveFileSyncManager.updateFile(localFile);

	}

	@Test
	public void testUpdateFile_When_FileId_IS_Null() throws IOException {
		FileList fileList = new FileList();
		File file = new File();
		file.setId("cs5855");
		file.setTitle("cs5855");

		ArrayList<File> list = new ArrayList<File>();
		list.add(file);
		fileList.setItems(list);

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.list()).thenReturn(mock_lists);
		when(mock_lists.execute()).thenReturn(fileList);
		when(mock_files.insert(Mockito.any(File.class), Mockito.any(FileContent.class))).thenReturn(mock_inserts);
		when(mock_inserts.execute()).thenReturn(file);

		googleDriveFileSyncManager.updateFile(localFile);

		verify(mock_files).list();
		verify(mock_lists).execute();
		verify(mock_inserts).execute();
		
		assertEquals("File ID: cs5855", outContent.toString().trim());
		
		
	}

	@Test
	public void testDeleteFile() throws IOException {
		FileList fileList = new FileList();
		File file = new File();
		file.setId("cs585");
		file.setTitle("cs585");

		ArrayList<File> list = new ArrayList<File>();
		list.add(file);
		fileList.setItems(list);

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.list()).thenReturn(mock_lists);
		when(mock_lists.execute()).thenReturn(fileList);
		when(mock_files.delete(Mockito.any(String.class))).thenReturn(mock_deletes);

		googleDriveFileSyncManager.deleteFile(localFile);
		
		verify(mock_files).list();
		verify(mock_lists).execute();
		verify(mock_deletes).execute();

	}

	@Test(expected = FileNotFoundException.class)
	public void testDeleteFile_With_FileNotFoundException() throws IOException {
		FileList fileList = new FileList();
		File file = new File();
		file.setId("cs5855");
		file.setTitle("cs5855");

		ArrayList<File> list = new ArrayList<File>();
		list.add(file);
		fileList.setItems(list);

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.list()).thenReturn(mock_lists);
		when(mock_lists.execute()).thenReturn(fileList);

		googleDriveFileSyncManager.deleteFile(localFile);
		
		verify(mock_files).list();
		verify(mock_lists).execute();
		

	}

	@Test(expected = IOException.class)
	public void testDeleteFile_With_IOException() throws IOException {
		FileList fileList = new FileList();
		File file = new File();
		file.setId("cs5855");
		file.setTitle("cs5855");

		ArrayList<File> list = new ArrayList<File>();
		list.add(file);
		fileList.setItems(list);

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.list()).thenReturn(mock_lists);
		when(mock_lists.execute()).thenThrow(new IOException());

		googleDriveFileSyncManager.deleteFile(localFile);

		verify(mock_files).list();
		verify(mock_lists).execute();
		
	}

	@Test
	public void testGetFileId() throws IOException {
		FileList fileList = new FileList();
		File file = new File();
		file.setId("cs585_id");
		file.setTitle("cs585_title");

		ArrayList<File> list = new ArrayList<File>();
		list.add(file);
		fileList.setItems(list);

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.list()).thenReturn(mock_lists);
		when(mock_lists.execute()).thenReturn(fileList);

		String fileId = googleDriveFileSyncManager.getFileId(file.getTitle());

		verify(mock_files).list();
		verify(mock_lists).execute();

		assertNotNull(fileId);
		
		
	}

	@Test
	public void testGetFileId_With_Null() throws IOException {
		FileList fileList = new FileList();
		File file = new File();
		file.setId("cs585_id");
		file.setTitle("cs585_title");

		ArrayList<File> list = new ArrayList<File>();
		list.add(file);
		fileList.setItems(list);

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.list()).thenReturn(mock_lists);
		when(mock_lists.execute()).thenReturn(fileList);

		String fileId = googleDriveFileSyncManager.getFileId("title_null");

		verify(mock_files).list();
		verify(mock_lists).execute();

		assertNull(fileId);


	}

	@Test
	public void testGetFileId_Throws_IOException() throws IOException {

		when(mockService.files()).thenReturn(mock_files);
		when(mock_files.list()).thenReturn(mock_lists);
		when(mock_lists.execute()).thenThrow(new IOException());

		googleDriveFileSyncManager.getFileId("cs585");
		
		verify(mock_files).list();
		verify(mock_lists).execute();
		
		assertEquals("An error occurred: java.io.IOException", outContent.toString().trim());
	
		
	}

}
