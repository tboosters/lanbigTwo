import java.util.ArrayList;

/**
 * This class is a subclass of "Hand" class. It is used to model a "straight flush" hand in a Big Two Game.
 * @author Yin
 *
 */
public class StraightFlush extends Hand {
	/**
	 * Make use of the super class constructor
	 * @param player
	 * @param cards
	 */
	public StraightFlush(CardGamePlayer player, CardList cards){
		super(player, cards);
	};
	/**
	 * Decides if the hand is a valid quad, i.e. contains five cards consecutive ranks in the same suit
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
			sort();
			for(int i = 0; i < 5; i++){
				if(this.getCard(i).getSuit() != this.getCard(i+1).getSuit() || (ranks.get(i+1) - ranks.get(i)) != 1){
					return false;
				};
			};
			return true;
		}
		return false;
	};
	
	/**
	 * Returns the name of this type of hand
	 * @return string "StraightFlush"
	 */
	public String getType(){
		return "StraightFlush";
	};
}
