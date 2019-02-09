package sec07.exam03_chatting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ServerExample extends Application {
	ExecutorService executorService;
	ServerSocket serverSocket;
	List<Client> connections = new Vector<Client>();

	void startServer() {
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("localhost", 5003));
		} catch (Exception e) {
			if (!serverSocket.isClosed()) {
				stopServer();
			}
			return;
		}
		// 서버가 시작되면 클라이언트의 연결을 기다리는 작업
		// 이 작업을 처리하는 Thread 가 이제 모든 Client들의 연결을 담당한다.
		Runnable runnable = new Runnable() { 
			@Override
			public void run() {
				Platform.runLater(() -> { // JavaApplicationThread가 담당
					displayText("[서버 시작]");
					btnStartStop.setText("stop");
				});
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						String message = "[연결 수락: " + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName() + "]";
						Platform.runLater(() -> {
							displayText(message);
						});

						Client client = new Client(socket); // client들의 서비스를 처리하기위한 객체를 만들어준다.
						connections.add(client);
						Platform.runLater(() -> {
							displayText("[연결 개수: " + connections.size() + "]");
						});
					} catch (IOException e) {
						if (!serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
			}
		};
		executorService.submit(runnable);
	}

	void stopServer() {
		try {
			Iterator<Client> iterator = connections.iterator();
			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}

			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}
			Platform.runLater(() -> {
				displayText("[서버 멈춤]");
				btnStartStop.setText("start");
			});
		} catch (Exception e) {
		}
	}

	class Client { // 고객요청서비스처리객체
		Socket socket;

		Client(Socket socket) {
			this.socket = socket;
			receive();
		}

		void receive() {
			// 받기 작업을 정의 이걸 맡은 Thread가 받기전용 Thread 가 된다.
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						while (true) {
							byte[] byteArr = new byte[100];
							InputStream inputStream = socket.getInputStream();
							int readByteCount = inputStream.read(byteArr); 
							// 정상종료 비정상종료 데이터받음 3가지경우가 있다.(그에 따른 예외처리)
							if (readByteCount == -1) {
								throw new IOException();
							}
							String message = "[요청 처리 : " + socket.getRemoteSocketAddress() + ": "
									+ Thread.currentThread().getName() + "]";
							Platform.runLater(() -> displayText(message));
							String data = new String(byteArr, 0, readByteCount, "UTF-8");
							for (Client client : connections) { // 받은 데이터를 모든 client에게 뿌려준다.
								client.send(data);
							}
						}
					} catch (Exception e) {
						connections.remove(Client.this);
						String message = "[클라이언트 통신 안됨: ]" + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName() + "]";
						Platform.runLater(() -> displayText(message));
						try {
							socket.close();
						} catch (IOException e1) {
						}
					}
				}
			};
			executorService.submit(runnable);
		}

		void send(String data) { // data를 받아서 client에게 보내는 메소드
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						byte[] byteArr = data.getBytes("UTF-8");
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(byteArr);
						outputStream.flush();
					} catch (Exception e) {
						String message = "[클라이언트 통신 안됨: ]" + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName() + "]";
						Platform.runLater(() -> displayText(message));
						connections.remove(Client.this);
						try {
							socket.close();
						} catch (IOException e1) {}
					}
				}
			};
			executorService.submit(runnable);
		}
	}

	//////////////////////// GUI 부분 ////////////////////////
	TextArea txtDisplay;
	Button btnStartStop;

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(500, 300);

		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0, 0, 2, 0));
		root.setCenter(txtDisplay);

		btnStartStop = new Button("start");
		btnStartStop.setPrefHeight(30);
		btnStartStop.setMaxWidth(Double.MAX_VALUE);
		btnStartStop.setOnAction(e -> {
			if (btnStartStop.getText().equals("start")) {
				startServer();
			} else if (btnStartStop.getText().equals("stop")) {
				stopServer();
			}
		});
		root.setBottom(btnStartStop);

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Server");
		primaryStage.setOnCloseRequest(event -> stopServer());
		primaryStage.show();
	}

	void displayText(String text) {
		txtDisplay.appendText(text + "\n");
	}

	public static void main(String[] args) {
		launch(args);
	}
}
