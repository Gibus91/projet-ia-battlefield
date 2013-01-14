package surface;

import utils.Vector2d;
import java.util.*;
import java.awt.Graphics;
import java.awt.Polygon;
import java.lang.reflect.*;

/**
 * Allow to handle any polyline object on the map... No Splines yet... And 2D :(
 * 
 * @author L. Simon, Univ. Paris Sud, 2008
 */
public class PolylineObject {
	
	public int nbPoints;
	public Vector2d coordFirstPoint;
    private boolean visible;
    static private Vector2d tmpvect = new Vector2d();
    
    // Some cached values
    public Vector<Vector2d> globalCoordPoints; 
	private Polygon cachedPolygon;
	/**
	 * @param AnchorPoint The global coordonates of the anchor point in the surface
	 * @param scale a scaling factor applied to all local coordinates
	 */
	public PolylineObject(Vector2d AnchorPoint, Surface onsurface) {
		nbPoints = 0;
		visible = true;
		globalCoordPoints = new Vector<Vector2d>();
		globalCoordPoints.add(new Vector2d(AnchorPoint.x,AnchorPoint.y));
		nbPoints ++;
	}
	
	public void addNode(Vector2d localCoord) {
		globalCoordPoints.add(localCoord);
		nbPoints ++;
	}
	
	public void fixObject() {
		cachedPolygon = new Polygon();
		for (int i=0; i< nbPoints;i++) {
			cachedPolygon.addPoint((int)(globalCoordPoints.get(i).x) , 
					(int)(globalCoordPoints.get(i).y ));
		}
	}
	
	
	private boolean twoSegmentsIntersection(Vector2d A, Vector2d B, Vector2d C, Vector2d D, Vector2d result)
    {
        double numR = (A.y-C.y)*(D.x-C.x)-(A.x-C.x)*(D.y-C.y);
        double denR = (B.x-A.x)*(D.y-C.y)-(B.y-A.y)*(D.x-C.x);

        double numS = (A.y-C.y)*(B.x-A.x)-(A.x-C.x)*(B.y-A.y);
        double denS = (B.x-A.x)*(D.y-C.y)-(B.y-A.y)*(D.x-C.x);

        if( denR == 0 || denS == 0 )
            return false;
        
        double s = numS/denS;
        double r = numR/denR;
        
        if( 0<=r && r<=1 && 0<=s && s<=1 )
            return true;
        
        return false;
    }

	
	private boolean twoSegmentsIntersectionOld(Vector2d A, Vector2d B, Vector2d C, Vector2d D, Vector2d result) {
		double Sx;
		double Sy;

		if(A.x==B.x) {
			if(C.x==D.x)
				return false;
			else {
				double pCD = (C.y-D.y)/(C.x-D.x);
				Sx = A.x;
				Sy = pCD*(A.x-C.x)+C.y;
			}
		}
		else {
			if(C.x==D.x) {
				double pAB = (A.y-B.y)/(A.x-B.x);
				Sx = C.x;
				Sy = pAB*(C.x-A.x)+A.y;
			}
			else {
				double pCD = (C.y-D.y)/(C.x-D.x);
				double pAB = (A.y-B.y)/(A.x-B.x);
				double oCD = C.y-pCD*C.x;
				double oAB = A.y-pAB*A.x;
				Sx = (oAB-oCD)/(pCD-pAB);
				Sy = pCD*Sx+oCD;
			}
		}
		if((Sx<A.x && Sx<B.x)||(Sx>A.x && Sx>B.x) || (Sx<C.x && Sx<D.x)|(Sx>C.x && Sx>D.x)
				|| (Sy<A.y && Sy<B.y)||(Sy>A.y && Sy>B.y) || (Sy<C.y && Sy<D.y)||(Sy>C.y && Sy>D.y)) 
			return false;
		result.set((float) Sx, (float)Sy);
		return true;
	}
	
	/**
	 * Return True if the segment intersects the polyline object.
	 * Note: We consider Vector3 as Vector2 vectors only here (z=0);
	 * 
	 * @param pointA
	 * @param pointB
	 * @return the point of intersection (via result) and true, if...
	 */
	public boolean closestPointOfIntersectionWith(Vector2d pointA, Vector2d pointB, Vector2d result) {
			float bestLength = -1F;
			boolean interesects = false;
			Vector2d tmp = new Vector2d();
			result = new Vector2d();
			for (int i=0; i<nbPoints;i++) {
				if (twoSegmentsIntersection(pointA, pointB, 
						globalCoordPoints.get(i), globalCoordPoints.get(i+1>=nbPoints?0:i+1), tmp)) {
					interesects = true;
					float newLength = pointA.distance(tmp);
					if ((bestLength < 0F) || (newLength < bestLength)) {
						bestLength = newLength;
						result.set(tmp);
					}
				}
			}	
			return interesects;
	}
	
	
	
	/**
	 * Return the number of intersection between the segment and the polyline object.
	 * Note: We consider Vector3 as Vector2 vectors only here (z=0);
	 * 
	 * @param pointA
	 * @param pointB
	 * @return the number of intersection between the segment and the polyline object.
	 */
	public int nbIntersectionWith(Vector2d pointA, Vector2d pointB) {
			int  nbInteresects = 0;
			for (int i=0; i<nbPoints;i++) {
				if (twoSegmentsIntersection(pointA, pointB, 
						globalCoordPoints.get(i), globalCoordPoints.get(i+1>=nbPoints?0:i+1), tmpvect)) {
					nbInteresects++;
				}
			}	
			return nbInteresects;
	}
	
	
	

	public boolean intersectsWith(Vector2d pointA, Vector2d pointB) {
		return closestPointOfIntersectionWith(pointA,pointB, tmpvect);
	}
	
	
    
	public Vector2d getCoordFirstPoint() {
		return coordFirstPoint;
	}
	

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}    

	public void draw(Graphics g) {
		g.fillPolygon(cachedPolygon);
		//g.drawPolygon(cachedPolygon.xpoints, cachedPolygon.ypoints, cachedPolygon.npoints);
	}
	   
}
