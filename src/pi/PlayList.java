package pi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;


public class PlayList extends JFrame implements ActionListener, KeyListener{
	
	private static final long serialVersionUID = -4663798205944530771L;
	private	JPanel	topPanel = new JPanel();
	private	JPanel	buttons  = new JPanel();
	private JButton openB  = new JButton("Load");
	private JButton clearB = new JButton("Clear");
	private JButton playB  = new JButton("Play");
	private JButton pauseB = new JButton("Pause");
	private DefaultListModel<String> myListModel = new DefaultListModel<String>();
	private	JList<String> listbox  = new JList<String>(myListModel);
	private final JFileChooser fc  = new JFileChooser();
	private Process p;
	private ProcessBuilder builder; 


	// Constructor of main frame
	public PlayList()
	{
		// Set the frame characteristics
		setTitle( "Pi-Playlist" );
		setSize( 300, 100 );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.lightGray);
		setPreferredSize(new Dimension(600, 400));
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		// Create a panel to hold all other components
		
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);
		
		MyMouseAdaptor mma = new MyMouseAdaptor();
		listbox.addMouseListener(mma);
		listbox.addMouseMotionListener(mma);
		listbox.addKeyListener(this);
		listbox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//listbox.setDragEnabled(true);
		topPanel.add(new JScrollPane(listbox), BorderLayout.CENTER);
		buttons.setLayout(new FlowLayout());
		openB.addActionListener(this);
		clearB.addActionListener(this);
		playB.addActionListener(this);
		pauseB.addActionListener(this);
		buttons.add(openB);
		buttons.add(playB);
		buttons.add(pauseB);
		buttons.add(clearB);		
		topPanel.add(buttons, BorderLayout.SOUTH);
		pack();
	}
	
	private void volume (boolean up){
		try {
			if (p != null){
			    OutputStream stdin = p.getOutputStream(); 
			    String x = (up) ? "+\n" : "-\n";
			    stdin.write(x.getBytes());
			    stdin.flush();
			    //stdin.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	private void stopIt(){ 
		if (p != null)
			p.destroy(); 
	}

    private void playIt(boolean pause){
    	String path = selection();
        String[] omxCommand = {"omxplayer", path}; 
        // PLAY item using full omxCommand
        try {
        	if (pause){
        		if (p != null){
        			OutputStream stdin = p.getOutputStream(); 
        		    String space = "\\s\n"; 
        		    stdin.write(space.getBytes()); 
        		    stdin.flush(); 
        		    //stdin.close();
        		   //p.waitFor();
        		}
        	}
        	else{
        		builder  = new ProcessBuilder(omxCommand);
        		builder.redirectErrorStream(true);
        	    p = builder.start();	
			//p = Runtime.getRuntime().exec(omxCommand);
			//p.waitFor();
        	}
		} catch (IOException e) {
			e.printStackTrace();
			}
    }

    
    public void appendPaths(File... fs){
    	try {
    	    for (File f : fs)
			   myListModel.addElement(f.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
				}
    }
	
	public String selection(){
		return listbox.getSelectedValue();		
	}
	
	private void clearAll(){
		myListModel.clear();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openB){
			int v = fc.showOpenDialog(this);
			
			if (v == JFileChooser.APPROVE_OPTION) {
	            File temp = fc.getSelectedFile();
	            if (temp.isDirectory())
					appendPaths(temp.listFiles());
	            else
					appendPaths(temp);
	        } 
		}
		else if (e.getSource() == playB)  playIt(false);
		else if (e.getSource() == pauseB) playIt(true);
		else if (e.getSource() == clearB) clearAll();
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		int kcode = e.getKeyCode();
		int sel = listbox.getSelectedIndex();
		if (kcode ==  KeyEvent.VK_DELETE)
			myListModel.remove(sel);
		else if (kcode ==  KeyEvent.VK_ENTER)
			playIt(false);
		else if (kcode ==  KeyEvent.VK_SPACE)
			playIt(true);
		else if (kcode ==  KeyEvent.VK_Q)
			stopIt();
		else if (kcode ==  KeyEvent.VK_PLUS)
			volume(true);
		else if (kcode ==  KeyEvent.VK_MINUS)
			volume(false);
		else if (kcode ==  KeyEvent.VK_UP)
			listbox.setSelectedIndex(sel + 1);
		else if (kcode ==  KeyEvent.VK_DOWN)
			listbox.setSelectedIndex(sel - 1);
		
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}  
	
	private class MyMouseAdaptor extends MouseInputAdapter {
        private boolean mouseDragging = false;
        private int dragSourceIndex;

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                dragSourceIndex = listbox.locationToIndex(e.getPoint());
                mouseDragging = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseDragging = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (mouseDragging) {
                int currentIndex =  listbox.locationToIndex(e.getPoint());
                if (currentIndex != dragSourceIndex) {
                    int dragTargetIndex = listbox.getSelectedIndex();
                    String dragElement = myListModel.getElementAt(dragSourceIndex);
                    myListModel.remove(dragSourceIndex);
                    myListModel.add(dragTargetIndex, dragElement);
                    dragSourceIndex = currentIndex;    
                }
            }
        }
        @Override
    	public void mouseClicked(MouseEvent evt) {
    		int clicks = evt.getClickCount();
            if (clicks == 2 || clicks == 3)  //double or triple click? 
               playIt(false); 
    	} 
    }

}
