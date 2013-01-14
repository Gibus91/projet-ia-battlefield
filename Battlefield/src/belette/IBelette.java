package belette;

import bots.*;
import utils.*;

import java.awt.*;

/**
 * Interface pour donner une idée de ce que l'on peut mettre
 * pour gérer les balles
 * 
 * @author L. Simon, Univ. Paris Sud, 2008
 *
 */
public interface IBelette {

	/**
	 * @return le bot qui a tiré cette balle
	 */
	public IBot firedBy();
	
	/**
	 * @return le vecteur vitesse de la balle (ou de la roquette)
	 */
	public Vector2d getVelocity();
	
	/**
	 * @return force de la balle
	 */
	public float getPower();
	
	/**
	 * @return les coordonnées de la balle
	 */
	public Vector2d getCoords();
	
	/**
	 * @return le rayon de l'arme, pour les collisions
	 */
	public float getRadius();
	
	/**
	 * Permet de rendre compte des dégats sur le bot qui est touché
	 * Suivant votre modélisation des bots, vous pouvez la remplacer
	 * par une méthode IBot.hitBy(IBelette balle) dans les bots... A vous
	 * de voir...
	 * @param bot
	 */
	public void hitBot(IBot bot);
	
	/**
	 * Juste pour dire, au cas où vous vouliez gérer d'éventuelles explosions
	 * @param impactCoords coordonnées de l'impact
	 */
	public void hitWall(Vector2d impactCoords);
	
	/**
	 * Affichage de la balle (traçante, donc)
	 * @param g
	 */
	public void draw(Graphics g);
	
	
	/**
	 * Mise à jour des coordonnées de la balle, gestion des collisions, ...
	 */
	public void computeNextFrame();
	/**
	 * Renvoie si la balle est encore active
	 * @return
	 */
	public boolean isActive();
}
