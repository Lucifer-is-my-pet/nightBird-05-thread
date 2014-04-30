import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;


public class Handler implements Runnable {

	Socket socket;

	WoodDrawingThread drawing;
	private ArrayList<Point> starts;
	private ArrayList<Point> finishes;
	PrintableWood pWood; 

	public Handler(Socket socket, PrintableWood wood, ArrayList<Point> starts, ArrayList<Point> finishes) {
		this.pWood = wood;
		//notify
		// передавать потоку что-то на отрисовку
		this.starts = starts;
		this.finishes = finishes;

	}

	@Override
	public void run() {
		Action act = Action.Ok;
		int lifeCount = 3;
		Random chooseThePoint = new Random();	

		//обрабатывать команды, отвечать, пинать отрисовывающий поток


		try {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			do {
				MessageToServer recieved = (MessageToServer) ois.readObject();
				switch (recieved.getMethodName()) {
				case "createWoodman":
					synchronized (pWood) { 
						pWood.createWoodman(recieved.getWoodmanName(), starts.get(chooseThePoint.nextInt(starts.size())), finishes.get(chooseThePoint.nextInt(finishes.size())));
					}
					break;
				case "move":				
					synchronized (pWood) {
						pWood.move(recieved.getWoodmanName(),
								recieved.getDirection());
					}
					break;
				}
			} while (act != Action.WoodmanNotFound && act != Action.Finish);


		} 

		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			ois.close();
			oos.close();
		}

	}
}