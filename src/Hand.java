
/**
 * This class is a subclass of CardList used to model any hands as list of cards that a player 
 * would play in a Big Two game.
 * It contains private variable "player" for storing the current player and "typeNo" storing type number of cards.
 * It also contains public methods like getter of "player" and the top card of this hand, as
 * well as beat() method for checking if this hand beats a specific hand.
 * There are abstract methods like isValid(), getType() and getTypeNo() to be overriden in its subclasses.
 * @author Yin
 *
 */
public abstract class Hand extends CardList {
	//Private variables
	private CardGamePlayer player;
		
	//Constructor
	/**
	 * Create a hand specifying the player and the list of cards
	 * @param player
	 * @param cards
	 */
	public Hand(CardGamePlayer player, CardList cards){
		this.player = player;
		for(int i = 0; i < cards.size(); i++){
			addCard(cards.getCard(i));
		}
	}
	/**
	 * Empty argument constructor for subclasses
	 */
	public Hand(){};
	
	//Public Methods
	/**
	 * Getter of private variable "player", i.e. the player who plays the hand
	 * @return player
	 */
	public CardGamePlayer getPlayer(){
		return player;
	}
	/**
	 * Getter of the top ranked card in the hand
	 * @return top ranked card in hand
	 */
	public Card getTopCard(){
		Card top = null;
		sort();
		switch(getTypeNo(getType())){
			//FullHouse
			case 5:
				if(getCard(2).getRank() != getCard(3).getRank()){
					top = getCard(0);
					for(int i = 1; i < 3; i++){
						if(getCard(i).getSuit() > top.getSuit()){
							top = getCard(i);
						}
					};
				}else if(getCard(1).getRank() != getCard(2).getRank()){
					top = getCard(2);
					for(int i = 3; i < 5; i++){
						if(getCard(i).getSuit() > top.getSuit()){
							top = getCard(i);
						}
					}
				}
				break;
			//Quad
			case 6:
				if(getCard(0).getRank() != getCard(1).getRank()){
					top = getCard(1);
					for(int i = 2; i < 5; i++){
						if(getCard(i).getSuit() > top.getSuit()){
							top = getCard(i);
						}
					};
				}else if(getCard(3).getRank() != getCard(4).getRank()){
					top = getCard(0);
					for(int i = 1; i < 4; i++){
						if(getCard(i).getSuit() > top.getSuit()){
							top = getCard(i);
						}
					}
				}
				break;
			//Single, Pair, Triple, Straight, Flush, StriahghtFlush
			default:
				top = getCard(size()-1);
		};
		return top;
	}
	/**
	 * Decides if the hand beats the specified hand by type and top card
	 * @param hand
	 * @return true if this hand beats specified hand, false otherwise
	 */
	public boolean beats(Hand hand){
		if(getTypeNo(getType()) > hand.getTypeNo(hand.getType())){
			return true;
		}else if((getTypeNo(getType()) == hand.getTypeNo(hand.getType())) && getTopCard().compareTo(hand.getTopCard()) == 1){
			return true;
		}else{
			return false;
		}
	}
	
	//Abstract Methods
	/**
	 * Abstract method to be overriden in subclasses. Used to check if the hand is valid.
	 * @return true if hand is valid, false otherwise
	 */
	public abstract boolean isValid();
	/**
	 * Abstract method to be overriden in subclasses. Getter the type of the hand in terms of string.
	 * @return type of the hand
	 */
	public abstract String getType();
	/**
	 * Converting type into type number of the hand in terms of string.
	 * Number representing the type of the hand:
	 * 0:Single 1:Pair 2:Triple 3:Straight 4:Flush 5:FullHouse 6:Quad 7:StraightFlush
	 * @return type number of the hand
	 */
	public int getTypeNo(String type){
		switch(type){
			case "Single":
				return 0;
			case "Pair":
				return 1;
			case "Triple":
				return 2;
			case "Straight":
				return 3;
			case "Flush":
				return 4;
			case "FullHouse":
				return 5;
			case "Quad":
				return 6;
			default:
				return 7;
		}
	};
}
