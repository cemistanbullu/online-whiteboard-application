import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;



public class Client extends javax.swing.JFrame {


    public Client() {
    	
    	initMemberVariables();
    	initComponents();
        initSocketConnection();

        
    }

    private void initMemberVariables() {
    	connectedToServer = false;
    	shapes = new ArrayList<Shape>();
    	timeLeft = 80000;
    }
    
    private void initSocketConnection() {
    	
    	new Thread(() -> {
    		
    		while(!connectedToServer) {
    			try {
        			
        			
        			s = new Socket("127.0.0.1",1203);
        			connectedToServer = true;
        			timer.start();
        			os = new ObjectOutputStream(s.getOutputStream());
        			is = new ObjectInputStream(s.getInputStream());
    	        	
    	        	

    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
			while(connectedToServer) {
        		
				try {
					
					NetworkData data = (NetworkData)is.readObject();
					switch (data.selection) {
    					case MESSAGE:
    						msg_area.append("Teacher:\t" + data.msg+"\n");
    						break;
    					case SHAPE:
       						Shape shape = data.shape;
    						shapes.add(shape);
    						whiteBoard.repaint();
    						shapeCounter.setText("Shape Count : " + shapes.size());
    						break;
    						
    					default:
    						break;
					}
					

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
    	}).start();
    }
                         
    private void initComponents() {

    	panel = new JPanel();
        board_area = new JLabel();
        msg_text = new JTextField();
        msg_send = new JButton();
        msg_area = new JTextArea();
        time_label = new JLabel();
        Menu_Bar = new JMenuBar();
        hand = new JCheckBox();
        shapeCounter = new JLabel();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(620,500);
        
        // MAIN PANEL
        panel.setLayout(null);
        add(panel);
        
        setJMenuBar(Menu_Bar);

        initMsgAreaComponent();

        initMsgTextComponent();
        
        initMsgSendComponent();
        
        initBoardAreaComponent();
        
        initHandComponent();
        
        initTimerComponent();
        
        initShapeCounterComponent();
        
        

        
    }
    
    void initMsgAreaComponent() {
        // MESSAGE AREA
    	msg_area.setBounds(350, 20, 230, 310);
        msg_area.setEditable(false);
        //msg_area.setBorder(new LineBorder(Color.BLACK));
        msg_area.setBorder(BorderFactory.createCompoundBorder(
        		msg_area.getBorder(), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        msg_area.setLineWrap(true);
        msg_area.setWrapStyleWord(true);
        
       
        JScrollPane scrollPane =  new JScrollPane(msg_area);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(350, 20, 230, 310);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getViewport().add(msg_area);
        
        panel.add(scrollPane);
        
        setLocationRelativeTo(null);
    }
    
    void initMsgTextComponent() {
        // MSG TEXT
        msg_text.setBounds(350, 340, 150, 30);
        panel.add(msg_text);
    }
    
    void initMsgSendComponent() {
        // MSG_SEND BUTTON
        msg_send.setBounds(510,340, 70,30);
        msg_send.setText("send");
        msg_send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                msg_sendActionPerformed(evt);
            }
        });
        panel.add(msg_send);
        
    }
    
    void initHandComponent() {
        // RISE HAND 
        hand.setBounds(20,380,100,15);
        hand.setText("Rise Hand");
        hand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	handActionPerformed(evt);
            }
        });
        panel.add(hand);
        
  
        
    }
    
    void initBoardAreaComponent() {
        // BOARD AREA
        board_area.setBounds(20,20,310,350);
        board_area.setLayout(new BorderLayout());
        board_area.setBackground(new Color(255, 255, 255));
        board_area.setForeground(new Color(255, 255, 255));
        board_area.setOpaque(true);
        board_area.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        whiteBoard = new StudentBoard();
        board_area.add(whiteBoard);
        panel.add(board_area);
    }
    
    void initTimerComponent() {
        timer=new Timer(100, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                timeLeft -= 100;
                SimpleDateFormat df=new SimpleDateFormat("mm:ss");
                time_label.setText(df.format(timeLeft));
                if(timeLeft<=0)
                {
                	JOptionPane.showMessageDialog(rootPane, "Time is over", "Lesson is dismissed ", 0);
                    timer.stop();
                }
            }
        });
        timer.setInitialDelay(0);
        //timer.start();
        
        time_label.setBounds(545,380,40,20);
        panel.add(time_label);
    }
    
    
    void initShapeCounterComponent() {
    	shapeCounter.setBounds(20,400,120,20);
    	shapeCounter.setText("Shape Count : 0");
    	panel.add(shapeCounter);
    }
    
    private class StudentBoard extends WhiteBoard {
        

        public StudentBoard() {}
        
        @Override
        public void paint(Graphics g) {
          Graphics2D g2d = (Graphics2D) g;
          g2d.setStroke(new BasicStroke(2));

          for (Shape shape : shapes) {
        	  g2d.setPaint(Color.BLACK);
        	  g2d.draw(shape);
        	  g2d.setPaint(Color.YELLOW);
        	  g2d.fill(shape);
          }
        }
        
      }
    
    private void handActionPerformed(ActionEvent evt) {                                       
        
    	
    	try {
    		if(connectedToServer) {
	    		NetworkData data = new NetworkData(hand.isSelected());
				os.writeObject(data);
				os.flush();
    		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    
    private void msg_sendActionPerformed(ActionEvent evt) {                                         
        try {
        	if(connectedToServer) {
        		String msgout = "";
            	msgout = msg_text.getText().trim();
            	msg_area.append("Student:\t" + msgout+"\n");
            	NetworkData data = new NetworkData(msgout);
            		
            	os.writeObject(data);
            	os.flush();

            	msg_text.setText("");
        	}
        }catch (Exception e) {
		}
    }         
    
    

    public static void main(String args[]) {

    
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    private Timer timer;
    private int timeLeft;
    

	private ArrayList<Shape> shapes;  
    
	
	static Socket s;
	static ObjectOutputStream os;
	static ObjectInputStream is;
	
	static boolean connectedToServer;
    
	private JPanel panel;
	private JMenuBar Menu_Bar;
	private JLabel board_area;
	private JTextArea msg_area;
	private JButton msg_send;
	private JTextField msg_text;
	private JLabel time_label;
	private WhiteBoard whiteBoard;
	private JCheckBox hand;
	private JLabel shapeCounter;
                     
}
