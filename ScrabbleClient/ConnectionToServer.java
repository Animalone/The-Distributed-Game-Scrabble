package ScrabbleClient;

import java.io.IOException;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ScrabbleClient.MyClient;

public class ConnectionToServer {
    private MyClient myClient;
    private Thread t;
    private JSONParser parser = new JSONParser();
    private Crossword cw;

    public ConnectionToServer(Crossword cw, MyClient myClient) {
        this.cw = cw;
        this.myClient = myClient;
    }

    public void connectionToServer() throws IOException {
        // while (true) {
        String content = new String();
        while (myClient.getBufferReader() != null) {
            String message = myClient.getBufferReader().readLine();
            System.out.println("Server:" + message);
            readMsg(message);
        }
        // System.out.println(1);
        // }
    }

    public void readMsg(String message) {
        try {
            JSONObject serverJSON = (JSONObject) parser.parse(message);
            if (serverJSON.containsKey("TURN")) {
                if (serverJSON.get("TURN").equals(myClient.getUserName())) {
                    cw.setTurn(true);
                }
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
