
/**
 * This class is a subclass of "Hand" class. It is used to model a "single" hand in a Big Two Game. 
 * @author Yin
 *
 */
public class Single extends Hand{
	/**
	 * Make use of the super class constructor
	 * @param player
	 * @param cards
	 */
	public Single(CardGamePlayer player, CardList cards){
		super(player, cards);
	};
	/**
	 * Decides if the hand is a valid single, i.e. contains only one single card
	 * @return true if valid, false if not
	 */
	public boolean isValid(){
		if(size() == 1){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Returns the name of this type of hand
	 * @return string "Single"
	 */
	public String getType(){
		return "Single";
	}
}
