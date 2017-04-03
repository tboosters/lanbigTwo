import java.util.ArrayList;

/**
 * This class is a subclass of "Hand" class. It is used to model a "fullhouse" hand in a Big Two Game.
 * @author Yin
 *
 */
public class FullHouse extends Hand {
	/**
	 * Make use of the super class constructor
	 * @param player
	 * @param cards
	 */
	public FullHouse(CardGamePlayer player, CardList cards){
		super(player, cards);
	};
	/**
	 * Decides if the hand is a valid fullhouse, i.e. contains five cards with 2 of same rank and 3 of another same rank
	 * @return true if valid, false if not
	 */
	public boolean isValid(){
		sort();
		if(size() == 5){
			ArrayList<Integer> ranks = new ArrayList<Integer>();
			for(int i = 0; i < 5; i++){
				int rank = getCard(i).getRank();
				ranks.add((rank-2>=0? rank-2:11+rank));
			};
			ranks.sort(null);
			if(ranks.get(2) != ranks.get(3)){
				for(int i = 0; i < 2; i++){
					if(ranks.get(i) != ranks.get(i+1)){
						return false;
					}
				};
				if(ranks.get(3) == ranks.get(4)){
					return true;
				};
			}else if(ranks.get(1) != ranks.get(2)){
				for(int i = 2; i < 4; i++){
					if(ranks.get(i) != ranks.get(i+1)){
						return false;
					}
				};
				if(ranks.get(0) == ranks.get(1)){
					return true;
				};
			}
		}
		return false;
	};
	
	/**
	 * Returns the name of this type of hand
	 * @return string "FullHouse"
	 */
	public String getType(){
		return "FullHouse";
	};
}
