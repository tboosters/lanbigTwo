
/**
 * This class is a subclass of "Hand" class. It is used to model a "flush" hand in a Big Two Game. 
 * @author Yin
 *
 */
public class Flush extends Hand {
	/**
	 * Make use of the super class constructor
	 * @param player
	 * @param cards
	 */
	public Flush(CardGamePlayer player, CardList cards){
		super(player, cards);
	};
	/**
	 * Decides if the hand is a valid flush, i.e. contains five cards all in same suit
	 * @return true if valid, false if not
	 */
	public boolean isValid(){
		if(size() == 5){
			for(int i = 1; i < 5; i++){
				if(this.getCard(0).getSuit() != this.getCard(i).getSuit()){
					return false;
				};
			};
			return true;
		}
		return false;
	};
	
	/**
	 * Returns the name of this type of hand
	 * @return string "Flush"
	 */
	public String getType(){
		return "Flush";
	};
}
