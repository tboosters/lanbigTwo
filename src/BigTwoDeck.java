
/**
 * This class is a subclass of the Deck class used to model a deck designed for Big Two games.
 * It overrides the initialize() method specifically for Big Two cards (BigTwoCard class).
 * @author Yin
 *
 */
public class BigTwoDeck extends Deck {
	/**
	 * Initialize the deck with cards modelled for a Big Two game (BigTwoCard class).
	 */
	public void initialize(){
		removeAllCards();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				BigTwoCard card = new BigTwoCard(i, j);
				addCard(card);
			}
		}
	}
}
