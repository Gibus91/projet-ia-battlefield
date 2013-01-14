package surface;
import utils.*;

//  ----------------------------------------------------------------------------
//  construct a PolylinePathway given the number of points (vertices),
//  an array of points, and a path radius.
public class PolylinePathway extends Pathway
{

    public void cachePathStats()
    {
        lengths = new float[points.length];
        normals = new Vector2d[points.length];
        for(int i = 1; i < points.length; i++)
        {
            normals[i] = new Vector2d();
            normals[i].setDiff(points[i], points[i - 1]);
            lengths[i] = normals[i].magnitude();
            normals[i].setScale(1.0F / lengths[i], normals[i]);
        }

    }

    public boolean mapPointToPath(Vector2d point, Vector2d onPath, Vector2d tangent)
    {
        float minDistance = 3.402823E+38F;
        for(int i = 1; i < points.length; i++)
        {
            segmentLength = lengths[i];
            segmentNormal = normals[i];
            float d = pointToSegmentDistance(point, points[i - 1], points[i]);
            if(d < minDistance)
            {
                minDistance = d;
                onPath.set(chosen);
                tangent.set(segmentNormal);
            }
        }

        return point.approximateDistance(onPath) < radius;
    }

    public float mapPointToPathDistance(Vector2d point)
    {
        float minDistance = 3.402823E+38F;
        float segmentLengthTotal = 0.0F;
        float pathDistance = 0.0F;
        for(int i = 1; i < points.length; i++)
        {
            segmentLength = lengths[i];
            segmentNormal = normals[i];
            float d = pointToSegmentDistance(point, points[i - 1], points[i]);
            if(d < minDistance)
            {
                minDistance = d;
                pathDistance = segmentLengthTotal + segmentProjection;
            }
            segmentLengthTotal += segmentLength;
        }

        return pathDistance;
    }

    public void mapPathDistanceToPoint(float pathDistance, Vector2d point)
    {
        float remainingDistance = pathDistance;
        for(int i = 1; i < points.length; i++)
        {
            segmentLength = lengths[i];
            if(segmentLength < remainingDistance)
            {
                remainingDistance -= segmentLength;
            } else
            {
                float ratio = remainingDistance / segmentLength;
                point.setInterp(ratio, points[i - 1], points[i]);
                return;
            }
        }

    }

    public float pointToSegmentDistance(Vector2d point, Vector2d ep0, Vector2d ep1)
    {
        local.setDiff(point, ep0);
        segmentProjection = segmentNormal.dot(local);
        if(segmentProjection < 0.0F)
        {
            chosen.set(ep0);
            segmentProjection = 0.0F;
            return point.approximateDistance(ep0);
        }
        if(segmentProjection > segmentLength)
        {
            chosen.set(ep1);
            segmentProjection = segmentLength;
            return point.approximateDistance(ep1);
        } else
        {
            chosen.setScale(segmentProjection, segmentNormal);
            chosen.setSum(chosen, ep0);
            return point.approximateDistance(chosen);
        }
    }

    public PolylinePathway(Vector2d[] points, float radius)
    {
		this.radius = radius;
		this.points = points;
		local = new Vector2d();
        chosen = new Vector2d();
        segmentNormal = new Vector2d();
        
        cachePathStats();
    }

    public float radius;
    public Vector2d points[];
    private float segmentLength;
    private float segmentProjection;
    private Vector2d local;
    private Vector2d chosen;
    private Vector2d segmentNormal;
    private float lengths[];
    private Vector2d normals[];
}
