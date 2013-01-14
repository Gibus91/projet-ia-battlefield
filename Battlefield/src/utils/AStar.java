package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AStar {

	
	
	public HashMap<Noeud, Noeud> performe(Noeud init, AStarParam p){
		
		HashSet<Noeud> dejaVu = new HashSet<Noeud>();
		ArrayList<Noeud> border = new ArrayList<Noeud>();
		border.add(init);
		boolean success = false;
		HashMap<Noeud, Double> g = new HashMap<Noeud, Double>();
		HashMap<Noeud, Noeud> pere = new HashMap<Noeud, Noeud>();
		g.put(init, 0.0);
		while(!border.isEmpty() && !success){
			//System.out.println("inside while");
			Noeud n = p.choose(border, g);
			if(p.isTerminal(n)){
				success = true;
			}
			else{
				border.remove(n);
				dejaVu.add(n);
				for(Noeud s : n.getMyNeighbours()){
					if(!dejaVu.contains(s) && !border.contains(s)){
						pere.put(s, n);	
						g.put(s, p.cost(n,s) + g.get(n));
						p.add(border, s);
					}
					else{
						if( g.get(s) > g.get(n) + p.cost(n, s)){
							if(dejaVu.contains(s)){
								dejaVu.remove(s);
							}
							pere.put(s, n);
							g.put(s, p.cost(n,s) + g.get(n));
							p.add(border, s);
						}
					}
				}
			}
		}
		
		if(success) return pere;
		return null;
	}
	
	
}
