package utils;

import java.util.ArrayList;
import java.util.HashMap;

public interface AStarParam {

	
	public void add(ArrayList<Noeud> noeuds, Noeud noeud);
	public boolean isTerminal(Noeud n);
	public Noeud choose(ArrayList<Noeud>border , HashMap<Noeud, Double> g);
	public double cost(Noeud a, Noeud b);
}
