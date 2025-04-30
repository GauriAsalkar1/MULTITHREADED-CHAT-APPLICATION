package chatting.application1;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.io.*;

/**
 * Server class for a multithreaded chat application
 * Handles client connections and message broadcasting
 */
public class Server implements ActionListener {
    
    // GUI Components
    JTextField text;                // Input field for messages
    JPanel a1;                      // Main message display panel
    static Box vertical = Box.createVerticalBox();  // Vertical layout for messages
    static JFrame f = new JFrame();  // Main application window
    static DataOutputStream dout;    // Output stream to clients
    
    /**
     * Server constructor - Initializes the GUI
     */
    Server() {
        // Configure main window
        f.setLayout(null);
        
        // Create header panel
        JPanel p1 = new JPanel();
        p1.setBackground(new Color(0,191,255));  // Light blue background
        p1.setBounds(0, 0, 450, 70);
        p1.setLayout(null);
        f.add(p1);
        
        // Add close button with icon
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/close.png"));
        Image i2 = i1.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel close = new JLabel(i3);
        close.setBounds(400, 20, 30, 15);
        p1.add(close);
        
        // Close button event handler
        close.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae) {
                System.exit(0);  // Terminate application
            }
        });
        
        // Add server profile picture
        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("icons/server.jpg"));
        Image i5 = i4.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel profile = new JLabel(i6);
        profile.setBounds(40, 10, 50, 50);
        p1.add(profile);
        
        // Server name label
        JLabel name = new JLabel("Server");
        name.setBounds(110, 15, 100, 18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        p1.add(name);
        
        // Status indicator
        JLabel status = new JLabel("Active Now");
        status.setBounds(110, 35, 100, 18);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF", Font.BOLD, 14));
        p1.add(status);
        
        // Main message display area
        a1 = new JPanel();
        a1.setBounds(5, 75, 440, 570);
        f.add(a1);
        
        // Message input field
        text = new JTextField();
        text.setBounds(5, 655, 310, 40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(text);

        // Enter key listener for message sending
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    ActionEvent ae = new ActionEvent(text, ActionEvent.ACTION_PERFORMED, "");
                    actionPerformed(ae);  // Trigger send action
                }
            }
        });
        
        // Send button configuration
        JButton send = new JButton("Send");
        send.setBounds(320, 655, 123, 40);
        send.setBackground(new Color(0,191,255));
        send.setForeground(Color.WHITE);
        send.addActionListener(this);
        send.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(send);
        
        // Window configuration
        f.setSize(450, 700);
        f.setLocation(200, 50);
        f.setUndecorated(true);      // Remove window decorations
        f.getContentPane().setBackground(Color.WHITE);
        f.setVisible(true);
    }
    
    /**
     * Action handler for send button/message input
     * @param ae ActionEvent object
     */
    public void actionPerformed(ActionEvent ae) {
        try {
            String out = text.getText();  // Get message text

            // Format message for display
            JPanel p2 = formatLabel(out);

            // Update message display area
            a1.setLayout(new BorderLayout());
            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));  // Add spacing
            a1.add(vertical, BorderLayout.PAGE_START);

            // Send message to client
            dout.writeUTF(out);
            text.setText("");  // Clear input field

            // Refresh UI
            f.repaint();
            f.invalidate();
            f.validate();   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Formats a message into a display panel with timestamp
     * @param out The message text to format
     * @return JPanel containing formatted message
     */
    public static JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Message bubble with HTML formatting
        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(135,206,250));  // Light blue bubble
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));
        
        // Add timestamp
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));
        
        // Combine components
        panel.add(output);
        panel.add(time);
        
        return panel;
    }
    
    /**
     * Main method - Starts the server and handles client connections
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        new Server();  // Initialize GUI
        
        try {
            // Create server socket on port 6001
            ServerSocket skt = new ServerSocket(6001);
            System.out.println("Server started on port 6001");
            
            while(true) {
                // Accept new client connection
                Socket s = skt.accept();
                System.out.println("New client connected: " + s);
                
                // Set up I/O streams
                DataInputStream din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());
                
                // Message handling loop
                while(true) {
                    String msg = din.readUTF();  // Read incoming message
                    System.out.println("Received: " + msg);
                    
                    // Format and display message
                    JPanel panel = formatLabel(msg);
                    JPanel left = new JPanel(new BorderLayout());
                    left.add(panel, BorderLayout.LINE_START);
                    vertical.add(left);
                    f.validate();  // Update UI
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
