package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class CustomFileChooser extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	Vector<String> filenames;
	String mainAction;
	private String selectedFile;
	JTextField fileInput;
	
	CustomFileChooser(JFrame parent, String mainAction, Vector<String> filenames, String initialFile) {
		super(parent,"File Chooser",true);
		setPreferredSize(new Dimension(500,300));

		this.mainAction = mainAction;
	    if (parent != null) {
	        Dimension parentSize = parent.getSize(); 
	        Point p = parent.getLocation(); 
	        setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
	      }
	      JPanel messagePane = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
	      messagePane.add(new JLabel("Select a file:"));

	      // JList
	      JList<String> fileList = new JList<String>(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getElementAt(int position) {
				return filenames.get(position);
			}

			@Override
			public int getSize() {
				return filenames.size();
			} 
	    	  
	      });
	      
	      fileList.setFixedCellWidth(-1);
	      fileList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)  {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				Graphics g = GUIController.tabbed_pane.getGraphics();
				FontMetrics fm = g.getFontMetrics(GUIController.DEFAULT_FONT);
				c.setPreferredSize(new Dimension(fm.stringWidth(filenames.get(index))+10,fm.getHeight()));
				return c;
			}
	    	  
	      });
	      fileList.setLayoutOrientation(JList.VERTICAL_WRAP);
	      fileList.setVisibleRowCount((filenames.size()/4)+1);
	      fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	      
	      fileList.setBorder(new LineBorder(Color.GRAY));
	      
	      JPanel mainPanel = new JPanel(new BorderLayout());
	      
	      mainPanel.add(messagePane,BorderLayout.NORTH);
	      mainPanel.add(fileList, BorderLayout.CENTER);
	      getContentPane().add(mainPanel, BorderLayout.CENTER);
	      
	      JPanel bottomPanel = new JPanel(new BorderLayout(0,10));
	      bottomPanel.add(new JLabel("File:"),BorderLayout.WEST);
	      
	      fileInput = new JTextField();	      
	      if(initialFile != null) {
	    	  fileInput.setText(initialFile);
	      }
	      bottomPanel.add(fileInput,BorderLayout.CENTER);
	      
	      mainPanel.add(bottomPanel,BorderLayout.SOUTH);
	      
	      // Buttons
	      JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
	      
	      JButton mainButton = new JButton(mainAction); 
	      mainButton.addActionListener(this);
	      
	      JButton cancelButton = new JButton("Cancel");
	      cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				CustomFileChooser.this.setVisible(false);
				CustomFileChooser.this.dispose();
			}
	    	  
	      });
	      
	      buttonPane.add(cancelButton);
	      JPanel border = new JPanel();
	      border.setBorder(new EmptyBorder(2,10,2,5));
	      buttonPane.add(border);
	      buttonPane.add(mainButton); 
	      	      
	      bottomPanel.add(buttonPane, BorderLayout.SOUTH);
	      
	      bottomPanel.setBorder(new EmptyBorder(13,0,0,0));
	      mainPanel.setBorder(new EmptyBorder(8,8,8,8));
	      
	      fileList.addMouseListener(new MouseAdapter() {
	    	  
		      public void mouseClicked(MouseEvent mouseEvent) {
		    	  if (mouseEvent.getClickCount() == 2) {
		    		  int index = fileList.locationToIndex(mouseEvent.getPoint());
		    		  if (index >= 0) {
		    			  Object o = fileList.getModel().getElementAt(index);
		    			  fileInput.setText(o.toString());
		    		  }
		    	  }
		      	}
		    });
	      
	      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	      pack(); 
	      
	      mainButton.requestFocusInWindow();
	      mainButton.requestFocus();
	      
	      setVisible(true);


	}
	
	public String getSelectedFile() {
		return selectedFile;
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		selectedFile = fileInput.getText();
		setVisible(false);
		dispose();
	}
	

}
