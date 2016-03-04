package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import javax.swing.filechooser.FileSystemView;

import common.Command;
import mp3_joiner.*;

public class JavanymousClient
{
	public JavanymousClient (String host,int port)
	{
		
		Socket clientSocket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try
		{
			clientSocket = new Socket(host,port);
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());
			boolean running = true;
			while (running) {
				System.out.println("1.JOIN, 2.SORT, 3.EXIT \n");
				@SuppressWarnings("resource")
				Scanner in = new Scanner(System.in);
				String input = in.nextLine();
				switch (input) {
					case "1": 
						
						oos.writeObject(Command.JOIN);
						oos.flush();
						System.out.println("Join command Sent.");
						
						// making a file list from an m3u file
						String m3uFileLocation = getM3ULocationFromUser();
						File m3uFile = new File(m3uFileLocation);
						List<File> fileObjList = M3UHandler.getMP3FileListFromM3U(m3uFile);
						
						// sending the server the file list to join them
						oos.writeObject(fileObjList);
						oos.flush();
						System.out.println("File list Sent.");
						
						// sending the content of the mp3 files
						for (File file : fileObjList) {
							sendFile(file, clientSocket);
							System.out.println("Mp3 sent.");
						}
						
						// getting the joined mp3 file from the server
						long fileSize = (long) ois.readObject();
						String joinedDir = dirCreation();
						
						File saveFile = new File(joinedDir + "//joined.mp3");
						saveFile(clientSocket, saveFile, fileSize);
						System.out.println("Joined file saved.");
						break;
							
					case "2":
						oos.writeObject(Command.SORT);
						//TODO
						
					case "3":
						System.out.println("Exit command sent.");
						oos.writeObject(Command.EXIT);
						oos.flush();
						running = false;
				}
			}
			ois.close();
			oos.close();
			clientSocket.close();
			
		}
		catch (ConnectException e){
			System.out.println("Server probably not running dumbass :D.");
			e.printStackTrace();
			System.exit(1);
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public String dirCreation() {
		FileSystemView filesys = FileSystemView.getFileSystemView();
		@SuppressWarnings("unused")
		File[] roots = filesys.getRoots();
		String home = filesys.getHomeDirectory().getAbsolutePath();
		File newDir = new File(home +"//JoinedFile");
		if (!newDir.exists()) {
			newDir.mkdir();
		}
		return newDir.getAbsolutePath();
	}

	private String getM3ULocationFromUser() {
		System.out.println("Give the absolute path of the m3u file.");
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		return input;
	}
	
	@SuppressWarnings("unused")
	private void saveFile(Socket clientSock, File file, long fileSize) throws IOException
	{
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
		FileOutputStream fos = new FileOutputStream(file);
		byte [ ] buffer = new byte [ 4096 ];
		int read = 0;
		int totalRead = 0;
		long remaining = fileSize;
		double percentage;
		while ((read = dis.read(buffer, 0, Math.min(buffer.length, (int) remaining))) > 0){
			totalRead += read;
			remaining -= read;
//			System.out.println("read " + totalRead + " bytes.");
//			percentage = ((double)totalRead)/(double)(totalRead+remaining)*100;
//			System.out.println(String.format("%.1f", percentage) + "Downloading  %");
			fos.write(buffer, 0, read);
			if (remaining <= 0) {
				System.out.println("Joined File Saved.");
			}
		}
		fos.close();
	}

	public void sendFile(File file, Socket s) throws IOException
	{
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
		byte [ ] buffer = new byte [ 4096 ];
		int read;
		while ((read = fis.read(buffer)) > 0){
			dos.write(buffer, 0, read);
			dos.flush();
		}
		fis.close();
	}
	
	public static void main(String [ ] args)
	{
		new JavanymousClient("192.168.150.45",10022);
	}
	
	
}
