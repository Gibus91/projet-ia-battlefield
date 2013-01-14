package surface;

import utils.Vector2d;
import java.util.*;
import java.awt.*;

/**
 * A simple class to handle the surface itself. All objects are polylines.
 * 
 * @author L. Simon, Univ. Paris Sud, 2008.
 *
 */
public class Surface {

	

	public int wxsize, wysize;

	// All objects on the surface are recorded in this vector of polylines.
	private Vector<PolylineObject> objects; // The objects on the surface

	/**
	 * @return the objects
	 */
	public Vector<PolylineObject> getObjects() {
		return objects;
	}

	/**
	 * Well, right now the objects are built "by hands". May by the first
	 * thing to do would be to put polylines objects in a map, and read the
	 * file and objects.
	 * 
	 * In this case, the size of the surface should be set inside the constructor?
	 * 
	 * @param wxsize
	 * @param wysize
	 * @param scale
	 */
	public Surface( int wxsize, int wysize, float scale) {
		this.wxsize = wxsize;
		this.wysize = wysize;
		objects = new Vector<PolylineObject>();
		
		
	}

	/**
	 * Draws all objects on the surface.
	 * 
	 * @param g
	 */
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		for(int i=0;i<objects.size();i++) {
			objects.get(i).draw(g);
		}
	}

	/**
	 * One of the main methods. Checks if the segment (tmpA, tmpB)
	 * intersects any of the lines of any polyline. If not, then
	 * the point tmpA 'can see' the point tmpB.
	 * 
	 * @param tmpA
	 * @param tmpB
	 * @return true if tmpA can see tmpB
	 */
	public boolean cansee(Vector2d tmpA, Vector2d tmpB) {
		for(int i=0;i<objects.size();i++) {
			if (objects.get(i).intersectsWith(tmpA, tmpB))
				return false;
		}
		return true;

	}
	
	/**
	 * One of the main methods. Checks if the segment (tmpA, tmpB)
	 * finish inside any of the lines of any polyline. If not, then
	 * the point tmpA 'can see' the point tmpB.
	 * 
	 * @param tmpA
	 * @param tmpB
	 * @return true if tmpA can see tmpB
	 */
	public boolean isInside(Vector2d tmpA, Vector2d tmpB) {
		for(int i=0;i<objects.size();i++) {
			if(objects.get(i).nbIntersectionWith(tmpA , tmpB) % 2 == 1){
				return true;
			}
		}
		return false;
	}

	public boolean circleIntersectRectangle(Vector2d center, float radius, PolylineObject p){
		float xa = center.x;
		float ya = center.y;
		for(int i = 0 ; i < 3 ; i+=2){
			float yb = p.globalCoordPoints.get(i).y;
			double delta1 = (float) (Math.pow(2 * xa, 2.0) - 4 *(xa*xa+ya*ya+yb*yb-2*ya*yb));
			if(delta1 > 0) return true;
			float xb = p.globalCoordPoints.get(i).x;
			double delta2 = (float) (Math.pow(2 * ya, 2.0) - 4 *(ya*ya+xa*xa+xb*xb-2*xa*xb));
			if(delta2 > 0) return true;
		}
		return false;
	}
	
	
	public Vector2d getClosestIntersectWithObjects(Vector2d a , Vector2d b){
		Vector2d result = null;
		float precDist = Float.MAX_VALUE;
		Vector2d tmpResult = null;
		for(PolylineObject p : objects){
			p.closestPointOfIntersectionWith(a, b, tmpResult);
			if(tmpResult != null && tmpResult.distance(a) < precDist){
				result = new Vector2d(tmpResult.x, tmpResult.y);
			}
		}
		return result;
	}


}

