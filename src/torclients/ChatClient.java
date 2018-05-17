package torclients;

import com.msopentech.thali.java.toronionproxy.JavaOnionProxyContext;
import com.msopentech.thali.java.toronionproxy.JavaOnionProxyManager;
import com.msopentech.thali.java.toronionproxy.OnionProxyManager;
import com.msopentech.thali.java.toronionproxy.Utilities;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static com.msopentech.thali.java.toronionproxy.Utilities.socks4aSocketConnection;
import static java.lang.Thread.getAllStackTraces;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;


public class ChatClient {

 
    static class ChatAccess extends Observable {
        private Socket socket;
        private OutputStream outputStream;

        
        public void notifyObservers(Object arg) {
            super.setChanged();						//ritorna true quando varia
            super.notifyObservers(arg);
        }

        public void InitSocket(String OnionAdress, int hiddenServicePort,int localPort) throws IOException {
            socket = socks4aSocketConnection(OnionAdress, hiddenServicePort, "127.0.0.1", localPort);		//ip e porta
            outputStream = socket.getOutputStream();	//outputStream viene usato per mandare messaggi

            /*Viene creato un thread per la gestione della ricezione dei messaggi dal server*/
            Thread receivingThread = new Thread() {
                public void run() {
                    try {
                    	/*Riceve messaggi dal server*/
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));	
                        String line;
                        
                        /*Legge i messaggi dal server*/
                        while ((line = reader.readLine()) != null){
                        	notifyObservers(line);
                        	}
                    } catch (IOException ex) {

                        notifyObservers("Connessione Scaduta");
                    }
                }
            };
            receivingThread.start();
        }
        

        private static final String CRLF = "\r\n";  //manda a capo in automatico il testo "Carriage Return Line Feed"

        public void send(String text) {
            try {
                outputStream.write((text + CRLF).getBytes());	//codifica il testo in bytes
                outputStream.flush();	//libera il canale
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }

        public void close() {
            try {
            	/*Chiude la connessione col server*/
                socket.close();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }

    static class ChatFrame extends JFrame implements Observer {

        private JTextArea textArea;
        private JTextField inputTextField;
        private JButton sendButton;
        private ChatAccess chatAccess;

        public ChatFrame(ChatAccess chatAccess) {
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            buildGUI();
        }

        private void buildGUI() {
            textArea = new JTextArea(20, 50);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            add(new JScrollPane(textArea), BorderLayout.CENTER);

            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            inputTextField = new JTextField();
            sendButton = new JButton("Invia");
            box.add(inputTextField);
            box.add(sendButton);

            ActionListener sendListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String str = inputTextField.getText();
                    if (str != null && str.trim().length() > 0)
                        chatAccess.send(str);
                    inputTextField.selectAll();
                    inputTextField.requestFocus();
                    inputTextField.setText("");
                }
            };
            inputTextField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);

            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    chatAccess.close();
                }
            });
        }

        public void update(Observable o, Object arg) {
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(finalArg.toString());
                    textArea.append("\n");
                }
            });
        }
    }

  
    public static void Client(String username) {

        Thread starttor = new Thread(){
            private volatile int localport;
            @Override
            public void run(){
                    OnionProxyManager onionProxyManager;
                    String fileStorageLocation = "torfiles";
                    onionProxyManager = new JavaOnionProxyManager(new JavaOnionProxyContext(new File(fileStorageLocation)));

                    int totalSecondsPerTorStartup = 4 * 60;
                    int totalTriesPerTorStartup = 5;

                    // Start the Tor Onion Proxy
                    try {
                        if (onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup) == false) {
                            return;
                        }
                        int hiddenServicePort = 80;
                        localport = onionProxyManager.getIPv4LocalHostSocksPort();

                String OnionAdress = "4rl344vcd7lnce3a.onion";


                ChatAccess access = new ChatAccess();

                JFrame frame = new ChatFrame(access);
                frame.setTitle("ChatApp via TOR");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setVisible(true);

                try {
                    access.InitSocket(OnionAdress, hiddenServicePort,localport);
                    access.send(username);
                } catch (IOException ex) {
                    System.out.println("Impossibile connettersi");
                    ex.printStackTrace();
                    System.exit(0);
                }



                } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            };



        } ;
        starttor.start();
    }}
