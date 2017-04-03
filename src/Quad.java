
/**
 * This class is a subclass of "Hand" class. It is used to model a "quad" hand in a Big Two Game.
 * @author Yin
 *
 */
public class Quad extends Hand {
	/**
	 * Make use of the super class constructor
	 * @param player
	 * @param cards
	 */
	public Quad(CardGamePlayer player, CardList cards){
		super(player, cards);
	};
	/**
	 * Decides if the hand is a valid quad, i.e. contains five cards with 4 in the same rank
	 * @return true if valid, false if not
	 */
	public boolean isValid(){
		sort();
		if(size() == 5){
			if(getCard(0).getRank() == getCard(1).getRank()){
				if(getCard(1).getRank() == getCard(2).getRank()){
					if(getCard(2).getRank() == getCard(3).getRank()){
						return true;
					}
				}
			}else if(getCard(4).getRank() == getCard(3).getRank()){
				if(getCard(3).getRank() == getCard(2).getRank()){
					if(getCard(2).getRank() == getCard(1).getRank()){
						return true;
					}
				}
			}
		}
		return false;
	};
	
	/**
	 * Returns the name of this type of hand
	 * @return string "Quad"
	 */
	public String getType(){
		return "Quad";
	};
}
