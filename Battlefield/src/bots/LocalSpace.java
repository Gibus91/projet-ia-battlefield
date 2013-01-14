package bots;
import utils.Vector2d;



public class LocalSpace
{

    public LocalSpace()
    {
        setToIdentity();
    }

    public LocalSpace(Vector2d initialPosition)
    {
        setToIdentity();
        position = initialPosition;
    }

    public void setToIdentity()
    {
        position = new Vector2d(0.0F, 0.0F);
        forward = new Vector2d(0.0F, 0.0F);
        side = new Vector2d(1.0F, 0.0F);
        up = new Vector2d(0.0F, 1.0F);
    }

    public void globalizePosition(Vector2d local, Vector2d globalized)
    {
        synchronized(component)
        {
            globalizeDirection(local, globalized);
            globalized.setSum(globalized, position);
        }
    }

    public void globalizeDirection(Vector2d local, Vector2d globalized)
    {
        synchronized(component)
        {
            globalized.setScale(local.x, side);
            component.setScale(local.y, up);
            globalized.setSum(globalized, component);
        }
    }

    public void localizePosition(Vector2d global, Vector2d localized)
    {
        synchronized(component)
        {
            component.setDiff(global, position);
            localized.set(component.dot(side), component.dot(up));
        }
    }

    public Vector2d position;
    public Vector2d forward;
    public Vector2d side;
    public Vector2d up;
    static Vector2d component = new Vector2d();

}
