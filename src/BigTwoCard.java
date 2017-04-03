
/**
 * This class is a subclass of Card class used to model a card used in a Big Two game. 
 * It overrides the compareTo() method to reflect order of cards in the Big Two game.
 * 
 * @author Yin
 *
 */
public class BigTwoCard extends Card {
	//Constructor
	/**
	 * Builds a card with arguments of suit and rank.
	 */
	public BigTwoCard(int suit, int rank){
		super(suit,rank);
	};

	/**
	 * Overrides the same-named method in Card class.
	 * Compare this card with the card specified in the argument in Big Two game order.
	 * @return 1 for larger, 0 for equal, -1 for smaller
	 */
	public int compareTo(Card card){
		int thisBigTwoRank = ((rank-2)>=0? rank-2:11+rank);
		int cardBigTwoRank = ((card.rank-2)>=0? card.rank-2:11+card.rank);
		if(thisBigTwoRank > cardBigTwoRank){
			return 1;
		}else if(thisBigTwoRank < cardBigTwoRank){
			return -1;
		}else{
			if(this.suit > card.suit){
				return 1;
			}else if(this.suit < card.suit){
				return -1;
			}else{
				return 0;
			}
		}
	};
};

