package utils;


import java.util.Random;

/**
 * Class to handle some 2D computations, taken from differents sources. 
 *  
 * @author L. Simon, Univ. Paris Sud, 2008
 *
 */
public class Vector2d
{

    public Vector2d()
    {
        setZero();
    }

    public Vector2d(float x, float y)
    {
        set(x, y);
    }

    public void set(Vector2d that)
    {
        x = that.x;
        y = that.y;
    }

    public void set(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void setZero()
    {
        set(0.0F, 0.0F);
    }

    public boolean equals(Vector2d that)
    {
        return x == that.x && y == that.y ;
    }

    public boolean equalsZero()
    {
        return x == 0.0F && y == 0.0F;
    }

    public float magnitude()
    {
        return (float)Math.sqrt(magnitudeSquared());
    }

    public float distance(Vector2d that) {
    	return (float)Math.sqrt((x-that.x)*(x-that.x)+(y-that.y)*(y-that.y));
    }
    
    public float magnitudeSquared()
    {
        return x * x + y * y;
    }

    public float dot(Vector2d that)
    {
        return x * that.x + y * that.y;
    }

    public void setUnitRandom()
    {
        do
        {
            x = generator.nextFloat() * 2.0F - 1.0F;
            y = generator.nextFloat() * 2.0F - 1.0F;
        } while(magnitudeSquared() > 1.0F);
    }

 
    public void setSum(Vector2d a, Vector2d b)
    {
        x = a.x + b.x;
        y = a.y + b.y;
    }

    public void setDiff(Vector2d a, Vector2d b)
    {
        x = a.x - b.x;
        y = a.y - b.y;
    }

    public void setScale(float a, Vector2d b)
    {
        x = a * b.x;
        y = a * b.y;
    }

    public void setCross(Vector2d a, Vector2d b)
    {
        set(a.y * b.x - a.x * b.y, a.y * b.x - a.x * b.y);
    }

    public void setInterp(float blend, Vector2d v0, Vector2d v1)
    {
        x = v0.x + blend * (v1.x - v0.x);
        y = v0.y + blend * (v1.y - v0.y);
    }

    public void setNormalize()
    {
        float m = magnitude();
        if(m != 0.0F)
            setScale(1.0F / m, this);
    }

    public void setApproximateNormalize()
    {
        float m = magnitude();
        if(m != 0.0F)
            setScale(1.0F / m, this);
    }

    public void setApproximateTruncate(float threshold)
    {
        float length = magnitude();
        if(length > threshold)
            setScale(threshold / length, this);
    }
    
   
    public Vector2d scale(float factor)
    {
        return new Vector2d(x * factor, y * factor);
    }

    
    public Vector2d add(Vector2d other)
    {
        return new Vector2d(x + other.x, y + other.y);
    }

    public Vector2d subtract(Vector2d other)
    {
        return new Vector2d(x - other.x, y - other.y);
    }

 
    
    public static Vector2d unitRandom()
    {
        Vector2d v = new Vector2d(0.0F, 0.0F);
        do
        {
            v.x = generator.nextFloat() * 2.0F - 1.0F;
            v.y = generator.nextFloat() * 2.0F - 1.0F;
        } while(v.magnitudeSquared() > 1.0F);
        return v;
    }

    public float approximateLength()
    {
        float a = x;
        if(a < 0.0F)
            a = -a;
        float b = y;
        if(b < 0.0F)
            b = -b;
        if(a < b)
        {
            float t = a;
            a = b;
            b = t;
        }
        return a * 0.9375F + b * 0.375F;
    }
    
    public float approximateDistance(Vector2d v)
    {
        synchronized(distTemp)
        {
            distTemp.setDiff(this, v);
            float f = distTemp.approximateLength();
            return f;
        }
    }
    

    public float x;
    public float y;
    public static final Vector2d zero = new Vector2d();
    public static Random generator = new Random();
    private static Vector2d distTemp = new Vector2d();
    
    
    
    
    

}
