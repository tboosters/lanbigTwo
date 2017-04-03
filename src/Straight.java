import java.util.ArrayList;

/**
 * This class is a subclass of "Hand" class. It is used to model a "straight" hand in a Big Two Game. 
 * @author Yin
 *
 */
public class Straight extends Hand {
	/**
	 * Make use of the super class constructor
	 * @param player
	 * @param cards
	 */
	public Straight(CardGamePlayer player, CardList cards){
		super(player, cards);
	};
	/**
	 * Decides if the hand is a valid straight, i.e. contains five cards with consecutive ranks
	 * @return true if valid, false if not
	 */
	public boolean isValid(){
		if(size() == 5){
			ArrayList<Integer> ranks = new ArrayList<Integer>();
			for(int i = 0; i < 5; i++){
				int rank = getCard(i).getRank();
				ranks.add((rank-2>=0? rank-2:11+rank));
			};
			ranks.sort(null);
			for(int i = 0; i < 4; i++){
				if(ranks.get(i+1)-ranks.get(i) != 1){
					return false;
				}
			};
			return true;
		}
		return false;
	};
	
	/**
	 * Returns the name of this type of hand
	 * @return string "Straight"
	 */
	public String getType(){
		return "Straight";
	};
}
