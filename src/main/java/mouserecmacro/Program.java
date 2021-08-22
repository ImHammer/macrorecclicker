package mouserecmacro;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

public class Program implements NativeKeyListener, NativeMouseListener
{
	
	static Program instance;
	
	public static Program program;
	public static Configurations configurations;
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		
		
		configurations = new Configurations();
		program = new Program();
		
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GlobalScreen.addNativeKeyListener(program);
		GlobalScreen.addNativeMouseListener(program);
		
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);
	}
	
	public static boolean IN_TIMER = false;
	public static MacroTimer macroTimer;
	
	public boolean needSave = false;
	public boolean recMouse = false;
	public boolean selectKey = false;
	
	public JFrame jFrame;
	public JPanel panel;
	public JTextField macroTotalTimeField;
	public JButton clearButton, recButton, saveButton, changeKeyButton;
	public JLabel recButtonLabel, saveButtonLabel, changeKeyButtonLabel, macroTotalTimeLabel;
	
	public ArrayList<Integer> NEW_MOUSE_POSITIONS_BUTTON = new ArrayList<Integer>();
	public ArrayList<HashMap<String, Integer>> NEW_MOUSE_POSITIONS = new ArrayList<HashMap<String, Integer>>();
	
	private int lastMousePositionButton = -1;
	private int lastMousePositionX = -1;
	private int lastMousePositionY = -1;
	
	public Program() {
		instance = this;
		
		initInterface();
		jFrame.setVisible(true);
	}
	
	public static Program getInstance() {
		return instance;
	}
	
	public void initInterface() {
		jFrame = new JFrame();
		jFrame.addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			public void windowIconified(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			public void windowDeiconified(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			public void windowDeactivated(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			public void windowClosing(WindowEvent e)
			{
				// TODO Auto-generated method stub
				Program.configurations.save();
			}
			
			public void windowClosed(WindowEvent e)
			{
				// TODO Auto-generated method stub
				Program.configurations.save();
			}
			
			public void windowActivated(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		macroTotalTimeField = new JTextField("Miliseconds");
		macroTotalTimeField.setDocument(new JTextFieldNoText(5));
		macroTotalTimeField.setText(String.valueOf(Program.configurations.getMacroTotalTime()));
		macroTotalTimeField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e)
			{
				// TODO Auto-generated method stub
				saveNewMacroTime();
			}
		});
		
		macroTotalTimeLabel = new JLabel();
		macroTotalTimeLabel.setText("Time to complete the action");
		macroTotalTimeLabel.setFont(new Font(Font.SANS_SERIF, 13, 13));
		
		panel = new JPanel();
		panel.setBackground(new Color(210, 210, 210));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setLayout(new GridLayout(4, 2));
		((GridLayout) panel.getLayout()).setHgap(10);
		((GridLayout) panel.getLayout()).setVgap(10);
		
		clearButton = new JButton("Clear");
		recButton = new JButton("Rec Macro");
		saveButton = new JButton("Save");
		changeKeyButton = new JButton("Change");
		
		clearButton.setEnabled(true);
		clearButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				clearActions();
			}
		});
		
		recButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				toggleRecMouse();
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				saveChanges();
			}
		});
		
		changeKeyButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				toggleChangeKey();
			}
		});
		
		recButtonLabel = new JLabel();
		recButtonLabel.setText("Count of actions: " + Program.configurations.getConvertedMousePositions().size());
		recButtonLabel.setFont(new Font(Font.SANS_SERIF, 13, 13));

		saveButtonLabel = new JLabel();
		saveButtonLabel.setText("No changes");
		saveButtonLabel.setFont(new Font(Font.SANS_SERIF, 13, 13));

		changeKeyButtonLabel = new JLabel();
		changeKeyButtonLabel.setText("Current key: " + NativeKeyEvent.getKeyText(Program.configurations.getMacroKey()));
		changeKeyButtonLabel.setFont(new Font(Font.SANS_SERIF, 13, 13));
		
		JPanel buttonsJPanel = new JPanel();
		buttonsJPanel.setBackground(new Color(210, 210, 210));
		buttonsJPanel.setLayout(new GridLayout(1, 2));
		((GridLayout) buttonsJPanel.getLayout()).setHgap(5);
		
		buttonsJPanel.add(clearButton);
		buttonsJPanel.add(recButton);
		
		panel.add(macroTotalTimeField);
		panel.add(macroTotalTimeLabel);
		panel.add(buttonsJPanel);
		panel.add(recButtonLabel);
		panel.add(saveButton);
		panel.add(saveButtonLabel);
		panel.add(changeKeyButton);
		panel.add(changeKeyButtonLabel);
		
		JLabel bannerLabel = new JLabel();
		bannerLabel.setSize(100, 200);
		bannerLabel.setHorizontalAlignment(0);
		bannerLabel.setIcon(new ImageIcon(Program.class.getResource("/resources/banner3Splited.png")));
		bannerLabel.setToolTipText("");
		bannerLabel.setCursor(new Cursor(0));
		bannerLabel.setHorizontalTextPosition(0);
		
		jFrame.setLayout(new GridLayout(2, 1));
		
		jFrame.add(bannerLabel);
		jFrame.add(panel);
		
		jFrame.setTitle("MacroRecMouse");
		jFrame.setResizable(false);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
		jFrame.pack();
		
		jFrame.getContentPane().setBackground(new Color(210, 210, 210));
		
		jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(Program.class.getResource("/resources/main.png")));
		
		this.updateNeedChanges();
	}
	
	public void toggleChangeKey() {
		this.changeKeyButton.setEnabled(false);
		this.changeKeyButton.setText("Press Key");
		this.selectKey = true;
	}
	
	public void updateNeedChanges() {
		if (this.needSave) {
			this.saveButton.setEnabled(true);
			this.saveButtonLabel.setText("Has changes");
		} else {
			this.saveButton.setEnabled(false);
			this.saveButtonLabel.setText("No changes");
		}
	}
	
	public void hasChanges() {
		this.needSave = true;
		this.updateNeedChanges();
	}
	
	public void saveChanges() {
		
		if (!needSave) {
			return;
		}
		
		if (!this.macroTotalTimeField.getText().isEmpty()) {
			int macroTime = Integer.valueOf(this.macroTotalTimeField.getText());
			
			if (macroTime < 1) {
				macroTime = 1;
			} else if (macroTime > 60000) {
				macroTime = 60000; 
			}
			Program.configurations.setMacroTotalTime(macroTime);
		}
		
		Program.configurations.save();

		this.clearButton.setEnabled(true);
		
		this.needSave = false;
		this.reloadLabels();
	}
	
	public void saveNewMacroTime() {
		if (this.macroTotalTimeField.getText().isEmpty()) {
			return;
		}
		
		this.hasChanges();
	}
	
	public void toggleRecMouse() {
		this.recMouse = true;
		
		this.recButton.setEnabled(false);
		this.clearButton.setEnabled(false);
		this.saveButton.setEnabled(false);
		this.changeKeyButton.setEnabled(false);
		this.macroTotalTimeField.setEnabled(false);
	}
	
	public void saveMouseRec() {
		this.recMouse = false;
		
		this.recButton.setEnabled(true);
		this.clearButton.setEnabled(false);
		this.saveButton.setEnabled(true);
		this.changeKeyButton.setEnabled(true);
		this.macroTotalTimeField.setEnabled(true);
		
		Program.configurations.setNewMousePositionsButton(NEW_MOUSE_POSITIONS_BUTTON, NEW_MOUSE_POSITIONS);
		
		NEW_MOUSE_POSITIONS_BUTTON.clear();
		NEW_MOUSE_POSITIONS.clear();
		
		this.hasChanges();
	}
	
	public void clearActions() {
		NEW_MOUSE_POSITIONS_BUTTON.clear();
		NEW_MOUSE_POSITIONS.clear();
		Program.configurations.clearMousePositions();
		
		this.reloadLabels();
	}
	
	public void reloadLabels() {
		this.updateNeedChanges();
		this.recButtonLabel.setText("Count of actions: " + Program.configurations.getConvertedMousePositions().size());
	}
	
	public void cancelRec() {
		NEW_MOUSE_POSITIONS_BUTTON.clear();
		NEW_MOUSE_POSITIONS.clear();
		
		this.recMouse = false;
		
		this.recButton.setEnabled(true);
		this.clearButton.setEnabled(true);
		this.saveButton.setEnabled(true);
		this.changeKeyButton.setEnabled(true);
		this.macroTotalTimeField.setEnabled(true);
		
		this.reloadLabels();
	}
	
	public void addMousePosition(int mousePositionButton, int mousePositionX, int mousePositionY) {
		
		HashMap<String, Integer> mousePositionHashMap = new HashMap<String, Integer>();
		mousePositionHashMap.put("x", mousePositionX);
		mousePositionHashMap.put("y", mousePositionY);
		
		NEW_MOUSE_POSITIONS_BUTTON.add(mousePositionButton);
		NEW_MOUSE_POSITIONS.add(mousePositionHashMap);
		
		this.lastMousePositionButton = mousePositionButton;
		this.lastMousePositionX = mousePositionX;
		this.lastMousePositionY = mousePositionY;
		
		System.out.println("LAST POSITION: " + mousePositionButton + " - " + mousePositionX + " " + mousePositionY);
		
		this.recButtonLabel.setText("Count of add: " + NEW_MOUSE_POSITIONS.size());
	}

	public void nativeMouseClicked(NativeMouseEvent nativeEvent)
	{
		// TODO Auto-generated method stub
	}

	public void nativeMousePressed(NativeMouseEvent nativeEvent)
	{
		// TODO Auto-generated method stub
		
		if (this.recMouse && !IN_TIMER) {
			this.addMousePosition(nativeEvent.getButton(), nativeEvent.getX(), nativeEvent.getY());			
		}
	}

	public void nativeMouseReleased(NativeMouseEvent nativeEvent)
	{
		// TODO Auto-generated method stub
	}

	public void nativeKeyTyped(NativeKeyEvent nativeEvent)
	{
		// TODO Auto-generated method stub
	}

	public void nativeKeyPressed(NativeKeyEvent nativeEvent)
	{
		// TODO Auto-generated method stub
		int selectedKeyCode = nativeEvent.getKeyCode();
		
		if (this.recMouse) {
			if (selectedKeyCode == Program.configurations.getMacroKey()) {
				this.saveMouseRec();
			} else if (selectedKeyCode == NativeKeyEvent.VC_C) {
				this.cancelRec();
			} else if (selectedKeyCode == NativeKeyEvent.VC_0) {
				for (int i = 0; i < 10; i++) {
					this.addMousePosition(this.lastMousePositionButton, this.lastMousePositionX, this.lastMousePositionY);
				}
			}
		} else if (this.selectKey) {
			this.selectKey = false;
			this.changeKeyButton.setEnabled(true);
			this.changeKeyButtonLabel.setText("Current key: " + NativeKeyEvent.getKeyText(selectedKeyCode));
			
			Program.configurations.setMacroKey(selectedKeyCode);
			hasChanges();
		} else if (selectedKeyCode == Program.configurations.getMacroKey()) {
			// EXECUTE PROGRAM
			if (!IN_TIMER) {
				System.out.println("EXECTION STARTED...");
				IN_TIMER = true;
				
				macroTimer = new MacroTimer(
					Program.configurations.getConvertedMousePositions(),
					Program.configurations.getMacroTotalTime()
				);
				
				this.macroTotalTimeField.setEnabled(false);
				this.recButton.setEnabled(false);
				this.saveButton.setEnabled(false);
				this.clearButton.setEnabled(false);
				this.changeKeyButton.setEnabled(false);
				
			} else {
				if (macroTimer != null) {
					macroTimer.stop();
					System.out.println("EXEUTION STOPED...");
					
					this.stopMacro();
				}
				IN_TIMER = false;
			}
		}
	}

	public void nativeKeyReleased(NativeKeyEvent nativeEvent)
	{
		// TODO Auto-generated method stub
		
	}
	
	public static void actualTimerFinished() {
		if (macroTimer != null) {
			macroTimer = null;					
		}
		IN_TIMER = false;
		
		Program.getInstance().stopMacro();
		System.out.println("EXECUTION FINISHED...");
	}
	
	public void stopMacro() {
		this.macroTotalTimeField.setEnabled(true);
		this.clearButton.setEnabled(true);
		this.recButton.setEnabled(true);
		this.saveButton.setEnabled(true);
		this.changeKeyButton.setEnabled(true);
	}
}
