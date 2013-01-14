package surface;
import utils.Vector2d;


// ----------------------------------------------------------------------------
//
//
// Pathway and PolylinePathway, for path following.
//

public abstract class Pathway
{

//	 ----------------------------------------------------------------------------
//	 Given an arbitrary point ("A"), returns the nearest point ("P") on
//	 this path.  Also returns, via output arguments, the path tangent at
//	 P and a measure of how far A is outside the Pathway's "tube".  Note
//	 that a negative distance indicates A is inside the Pathway.
   public abstract boolean mapPointToPath(Vector2d vector2, Vector2d vector2_1, Vector2d vector2_2);

// ----------------------------------------------------------------------------
// given a distance along the path, convert it to a point on the path
   public abstract void mapPathDistanceToPoint(float f, Vector2d vector2);

// ----------------------------------------------------------------------------
// given an arbitrary point, convert it to a distance along the path
    public abstract float mapPointToPathDistance(Vector2d vector2);

    public Pathway()
    {
    }
}
