import javafx.scene.control.ListView;


class MyTest {
	static Server serverConnection;
	static Client clientConnection;
	static ListView<String> listItems,listItems2;
	static DataPackage dataResult;
	static String resultType, resultMessage, resultRecipients;

//	@BeforeAll
//	static void setup(){
//		// Create new server Connection
//		serverConnection = new Server(data -> {
//			DataPackage dp = (DataPackage) data; // Dp is sent from
//			Platform.runLater(()->{
//				resultMessage = dp.getData().toString();
//			});
//		});
//
//		// Create new client connection
//		clientConnection = new Client(data->{
//			DataPackage dp = (DataPackage) data; // Dp is sent from
//			if (dp.getType().equals("LIST")){
//				Platform.runLater(()->{
//					dataResult = (DataPackage) data;
//				});
//			}
//			if(dp.getType().equals("MESSAGE")) {
//				Platform.runLater(() -> {
//					dataResult = (DataPackage) data;
//				});
//			}
//			if(dp.getType().equals("DM")){
//				Platform.runLater(()->{
//					dataResult = (DataPackage) data;
//				});
//			}
//		});
//		clientConnection.start();
//	}

//	@Test
//	void test_MessageData() {
//		// Send dataPack of type MESSAGE
//		DataPackage data = new DataPackage("MESSAGE","testtesttest");
//		clientConnection.send(data);
//		resultMessage = dataResult.getData().toString();
//		assertEquals("testtesttest",resultMessage,"Wrong Value");
//	}
}
