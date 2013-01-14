package belette;

import bots.*;
import utils.*;

import java.awt.*;
/**
 * Interface (a modifier suivant vos envies) permettant de gérer les items dans le jeu
 * 
 * @author L. Simon, Univ. Paris Sud, 2008
 *
 */
public interface IItem {

	/**
	 * @return le temps en frames de respawn pour cet item
	 */
	public int getRespawnTime();
	/**
	 * Modifie le temps que met un item pour apparaitre sur la carte
	 * @param time temps en frames
	 */
	public void setRespawnTime(int frames); 

	/**
	 * Ajoute des coordonnées possibles pour apparaitre sur la carte
	 * après getRespawnTime() secondes une fois récupéré
	 * @param x
	 * @param y
	 */
	public void addCoordRespawn(int x, int y);
	
	/**
	 * @return Les coordonnées de l'item, ou -1,-1 si pas sur la carte par ex.
	 */
	public Vector2d getCoord();
	
	/**
	 * On va représenter les item en rond... Pour les collisions sur la carte,
	 * on a besoin du rayon de l'item
	 * @return le rayon (en coordonnées de cartes) de l'item
	 */
	public float itemRadius();
	
	/**
	 * Permet d'appeler les fonctions adéquates pour modifier le bot
	 * Vous pouvez le modifier pour appeler cette fonction à l'envers,
	 * depuis bot avec une méthode captureItem(IItem item) dans bot...
	 * A vous de voir...
	 * Il faut lancer un timer pour faire renaitre l'item au bout d'un certain temps...
	 * @param bot
	 */
	public void capturedByBot(IBot bot);
	
	/**
	 * Affiche l'item sur le dessin... A vos idées !
	 * @param g
	 */
	public void draw(Graphics g);
	
	/**
	 * @return la chaine representant l'item et son etat, pour debug ou autre
	 */
	public String toString();
	
	/**
	 * Mise à jour éventuelle après chaque frame (Récupéré ? disparaitre ? Apparaitre ? Dégrader ?)
	 */
	public void computeNextFrame();
	
}
