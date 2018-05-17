package torclients;


import sun.awt.resources.awt;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class GUI {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Login or Registration");
		frame.setSize(300, 170);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		frame.add(panel);
		placeComponents(frame,panel);
		frame.setVisible(true);
	}

	private static void placeComponents(JFrame frame,JPanel panel) {

		panel.setLayout(null);

		JLabel userLabel = new JLabel("User");
		userLabel.setBounds(10, 10, 80, 25);
		panel.add(userLabel);

		JTextField userText = new JTextField(20);
		userText.setBounds(100, 10, 160, 25);
		panel.add(userText);
                String user = userText.getText();
                System.out.println(user);

		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(10, 40, 80, 25);
		panel.add(passwordLabel);

		JPasswordField passwordText = new JPasswordField(20);
		passwordText.setBounds(100, 40, 160, 25);
		panel.add(passwordText);
                String password = passwordText.getText();
                System.out.println(password);

		JButton loginButton = new JButton("login");
		loginButton.setBounds(10, 100, 80, 25);
		panel.add(loginButton);
		
		JButton registerButton = new JButton("register");
		registerButton.setBounds(180, 100, 80, 25);
		panel.add(registerButton);
                
                JLabel responseLabel = new JLabel("");
		responseLabel.setBounds(110, 70, 180, 25);
		panel.add(responseLabel);
                
                loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String user = userText.getText();
                    String password = passwordText.getText();
                    String request = "http://18.184.144.163:9100/login?username="+user+"&"+"password="+password;
                    String risposta_login = Chat.executePost(request);
                    responseLabel.setText(risposta_login);
                    risposta_login = risposta_login.replaceAll("(\\r|\\n)", "");
                    if (risposta_login.equals("LOGIN ERRATO")){
                        responseLabel.setText("LOGIN ERRATO");
                    }
                    
                    else{
                        ProgressBar ps = new ProgressBar();
                        frame.dispose();
                        ChatClient.Client(risposta_login);
                    }
                    
                }
                });
                
                registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String user = userText.getText();
                    String password = passwordText.getText();
                    String request = "http://18.184.144.163:9100/signup?username="+user+"&"+"password="+password;
                    String risposta_signup = Chat.executePost(request);
                    responseLabel.setText(risposta_signup);
                }
                
                
                });
	}
}
