
/**
 * This class is a subclass of the "Hand" class. It is used to model a "pair" in a Big Two game.
 * @author Yin
 *
 */
public class Pair extends Hand {
	/**
	 * Make use of the super class constructor
	 * @param player
	 * @param cards
	 */
	public Pair(CardGamePlayer player, CardList cards){
		super(player, cards);
	};
	/**
	 * Decides if the hand is a valid pair, i.e. contains only two cards with same rank
	 * @return true if valid, false if not
	 */
	public boolean isValid(){
		if(size() == 2){
			if(getCard(0).getRank() == getCard(1).getRank()){
				return true;
			}
		}
		return false;
	};
	
	/**
	 * Returns the name of this type of hand
	 * @return string "Pair"
	 */
	public String getType(){
		return "Pair";
	};
}
