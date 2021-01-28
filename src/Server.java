import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;



public class Server extends JFrame {


    public Server() {
    	
    	initMemberVariables();
        initComponents();
        initSocketConnection();

        
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Classroom disposed, attendance list writing to file...");
                
                FileWriter myWriter;
				try {
					myWriter = new FileWriter("Attendance.txt");
					myWriter.write("Attendance List: \n");
					for(String student : attendanceList) {
						myWriter.write(student + "\n");
					}
	                myWriter.close();
	                System.out.println("Successfully wrote to the file.");
				} catch (IOException e1) {
					e1.printStackTrace();
				}

                
                e.getWindow().dispose();
            }
        });
        
        
    }
    private void initMemberVariables() {
    	timeLeft = 80000; // 80 1dk 10sn
    	attendanceList = new ArrayList<String>();
    	currentShapeType = ShapeType.CIRCLE;
    	shapes = new ArrayList<Shape>();
    	isClientConnected = false;
    }
    

    private void initSocketConnection() {
    	
    	new Thread(() -> {
    		int id = 0;
            try {
            	
            	ss = new ServerSocket(1203);
            	s = ss.accept();
            	isClientConnected = true;
            	timer.start();
            	attendanceList.add("Student " + ++id);
            	os = new ObjectOutputStream(s.getOutputStream());
            	is = new ObjectInputStream(s.getInputStream());
            	

            	while(true) {
                	NetworkData data = (NetworkData)is.readObject();
                	switch (data.selection){
						case MESSAGE:
							msg_area.append("Student:\t" + data.msg+"\n");
							break;
						case HAND:
							hand.setBackground( data.riseHand ? Color.GREEN : Color.LIGHT_GRAY);
							
							break;
						
							
					default:
						break;
					}
            	}

            	
            }catch (Exception e) {
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
        shapeMenu = new JMenu();
        rectangle = new JMenuItem();
        circle = new JMenuItem();
        line = new JMenuItem();
        attendance = new JMenuItem();
        optionMenu = new JMenu();
        hand = new JLabel();
        handText = new JLabel();
        shapeCounter = new JLabel();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(620,500);
        
        // MAIN PANEL
        panel.setLayout(null);
        this.add(panel);
        

        initMsgAreaComponent();

        initMsgTextComponent();
        
        initMsgSendComponent();
        
        initBoardAreaComponent();
        
        initHandComponent();
        
        initTimerComponent();
        
        initMenuComponent();
        
        initShapeCounterComponent();
        
        

        
    }  
    
    void initMsgAreaComponent() {
        // MESSAGE AREA
    	msg_area.setBounds(350, 20, 230, 310);
        msg_area.setEditable(false);
//        msg_area.setBorder(new LineBorder(Color.BLACK));
        
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
        hand.setBounds(20,381,10,10);
        hand.setOpaque(true);
        hand.setBackground(Color.LIGHT_GRAY);
        panel.add(hand);
        
        handText.setBounds(35,380,40,10);
        handText.setText("Hand");
        panel.add(handText);
        
    }
    
    void initBoardAreaComponent() {
        // BOARD AREA
        board_area.setBounds(20,20,310,350);
        board_area.setLayout(new BorderLayout());
        board_area.setBackground(new Color(255,255,255));
        board_area.setForeground(new Color(255,255,255));
        board_area.setOpaque(true);
        board_area.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        board_area.add(new TeacherBoard());
        panel.add(board_area);
    }
    
    void initTimerComponent() {
    	
        timer=new Timer(100, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                timeLeft -= 100;
                SimpleDateFormat df =new SimpleDateFormat("mm:ss");
                time_label.setText(df.format(timeLeft));
                if(timeLeft<=0)
                {
                	JOptionPane.showMessageDialog(rootPane, "Time is over", "Lesson is dismissed ",0);
                    timer.stop();
                }
            }
        });
        
        timer.setInitialDelay(0);
        //timer.start();
        
        time_label.setBounds(545,380,60,20);
        panel.add(time_label);
        
    }
    
    void initMenuComponent() {
    	// MENU (SHAPES) AREA
    	
        shapeMenu.setText("Shapes");

        rectangle.setText("Rectangle");
        rectangle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rectangleActionPerformed(evt);
            }
        });
        
        
        shapeMenu.add(rectangle);

        circle.setText("Circle");
        circle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                circleActionPerformed(evt);
            }
        });
        shapeMenu.add(circle);

        line.setText("Line");
        line.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                lineActionPerformed(evt);
            }
        });
        shapeMenu.add(line);
        Menu_Bar.add(shapeMenu);

        // MENU (SHAPES) AREA
        optionMenu.setText("Options");
        
        attendance.setText("Attendance");
        attendance.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	attendanceActionPerformed(evt);
            }
        });
        		
        optionMenu.add(attendance);
        Menu_Bar.add(optionMenu);

        setJMenuBar(Menu_Bar);
    	
    }
    
    void initShapeCounterComponent() {
    	shapeCounter.setBounds(20,400,120,20);
    	shapeCounter.setText("Shape Count : 0");
    	panel.add(shapeCounter);
    }
    
    private class TeacherBoard extends WhiteBoard {
        

        Point start, end;

        public TeacherBoard() {
          this.addMouseListener(new MouseAdapter() {
            
        	public void mousePressed(MouseEvent event) {
            	start = new Point(event.getX(), event.getY());
	        	end = start;
	        	repaint();
            }

            public void mouseReleased(MouseEvent e) {

              shapes.add(getShape());
              shapeCounter.setText("Shape Count : " + shapes.size());
              start = null;
              end = null;
              
              if(isClientConnected) {
            	  sendData();            	  
              }
             
              repaint();
            }
          });

          this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent event) {
              end = new Point(event.getX(), event.getY());
              repaint();
            }
          });
        }

        public void paint(Graphics g) {
          Graphics2D g2d = (Graphics2D) g;
      
          g2d.setStroke(new BasicStroke(2));

          for (Shape shape : shapes) {
        	  g2d.setPaint(Color.BLACK);
        	  g2d.draw(shape);
        	  g2d.setPaint(Color.YELLOW);
        	  g2d.fill(shape);
          }
           
          if (start != null && end != null) {
        	  g2d.setPaint(Color.LIGHT_GRAY);
        	  g2d.draw(getShape());
          }
        }
        
        Shape getShape() {
        	
        	Shape shape = null;
        	
        	switch (currentShapeType) {
				case CIRCLE:
					shape = createEllipse(start.x, start.y, end.x, end.y);					
					break;
				case RECTANGLE:
					shape = createRectangle(start.x, start.y, end.x, end.y);
					break;
				case LINE:
					shape = createLine(start.x, start.y, end.x, end.y);
					break;
					
				default:
					shape = createRectangle(start.x, start.y, end.x, end.y);
					break;
			}
        	
        	return shape;
        }
        
        void sendData() {
        	try {
        		
        		NetworkData data = new NetworkData(shapes.get(shapes.size()-1));
				os.writeObject(data);
				os.flush();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        private Rectangle2D.Float createRectangle(int x1, int y1, int x2, int y2) {
          return new Rectangle2D.Float( Math.min(x1, x2), Math.min(y1, y2),
        		  						Math.abs(x1 - x2), Math.abs(y1 - y2));
        }
        
        private Ellipse2D.Float createEllipse(int x1, int y1, int x2, int y2) {
        	return new Ellipse2D.Float(Math.min(x1, x2), Math.min(y1, y2), 
        							   Math.abs(x1 - x2), Math.abs(y1 - y2));
        }
        
        private Line2D.Float createLine(int x1, int y1, int x2, int y2) {
        	return new Line2D.Float(x1, y1, x2, y2);
        }
        
        
      }
    
    private void rectangleActionPerformed(ActionEvent evt) {                                          
        
    	currentShapeType = ShapeType.RECTANGLE;
    }                                         

    private void circleActionPerformed(ActionEvent evt) {                                       
        
    	currentShapeType = ShapeType.CIRCLE;
    }                                      

    private void lineActionPerformed(ActionEvent evt) {                                       
        
    	currentShapeType = ShapeType.LINE;
    }         

    private void attendanceActionPerformed(ActionEvent evt) {                                       
  
    	String formattedList = "";
    	for(String student : attendanceList) {
    		formattedList = formattedList + student +"\n";
    	}
    	
    	JOptionPane.showMessageDialog(rootPane, formattedList, "Attendance List", 1);    
    	
        
    }  
    
   

    
    private void msg_sendActionPerformed(ActionEvent evt) {                                         
        try {
        	String msgout = "";
        	msgout = msg_text.getText().trim();
        	msg_area.append("Teacher:\t" + msgout+"\n");
        	NetworkData data = new NetworkData(msgout);
        		
        	os.writeObject(data);
        	os.flush();

        	msg_text.setText("");
        			
        }catch (Exception e) {
        	
		}
    }         
    
    

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    static Timer timer;
    private int timeLeft; //7 seconds
    private  ArrayList<String> attendanceList;
    
    
    private enum ShapeType {
  	  RECTANGLE,
  	  CIRCLE,
  	  LINE
  	}
  
	private ShapeType currentShapeType;
	private ArrayList<Shape> shapes;  
    
	static ServerSocket ss;
	static Socket s;
	static ObjectOutputStream os;
	static ObjectInputStream is;
	
	static boolean isClientConnected;
    
	private JPanel panel;
	private JMenu shapeMenu;
	private JMenuBar Menu_Bar;
	private JMenu optionMenu;
	private JLabel board_area;
	private JMenuItem circle;
	private JMenuItem line;
	private JTextArea msg_area;
	private JButton msg_send;
	private JTextField msg_text;
	private JMenuItem rectangle;
	private JMenuItem attendance;
	private JLabel time_label;
	private WhiteBoard whiteBoard;
	private JLabel hand;
	private JLabel handText;
	private JLabel shapeCounter;
                     
}
