package utils;

import java.util.Vector;


public class Noeud{

	private Vector2d position;
	private Vector<Noeud> myNeighbours;
	
	
	/**
	 * constructor
	 * @param position
	 */
	public Noeud(Vector2d position){
		this.position = position;
		this.myNeighbours = new Vector<Noeud>();
	}
	
	/**
	 * constructor
	 * @param position
	 * @param myNeigbours
	 * @param myNeighbours 
	 */
	public Noeud(Vector2d position, Vector<Noeud> myNeighbours){
		this.position = position;
		this.myNeighbours = myNeighbours;
		((Object) this).hashCode();
	}
	
	/**
	 * retourne la distance entre 
	 * @param a
	 * @return
	 */
	public double getDistance(Noeud a){
		return position.distance(a.position);
	}

	
	
	public Vector2d getPosition() {
		return position;
	}

	public void setPosition(Vector2d position) {
		this.position = position;
	}

	public Vector<Noeud> getMyNeighbours() {
		return myNeighbours;
	}
	
	
	
	
	
}
