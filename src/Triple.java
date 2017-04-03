
/**
 * This class is a subclass of "Hand" class. It is used to model a "triple" hand in a Big Two Game. 
 * @author Yin
 *
 */
public class Triple extends Hand {
	/**
	 * Make use of the super class constructor
	 * @param player
	 * @param cards
	 */
	public Triple(CardGamePlayer player, CardList cards){
		super(player, cards);
	};
	/**
	 * Decides if the hand is a valid triple, i.e. contains three cards with same rank
	 * @return true if valid, false if not
	 */
	public boolean isValid(){
		if(size() == 3){
			if(getCard(0).getRank() == getCard(1).getRank()){
				if(getCard(1).getRank() == getCard(2).getRank()){
					return true;
				}
			}
		}
		return false;
	};
	
	/**
	 * Returns the name of this type of hand
	 * @return string "Triple"
	 */
	public String getType(){
		return "Triple";
	};
}
