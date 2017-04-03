import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.*;

/**
 * This class provides a GUI for the game of Big Two. It allows users to choose cards to play with mouse
 * clicks. Users can play the cards selected or pass their turn with corresponding buttons. There is also
 * a menu bar providing restart and quit options for the game. 
 * @author Yin
 *
 */
public class BigTwoTable implements CardGameTable {
	//Private Variables
	private BigTwoClient game;
	private boolean[] selected;		//indicate which card is selected
	private int activePlayer;
	private JFrame frame;
	private JPanel bigTwoPanel;
	private JButton playButton;
	private JButton passButton;
	private JLabel chatLabel;
	private JTextField chatOut;
	private JTextArea msgArea;
	private JTextArea chatIn;
	private Image[][] cardImages;	//faces of cards
	private Image cardBackImages;
	private Image[] avatars;
	
	/**
	 * Constructor
	 * Initializes the GUI. Initializing the instance variables and adding all the components that does 
	 * not require further repaints or updates into the frame.
	 * @param game
	 */
	public BigTwoTable(BigTwoClient game){
		this.game = game;
		selected = new boolean[13];
		
		frame = new JFrame();
		frame.setTitle("Big Two - Waiting for connection");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		bigTwoPanel = new BigTwoPanel();
		frame.add(bigTwoPanel, BorderLayout.CENTER);

		//Play, Pass Buttons and Chat text field
		playButton = new JButton("Play");
		playButton.addActionListener(new PlayButtonListener());

		passButton = new JButton("Pass");
		passButton.addActionListener(new PassButtonListener());
		
		chatLabel = new JLabel("Message: ");
		chatLabel.setForeground(Color.WHITE);
		
		chatOut = new JTextField(30);
		chatOut.setText("Press Enter to send message");
		chatOut.addActionListener(new ChatListener());
		
		JPanel buttons = new JPanel();
		JPanel chatComp = new JPanel();
		JPanel container = new JPanel();
		
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(playButton);
		buttons.add(Box.createRigidArea(new Dimension(20, 0)));
		buttons.add(passButton);
		buttons.setBackground(Color.DARK_GRAY);
		
		chatComp.setLayout(new BoxLayout(chatComp, BoxLayout.X_AXIS));
		chatComp.setBackground(Color.DARK_GRAY);
		chatComp.add(chatLabel);
		chatComp.add(chatOut);
		
		container.setBackground(Color.DARK_GRAY);
		container.setAlignmentX(Component.CENTER_ALIGNMENT);
		container.add(buttons);
		container.add(chatComp);
		
		frame.add(container, BorderLayout.SOUTH);
		
		//Text Area
		JPanel textPanel = new JPanel();
		textPanel.setBackground(Color.DARK_GRAY);
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		msgArea = new JTextArea(15, 40);
		clearMsgArea();
		JScrollPane scrollerMsg = new JScrollPane(msgArea);
		msgArea.setBackground(Color.DARK_GRAY);
		msgArea.setLineWrap(true);
		Font msgText = new Font("Consolas", Font.PLAIN, 16);
		msgArea.setFont(msgText);
		msgArea.setForeground(Color.WHITE);
		scrollerMsg.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollerMsg.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		textPanel.add(scrollerMsg);

		//chatArea Area
		chatIn = new JTextArea(15, 40);
		clearChatIn();
		JScrollPane scrollerChatIn = new JScrollPane(chatIn);
		chatIn.setBackground(Color.DARK_GRAY);
		chatIn.setLineWrap(true);
		chatIn.setFont(msgText);
		chatIn.setForeground(Color.YELLOW);
		chatIn.setText("Messages:\n");
		scrollerChatIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollerChatIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		textPanel.add(scrollerChatIn);
		frame.add(textPanel, BorderLayout.EAST);
		
		//card images
		char[] suit = {'d','c','h','s'};
		char[] rank = {'a','2','3', '4', '5', '6', '7', '8', '9', 't', 'j', 'q', 'k'};
		cardImages = new Image[suit.length][rank.length];
		for(int i = 0; i < suit.length; i++){
			for(int j = 0; j < rank.length; j++){
				cardImages[i][j] = new ImageIcon("img/"+rank[j]+suit[i]+".gif").getImage();
			};
		};
		cardBackImages = new ImageIcon("img/b.gif").getImage();
		avatars = new Image[4];
		for(int i = 0; i < 4; i++){
			avatars[i] = new ImageIcon("img/"+i+".png").getImage();
		};
		
		//Menu Bar
		JMenuBar bar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem connect = new JMenuItem("Connect");
		connect.addActionListener(new ConnectMenuListener());
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new QuitMenuListener());
		gameMenu.add(connect);
		gameMenu.add(quit);
		bar.add(gameMenu);
		frame.setJMenuBar(bar);
		
		frame.setMinimumSize(new Dimension(1200, 700));
		frame.setSize(1316, 720);		//Best viewed resolution (Minimum  for proper display: 1200:700)
		frame.setVisible(true);
	}
	
	/**
	 * Setter of the instance variable activePlayer, i.e. the current turn's player.
	 */
	@Override
	public void setActivePlayer(int activePlayer) {
		// TODO Auto-generated method stub
		this.activePlayer = activePlayer;
	}

	/**
	 * Getter of the selected[] array. It transforms the boolean array into an int array that only
	 * contains index of selected cards.
	 */
	@Override
	public int[] getSelected() {
		int size = 0;
		for(int i = 0; i < selected.length; i++){
			if(selected[i]){
				size++;
			};
		};
		int[] intSelected = new int[size];
		int j = 0;
		for(int i = 0; i < selected.length; i++){
			if(selected[i]){
				intSelected[j] = i;
				j++;
			};
		};
		if(size == 0){
			return null;
		}else{
			return intSelected;
		}
	}

	/**
	 * Resets the selected[] array by setting all elements to be false.
	 */
	@Override
	public void resetSelected() {
		for(int i = 0; i < selected.length; i++){
			selected[i] = false;
		};
	}

	/**
	 * Repaints the private JPanel bigTwoPanel (the only component that requires repainting)
	 */
	@Override
	public synchronized void repaint() {
		frame.setTitle("Big Two - " + game.getPlayerList().get(activePlayer).getName());
		
		//Main panel
		if(game.getCurrentIdx() != activePlayer){
			disable();
		}else{
			enable();
		};
		frame.repaint();
	}

	/**
	 * Print message to the message area (msgArea) by appending the message to the end of the area.
	 * @param msg
	 */
	@Override
	public void printMsg(String msg) {
		// TODO Auto-generated method stub
		msgArea.append(msg);
	}
	
	/**
	 * Print message to the message area (msgArea) by appending the message to the end of the area with line
	 * break behind.
	 * @param msg
	 */
	public void printMsgln(String msg) {
		// TODO Auto-generated method stub
		msgArea.append(msg + "\n");
	}
	
	/**
	 * Print chat message to the chat area (chatArea) by appending the message to the end of the area.
	 * @param msg
	 */
	public void printChat(String msg){
		chatIn.append(msg + "\n");
	};
	
	/**
	 * Clear all contents in msgArea by setting the content to an empty string.
	 */
	@Override 
	public void clearMsgArea() {
		// TODO Auto-generated method stub
		msgArea.setText("");
	}

	/**
	 * Clear all contents in chatArea by setting the content to an empty string.
	 */
	public void clearChatIn(){
		chatIn.setText("");
	};
	
	/**
	 * Reset the GUI by resetting the instance variables of it to the initial state.
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		resetSelected();
		for(int i = 0; i < 4; i++){
			game.getPlayerList().get(i).removeAllCards();
		}
		setActivePlayer(0);
		clearMsgArea();
		clearChatIn();
	}
	
	/**
	 * Enable user interaction with the whole GUI frame
	 */
	@Override
	public void enable() {
		// TODO Auto-generated method stub
		frame.setEnabled(true);
		Component[] comp = bigTwoPanel.getComponents();
		for(Component comps : comp){
			comps.setEnabled(true);
		};
		playButton.setEnabled(true);
		passButton.setEnabled(true);
	}

	/**
	 * To be called after the end of a game, disabling only the bigTwoPanel and the play and pass buttons,
	 * allowing users to start a new game or quit the game in the game menu.
	 */
	@Override
	public void disable() {
		// TODO Auto-generated method stub
		Component[] comp = bigTwoPanel.getComponents();
		for(Component comps : comp){
			comps.setEnabled(false);
		};
		playButton.setEnabled(false);
		passButton.setEnabled(false);
	}
	
	//Inner Classes
	/**
	 * This inner class models the table (panel) in a Big Two game. All visual elements are drawn in this
	 * panel, including the cards (both back and faces), the player names, avatars and the last hand on
	 * table. MouseListener is implemented to allow user interactivity. Users can click on the cards to
	 * select the card they want to play.
	 * 
	 * @author Yin
	 *
	 */
	class BigTwoPanel extends JPanel implements MouseListener{
		/**
		 * Constructor of the class: register the mouse listener to the class.
		 */
		public BigTwoPanel(){
			this.addMouseListener(this);
		}
		
		/**
		 * Draw all the visual elements.
		 * @param g
		 */
		public void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			
			//frame dimensions
			int fWidth = this.getWidth();
			int	fHeight = this.getHeight();
			
			//avatars dimensions
			int aWidth = avatars[0].getWidth(this);
			int aHeight = avatars[0].getHeight(this);
			
			//cards dimensions
			int cWidth = cardBackImages.getWidth(this);
			int cHeight = cardBackImages.getHeight(this);
			
			//original orientation of g2d object
			AffineTransform old = g2d.getTransform();
			
			//font of the player names
			g2d.setFont(new Font("TimesRoman", Font.BOLD, 15));
			
			//drawing background
			g2d.setColor(new Color(25, 91, 24));
			g2d.fillRect(0, 0, fWidth, fHeight);
			g2d.setColor(Color.RED);
			g2d.drawRect(2*fWidth/7, fHeight/3, 3*fWidth/7, fHeight/3);
			g2d.setColor(Color.WHITE);
			g2d.drawString("TABLE", 2*fWidth/7+5, fHeight/3+20);
			
			//drawing avatars, names and cards
			//this player
			int player = activePlayer;
			int numOfCards = game.getPlayerList().get(activePlayer).getNumOfCards();
			int cardLocationX = fWidth/2 - numOfCards/2*cWidth/2 - cWidth/2;
			int cardLocationY = fHeight-aHeight-cHeight-10;
			String playerName = game.getPlayerName();
			int length = g2d.getFontMetrics().stringWidth(playerName);
			
			for(int i = 0; i < numOfCards; i++){
				int suit = game.getPlayerList().get(player).getCardsInHand().getCard(i).getSuit();
				int rank = game.getPlayerList().get(player).getCardsInHand().getCard(i).getRank();
				if(!selected[i]){
					g2d.drawImage(cardImages[suit][rank], cardLocationX, cardLocationY, this);
				}else{
					g2d.drawImage(cardImages[suit][rank], cardLocationX, cardLocationY-10, this);
				}
				cardLocationX += cWidth/2;
			}
			g2d.drawImage(avatars[player], (fWidth-aWidth)/2, fHeight-aHeight, this);
			if(player ==  game.getCurrentIdx()){g2d.setColor(Color.YELLOW);};
			g2d.drawString(playerName, fWidth/2-length/2, fHeight-aHeight-cHeight-20);
			g2d.setColor(Color.WHITE);
			
			//2nd
			player = activePlayer+1>3? activePlayer-3:activePlayer+1;
			if(game.getPlayerList().get(player).getName() != null){
				numOfCards = game.getPlayerList().get(player).getNumOfCards();
				cardLocationX = aHeight+20;
				cardLocationY = fHeight/2 - numOfCards/2*cWidth/2 - cWidth/2;
				playerName = game.getPlayerList().get(player).getName();
				length = g2d.getFontMetrics().stringWidth(playerName);
							
				for(int i = 0; i < numOfCards; i++){
					g2d.rotate(Math.toRadians(90), cardLocationX+cWidth/2, cardLocationY+cHeight/2);
					g2d.drawImage(cardBackImages, cardLocationX, cardLocationY, this);
					cardLocationY += cWidth/2;
					g2d.setTransform(old);
				}
				g2d.rotate(Math.toRadians(90), aWidth/2, (fHeight-aHeight)/2 + aHeight/2);			
				g2d.drawImage(avatars[player], 0, (fHeight-aHeight)/2, this);
				g2d.setTransform(old);
				if(player ==  game.getCurrentIdx()){g2d.setColor(Color.YELLOW);};
				g2d.drawString(playerName, aHeight+cHeight+20, fHeight/2);
				g2d.setColor(Color.WHITE);
			};
			
			//3rd
			player = activePlayer+2>3? activePlayer+2-4:activePlayer+2;
			if(game.getPlayerList().get(player).getName() != null){
				numOfCards = game.getPlayerList().get(player).getNumOfCards();
				cardLocationX = fWidth/2 - numOfCards/2*cWidth/2 - cWidth/2;
				cardLocationY = aHeight+10;
				playerName = game.getPlayerList().get(player).getName();
				length = g2d.getFontMetrics().stringWidth(playerName);
							
				for(int i = 0; i < numOfCards; i++){
					g2d.rotate(Math.toRadians(180), cardLocationX+cWidth/2, cardLocationY+cHeight/2);
					g2d.drawImage(cardBackImages, cardLocationX, cardLocationY, this);
					cardLocationX += cWidth/2;
					g2d.setTransform(old);
				}
				g2d.rotate(Math.toRadians(180), (fWidth-aWidth)/2 + aWidth/2, aHeight/2);	
				g2d.drawImage(avatars[player], (fWidth-aWidth)/2, 0, this);
				g2d.setTransform(old);
				if(player ==  game.getCurrentIdx()){g2d.setColor(Color.YELLOW);};
				g2d.drawString(playerName, fWidth/2-length/2, aHeight+cHeight+25);
				g2d.setColor(Color.WHITE);
			};
			
			//4th
			player = activePlayer+3>3? activePlayer+3-4:activePlayer+3;
			if(game.getPlayerList().get(player).getName() != null){
				numOfCards = game.getPlayerList().get(player).getNumOfCards();
				cardLocationX = fWidth-cHeight-aHeight-5;
				cardLocationY = fHeight/2 - numOfCards/2*cWidth/2 - cWidth/2;
				playerName = game.getPlayerList().get(player).getName();
				length = g2d.getFontMetrics().stringWidth(playerName);
							
				for(int i = 0; i < numOfCards; i++){
					g2d.rotate(Math.toRadians(270), cardLocationX+cWidth/2, cardLocationY+cHeight/2);
					g2d.drawImage(cardBackImages, cardLocationX, cardLocationY, this);
					cardLocationY += cWidth/2;
					g2d.setTransform(old);
				}
				
				g2d.rotate(Math.toRadians(270), fWidth-aWidth+aWidth/2, (fHeight-aHeight)/2+aHeight/2);
				g2d.drawImage(avatars[player], fWidth-aWidth, (fHeight-aHeight)/2, this);
				g2d.setTransform(old);
				if(player ==  game.getCurrentIdx()){g2d.setColor(Color.YELLOW);};
				g2d.drawString(playerName, fWidth-aHeight-cHeight-length-25, fHeight/2);
				g2d.setColor(Color.WHITE);
			};

			//drawing last hand played and the corresponding player avatar
			if(game.getHandsOnTable().size()-1 >= 0){
				Hand lastHand = game.getHandsOnTable().get(game.getHandsOnTable().size()-1);
				numOfCards = lastHand.size();
				cardLocationX = fWidth/2 - numOfCards/2*cWidth/2 - cWidth/2;
				cardLocationY = fHeight/2 - cHeight/2;
				playerName = lastHand.getPlayer().getName();
				String corrPlayerName = lastHand.getPlayer().getName();
				int corrPlayer = 0;
				for(int i = 0; i < 4; i++){
					if(game.getPlayerList().get(i).getName() == corrPlayerName){
						corrPlayer = i;
						break;
					};
				};
				for(int i = 0; i < numOfCards; i++){
					int suit = lastHand.getCard(i).getSuit();
					int rank = lastHand.getCard(i).getRank();
					g2d.drawImage(cardImages[suit][rank], cardLocationX, cardLocationY, this);
					cardLocationX += cWidth/2;
				}
				g2d.drawImage(avatars[corrPlayer], cardLocationX-1*cWidth/7, cardLocationY+3*cHeight/5, this);
				g2d.drawString(lastHand.getType() + " played by: " + playerName, 5*fWidth/7-g2d.getFontMetrics().stringWidth(lastHand.getType() + " played by: " + playerName)-5, 
						2*fHeight/3-5);
			}
		}
		
		//Mouse events
		/**
		 * Implements the mouseClicked method to determine where the click lands on and thus selecting the
		 * correct card.
		 */
		@Override
		public void mouseClicked(MouseEvent event){
			if(event.getButton() == MouseEvent.BUTTON1){
				//obtain mouse location
				int xm = event.getX();
				int ym = event.getY();
				int numOfCards = game.getPlayerList().get(activePlayer).getNumOfCards();
			
				//frame dimensions
				int fWidth = this.getWidth();
				int	fHeight = this.getHeight();
				
				//avatars dimensions
				int aHeight = avatars[0].getHeight(this);
				
				//cards dimensions
				int cWidth = cardBackImages.getWidth(this);
				int cHeight = cardBackImages.getHeight(this);
				
				//cards locations
				int cardLocationX = fWidth/2 - numOfCards/2*cWidth/2 - cWidth/2;
				int cardLocationY = fHeight-aHeight-cHeight-10;
			
				//check which card the click event lands on
				for(int i = numOfCards-1; i >= 0; i--){
					if(xm > cardLocationX+i*cWidth/2 && xm < cardLocationX+(i+2)*cWidth/2){
						if(selected[i]){
							if(ym > cardLocationY-10 && ym < cardLocationY+cHeight-10){
								selected[i] = false;
								break;
							}else if(ym > cardLocationY+cHeight-10 && ym < cardLocationY+cHeight){
								if(i != 0 && !selected[i-1]){
									selected[i-1] = true;
									break;
								}
							}
						}else{
							if(ym > cardLocationY && ym < cardLocationY+cHeight){
								selected[i] = true;
								break;
							}
						}
					}
				}
			}
			frame.repaint();
		}
		@Override
		public void mouseEntered(MouseEvent event) {
		}
		@Override
		public void mouseExited(MouseEvent event) {
		}
		@Override
		public void mousePressed(MouseEvent event) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
	
	/**
	 * Inner class that models an action listener for the play button such that it can submit the activePlayer
	 * and selected[] array to the server with makeMove().
	 * 
	 * @author Yin
	 */
	class PlayButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			game.makeMove(activePlayer, getSelected());
		}
	}
	
	/**
	 * Inner class that implements the action listener for the pass button such that it can pass an empty
	 * selected[] array for the pass function in the checkMove() method.
	 * 
	 * @author Yin
	 */
	class PassButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			resetSelected();
			game.makeMove(activePlayer, getSelected());
		}
	}
	
	/**
	 * Inner class that implements the action listener for the chat text field such that the message can
	 * be sent to the the message box.
	 * 
	 * @author Yin
	 */
	class ChatListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			String msg = chatOut.getText();
			CardGameMessage chatMsg = new CardGameMessage(CardGameMessage.MSG, -1, msg);
			game.sendMessage(chatMsg);
			chatOut.setText("");
			chatOut.requestFocus();
		};
	};
	
	/**
	 * Inner class that implements the action listener for the connect menu item such that it connects
	 * client to server again.
	 * 
	 * @author Yin
	 */
	class ConnectMenuListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			game.makeConnection();
		}
	}
	
	/**
	 * Inner class that implements the action listener for the quit menu item such that it shuts down the
	 * game.
	 */
	class QuitMenuListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			System.exit(0);
		}
	}
}
