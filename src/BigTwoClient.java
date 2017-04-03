import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * This class is used to model a Big Two game with cards, players, hands played, index of
 * current player and console for playing. 
 * @author Yin
 *
 */
public class BigTwoClient implements CardGame, NetworkGame{
	//Private variables
	private int numOfPlayers;
	
	private Deck deck;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private int playerID;
	private String playerName;
	
	private String serverIP;
	private int serverPort;
	private Socket sock;
	private ObjectOutputStream oos; 
	private boolean connected;
	
	private int currentIdx;
	private BigTwoTable table;
	
	//Constructor
	/**
	 * Constructor of the Big Two game. It creates 4 players to the player list, prompt the user to
	 * input their name, server IP address and server port, then creates a table for displaying game 
	 * content.
	 */
	public BigTwoClient(){
		numOfPlayers = 0;
		//Reset connected
		connected = false;
		while(true){
			playerName = JOptionPane.showInputDialog("Please enter your name:");
			if(!(playerName == null)){
				break;
			};
		};
		while(true){
			serverIP = JOptionPane.showInputDialog("Please enter the server IP:");
			if(!(serverIP == null)){
				break;
			};
		};
		while(true){
			String input = JOptionPane.showInputDialog("Please enter your the server port:");
			if(!(input == null)){
				serverPort = Integer.parseInt(input);
				break;
			};
		};
		playerList = new ArrayList<CardGamePlayer>();
		handsOnTable = new ArrayList<Hand>();
		for(int i = 0; i < 4; i++){
			CardGamePlayer player = new CardGamePlayer();
			playerList.add(player);
		};
		makeConnection();
		table = new BigTwoTable(this);
		table.setActivePlayer(playerID);
	};
	
	//Public methods
	/**
	 * Getter of number of players
	 * @return number of players
	 */
	public int getNumOfPlayers(){
		return playerList.size();
	}
	/**
	 * Getter of the private variable "deck", i.e. content of the deck
	 * @return deck
	 */
	public Deck getDeck(){
		return deck;
	};
	/**
	 * Getter of the private array list "playerList", i.e. players in the game
	 * @return playerList
	 */
	public ArrayList<CardGamePlayer> getPlayerList(){
		return playerList;
	};
	/**
	 * Getter of private array list "handsOnTable", i.e. hands played in the game
	 * @return handsOnTable
	 */
	public ArrayList<Hand> getHandsOnTable(){
		return handsOnTable;
	};
	/**
	 * Getter of private variable "currentIdx", i.e. index of current player
	 * @return currentIdx
	 */
	public int getCurrentIdx(){
		return currentIdx;
	};
	/**
	 * Starts the game with a deck of cards as argument when all 4 players are set, pick the player
	 * with diamond 3 to start first
	 * @param deck
	 */
	public void start(Deck deck){
		//Reset first
		first = true;
		//clear cards on table and those in player hands
		handsOnTable.clear();
		//Distribute cards
		for(int i = 0; i < 4; i++){
			playerList.get(i).removeAllCards();
			for(int j = 0; j < 13; j++){
				playerList.get(i).addCard(deck.getCard((i*13+j)));
				playerList.get(i).sortCardsInHand();
			};
		};
		//Find the player with Diamond 3
		int firstPlayer = 0;
		BigTwoCard d3 = new BigTwoCard(0,2);
		for(; firstPlayer < 4; firstPlayer++){
			if(playerList.get(firstPlayer).getCardsInHand().contains(d3)){
				break;
			};
		};
		table.setActivePlayer(playerID);
		currentIdx = firstPlayer;
		table.repaint();
		table.clearMsgArea();
		table.printMsgln("All players set, game starts.");
		table.printMsgln(playerList.get(currentIdx).getName() + "'s turn:");
	};
	
	/**
	 * This method determines if the game has ended by counting the number of cards of the current
	 * player
	 * @return true if current player has 0 card, false otherwise
	 */
	public boolean endOfGame(){
		for(int i = 0; i < 4; i++){
			if(playerList.get(i).getName() == null){		//not enough player, game not started 
				return true;
			};
		};
		return playerList.get(currentIdx).getNumOfCards() == 0;
	}
	
	/**
	 * Make a move (cards played) by the cards chosen by the player, send the move as the message to the sever
	 */
	public void makeMove(int playerID, int[] cardIdx){
		CardGameMessage move = new CardGameMessage(CardGameMessage.MOVE, playerID, cardIdx);
		sendMessage(move);
	}
	
	private boolean first = true;
	/**
	 * This method implements the game logic after obtaining the cards that the current player plays. It
	 * compares the cards with the last hand on table to see if it is a playable move. If the cards are
	 * playable, they are removed from the player's hands and are placed on the table. If the player has
	 * no more cards, it ends the game, otherwise it passes the turn to the next player.
	 * 
	 * @see endOfGame()
	 * @param cardIdx
	 */
	public void checkMove(int playerID, int[] cardIdx){
		BigTwoCard d3 = new BigTwoCard(0,2);
		
		if(cardIdx != null){
			Hand hand = composeHand(playerList.get(playerID), playerList.get(playerID).play(cardIdx));
			//First Play
			if(hand == null){
				table.printMsgln("Not a legal move!!!");
				return;
			}
			if(first){
				if(hand.contains(d3)){
					table.printMsgln("{" + hand.getType() + "} ");
					for(int i = 0; i < hand.size(); i++){
						table.printMsg("[" + hand.getCard(i).toString() + "] ");
					}
					table.printMsgln("");
					handsOnTable.add(hand);
					playerList.get(playerID).removeCards(hand);
					first = false;
				}else{
					table.printMsgln("{" + hand.getType() + "} ");
					for(int i = 0; i < hand.size(); i++){
						table.printMsg("[" + hand.getCard(i).toString() + "] ");
					}
					table.printMsgln(" <== Not a legal move!!!");
					return;
				};
				//Thereafter
			}else if((hand.size() == handsOnTable.get(handsOnTable.size()-1).size() && (hand.beats(handsOnTable.get(handsOnTable.size()-1)))) || playerList.get(playerID) == handsOnTable.get(handsOnTable.size()-1).getPlayer()){
				table.printMsg("{" + hand.getType() + "} ");
				for(int i = 0; i < hand.size(); i++){
					table.printMsg("[" + hand.getCard(i).toString() + "] ");
				};
				table.printMsgln("");
				handsOnTable.add(hand);
				playerList.get(playerID).removeCards(hand);
			}else{
				table.printMsgln("{" + hand.getType() + "}");
				for(int i = 0; i < hand.size(); i++){
					table.printMsg("[" + hand.getCard(i).toString() + "] ");
				}
				table.printMsgln(" <== Not a legal move!!!");
				return;
			}
		}else{
			if(((handsOnTable.size() != 0)) && (handsOnTable.get(handsOnTable.size()-1).getPlayer() != playerList.get(playerID))){
				table.printMsgln("{pass}");
			}else{
				table.printMsgln("{pass} <== Not a legal move!!!");
				return;
			}
		};
		
		if(!endOfGame()){
			currentIdx++;
			if(currentIdx > 3){
				currentIdx = 0;
			};
			table.printMsgln(playerList.get(currentIdx).getName() + "'s turn:");
			table.resetSelected();
			table.repaint();
		}else{
			table.disable();
			String msg = "Game ends\n";
			for(int i = 0; i < 4; i++){
				if(i == currentIdx){
					msg += (playerList.get(i).getName() + " wins the game.\n");
				}else{
					msg += (playerList.get(i).getName()  + " has " + playerList.get(i).getNumOfCards() + " cards in hand.\n");
				};
			};
			JOptionPane.showMessageDialog(null, msg);
			CardGameMessage ready = new CardGameMessage(CardGameMessage.READY, -1, null);
			sendMessage(ready);
			return;
		};
		table.repaint();
	};

	//NetworkGame methods
	/**
	 * Getter of playerID
	 * @return playerID
	 */
	public int getPlayerID(){
		return playerID;
	}
	
	/**
	 * Setter of playerID
	 */
	public void setPlayerID(int playerID){
		this.playerID = playerID;
	}
	
	/**
	 * Getter of playerName
	 * @return playerName
	 */
	public String getPlayerName(){
		return playerName;
	}
	
	/**
	 * Setter of playerName
	 */
	public void setPlayerName(String playerName){
		this.playerName = playerName;
	}
	
	/**
	 * Getter of serverIP
	 * @return serverIP
	 */
	public String getServerIP(){
		return serverIP;
	}
	
	/**
	 * Setter of serverIP
	 */
	public void setServerIP(String serverIP){
		this.serverIP = serverIP;
	}
	
	/**
	 * Getter of serverPort
	 */
	public int getServerPort(){
		return serverPort;
	}
	
	/**
	 * Setter of serverPort
	 */
	public void setServerPort(int serverPort){
		this.serverPort = serverPort;
	}
	
	/**
	 * Connect the client with the server
	 */
	public synchronized void makeConnection(){
		if(!connected){
			try{
				sock = new Socket(serverIP, serverPort);
				oos = new ObjectOutputStream(sock.getOutputStream());
				Thread readMsgThread = new Thread(new ServerHandler());
				readMsgThread.start();
				CardGameMessage join = new CardGameMessage(CardGameMessage.JOIN, -1, playerName);
				sendMessage(join);
				CardGameMessage ready = new CardGameMessage(CardGameMessage.READY, -1, null);
				sendMessage(ready);
				connected = true;
			}catch(Exception e){
				e.printStackTrace();
			};
		}else{
			table.printMsgln(playerName + " has already connected to the server.");
		}
		
		
	}
	
	/**
	 * Parse the message received from the server into commands for the client to operate
	 */
	public synchronized void parseMessage(GameMessage message){
		int type = message.getType();
		switch(type){
			case 0:{		//PLAYER_LIST
				String[] names = (String[]) message.getData();
				playerID = message.getPlayerID();
				for(int i = 0; i < 4; i++){
					playerList.get(i).setName(names[i]);
				};
				break;
			}
			case 1:{		//JOIN
				int id = message.getPlayerID();
				String name = (String) message.getData();
				playerList.get(id).setName(name);
				break;
			}
			case 2:{		//FULL
				table.printMsgln("The server is full! Cannot join the game now.");
				break;
			}
			case 3:{		//QUIT
				int id = message.getPlayerID();
				String ip = (String) message.getData();
				table.printMsgln(playerList.get(id).getName() + " (" + ip + ") has left the game.");
				numOfPlayers--;
				playerList.get(id).setName(null);
				table.repaint();
				table.disable();
				CardGameMessage ready = new CardGameMessage(CardGameMessage.READY, -1, null);
				sendMessage(ready);
				break;
			}
			case 4:{		//READY
				int id = message.getPlayerID();
				table.printMsgln(playerList.get(id).getName() + " is ready.");
				numOfPlayers++;
				break;
			}
			case 5:{		//START
				BigTwoDeck deck = (BigTwoDeck) message.getData();
				start(deck);
				break;
			}
			case 6:{		//MOVE
				int id = message.getPlayerID();
				int[] cards = (int[]) message.getData();
				checkMove(id, cards);
				break;
			}
			case 7:{		//MSG
				String msg = (String) message.getData();
				table.printChat(msg);
				break;
			}
		};
	}
	
	/**
	 * Sends message to the server
	 */
	public void sendMessage(GameMessage message){
		try{
			oos.writeObject(message);
			oos.flush();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//Inner Class
	/**
	 * A runnable for the thread of receiving message from the server
	 * @author Yin
	 */
	public class ServerHandler implements Runnable{
		CardGameMessage msg;
		ObjectInputStream streamReader;
		
		public ServerHandler(){
			try{
				streamReader = new ObjectInputStream(sock.getInputStream());
			}catch(Exception ex){
				ex.printStackTrace();
			};
		}
		
		public synchronized void run(){
			try{
				while((msg = (CardGameMessage) streamReader.readObject()) != null){
					parseMessage(msg);
					try{
						Thread.sleep(300);
					}catch(Exception ex){
						ex.printStackTrace();
					};
				};
			}catch(Exception ex){
				ex.printStackTrace();
			};
		}
	}
	
	//Public static methods
	/**
	 * Starts the Big Two game by creating a Big Two game with a shuffled deck
	 * @param args
	 */
	public static void main(String[] args){
		new BigTwoClient();
	};
	
	/**
	 * Returns a valid hand from the list of cards played by the player
	 * @param player
	 * @param cards
	 * @return composeHand
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards){
		Single s = new Single(player, cards);
		Pair p = new Pair(player, cards);
		Triple t = new Triple(player, cards);
		Quad q = new Quad(player, cards);
		Straight st = new Straight(player, cards);
		Flush f = new Flush(player, cards);
		FullHouse fh = new FullHouse(player, cards);
		StraightFlush sf = new StraightFlush(player, cards);
		
		if(cards == null){
			return null;
		}else if(s.isValid()){
			return s;
		}else if(p.isValid()){
			return p;
		}else if(t.isValid()){
			return t;
		}else if(q.isValid()){
			return q;
		}else if(fh.isValid()){
			return fh;
		}else if(sf.isValid()){
			return sf;
		}else if(st.isValid()){
			return st;
		}else if(f.isValid()){
			return f;
		}else{
			return null;
		}
	};
}
