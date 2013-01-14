package bots;

import java.util.ArrayList;
import java.util.HashMap;

import utils.AStarParam;
import utils.Noeud;

public class SoldierParamPathFinding implements AStarParam{

	Noeud toGo;
	
	
	public SoldierParamPathFinding(Noeud n){
		toGo = n;
	}
	
	@Override
	public void add(ArrayList<Noeud> noeuds, Noeud noeud) {
		noeuds.add(noeud);
	}

	@Override
	public boolean isTerminal(Noeud n) {
		return (n.getDistance(toGo) == 0);
	}

	@Override
	public Noeud choose(ArrayList<Noeud> border, HashMap<Noeud, Double> g) {
		double min = Integer.MAX_VALUE;
		Noeud closestNode = null;
		for(Noeud n : border){
			if(min >= g.get(n)){
				closestNode = n;
				min = g.get(n);
			}
		}
		return closestNode;
	}

	@Override
	public double cost(Noeud a, Noeud b) {
		return  a.getDistance(b);
	}

	
}
