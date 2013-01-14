package bots;

import java.awt.Color;
import java.awt.Graphics;

import utils.Vector2d;

/**
 * Petite interface "indicative" de ce qu'il peut y avoir dans un bot.
 * Il faut aussi g�rer l'ensemble des bots du jeu dans une structure � part.
 *  
 * @author L. Simon, Univ. Paris Sud, 2008
 *
 */
public abstract class IBot extends LocalSpace{
	
	/**
	 * @return Les coordonn�es du bot, ou -1,-1 si pas sur la carte par ex.
	 */
	public abstract Vector2d getCoord();
	
	/**
	 * On va repr�senter les bots en rond... Pour les collisions sur la carte,
	 * on a besoin du rayon du bot
	 * @return le rayon (en coordonn�es de cartes) du bot
	 */
	public abstract float botRadius();
	
	/**
	 * Affiche le bot sur le dessin... A vos id�es !
	 * @param g
	 */
	public abstract void draw(Graphics g, float Scale);
	
	/**
	 * @return la chaine representant le bot et son etat, pour debug ou autre
	 */
	public abstract String toString();
	
	/**
	 * Mise � jour IA
	 * des decisions � prendre 
	 */
	public abstract void AI();

	/**
	 * Suivant ses decisions et les autres, le bot peut mettre a jour ses positions
	 */
	public abstract void updatePosition();
	
	/**
	 * Renvoie la vie du bot
	 * @return
	 */
	public abstract float getLife();
	
	/**
	 * Touche le bot et lui enleve de la vie
	 * @param power la puissance 
	 * @return true si mort, false sinon
	 */
	public abstract boolean hit(float power);
	
	/**
	 * Renvoie si le bot est en vie
	 * @return
	 */
	public abstract boolean isAlive();
	
	/**
	 * Renvoie ou le bot regarde (pour tirer)
	 * @return
	 */
	public abstract Vector2d getAim();
	
	/**
	 * 
	 * @return la couleur de l'équipe
	 */
	public abstract int getColor();
	
	
	
	


    public IBot()
    {
        acceleration = new Vector2d();
        mass = 1.0F;
        maxSpeed = 2.0F;
        maxForce = 10.0F;
        velocity = new Vector2d();
        allForces = new Vector2d();
    }

    public void applyGlobalForce(Vector2d force)
    {
        allForces.setSum(allForces, force);
    }

    public void update()
    {
        allForces.setApproximateTruncate(maxForce);
         // Here we should pay attention of the kind of ground (road, sand, ...)
        // And add a deceleration force if needed.
        if(mass == 1.0F)
            newAccel.set(allForces);
        else
            newAccel.setScale(1.0F / mass, allForces);
        allForces.setZero();
        acceleration.setInterp(accelDamping, newAccel, acceleration);
        velocity.setSum(velocity, acceleration);
        velocity.setApproximateTruncate(maxSpeed);
        super.position.setSum(super.position, velocity);
        accelUp.setScale(0.5F, acceleration);
        bankUp.setSum(super.up, accelUp);
        bankUp.setSum(bankUp, globalUp);
        bankUp.setNormalize();
        float speed = velocity.magnitude();
        if(speed > 0.0F)
        {
            super.forward.setScale(1.0F / speed, velocity);
            super.side.setCross(super.forward, bankUp);
            super.up.setCross(super.side, super.forward);
        }
    }
    
    public float random01()
    {
        return Vector2d.generator.nextFloat();
    }

    public void drawVector(Vector2d v, float vscale, Color c, Graphics g, float dscale)
    {
        drawSteer.setScale(vscale, v);
        drawSteer.setSum(super.position, drawSteer);
        drawLine(super.position, drawSteer, c, g, dscale);
        drawCircle(drawSteer, .2F, false, c, g, dscale);
    }

    public void drawLine(Vector2d v0, Vector2d v1, Color c, Graphics g, float dscale)
    {
        g.setColor(c);
        g.drawLine((int)(dscale * v0.x), (int)(dscale * v0.y), (int)(dscale * v1.x), (int)(dscale * v1.y));
    }

    public void drawCircle(Vector2d center, float radius, boolean filled, Color c, Graphics g, float scale)
    {
        int r = (int)(scale * radius);
        int diameter = r * 2;
        int top = (int)(scale * center.y - (float)r);
        int left = (int)(scale * center.x - (float)r);
        g.setColor(c);
        if(filled)
        {
            g.fillOval(left, top, diameter, diameter);
            return;
        } else
        {
            g.drawOval(left, top, diameter, diameter);
            return;
        }
    }

    public void drawOutlinedCircle(Vector2d center, float radius, Color fill, Color outline, Graphics g, float scale)
    {
        int r = (int)(scale * radius);
        int diameter = r * 2;
        int top = (int)(scale * center.y - (float)r);
        int left = (int)(scale * center.x - (float)r);
        g.setColor(outline);
        g.fillOval(left, top, diameter, diameter);
        g.setColor(fill);
        g.drawOval(left, top, diameter, diameter);
    }

    public void drawTarget(Vector2d center, Color c, Graphics g, float scale)
    {
        int radius = (int)(scale / 2.0F);
        int diameter = (int)(scale - 1.0F);
        int tx = (int)(scale * center.x);
        int ty = (int)(scale * center.y);
        g.setColor(c);
        g.drawOval(tx - radius, ty - radius, diameter, diameter);
        g.drawLine(tx + diameter, ty, tx - diameter, ty);
        g.drawLine(tx, ty + diameter, tx, ty - diameter);
    }

    public void drawRect(float left, float top, float right, float bot, Color c, Graphics g, float scale)
    {
        float l;
        float r;
        if(left < right)
        {
            l = left;
            r = right;
        } else
        {
            l = right;
            r = left;
        }
        float t;
        float b;
        if(top < bot)
        {
            t = top;
            b = bot;
        } else
        {
            t = bot;
            b = top;
        }
        g.setColor(c);
        g.fillRect((int)(scale * l), (int)(scale * t), (int)(scale * (r - l)) + 1, (int)(scale * (b - t)) + 1);
    }

    
    
    static float radius = 0.5F;
    private static Vector2d drawSteer = new Vector2d();
    public float mass;
    public float maxSpeed;
    public float maxForce;
    public Vector2d velocity;
    public Vector2d allForces;
    public Vector2d acceleration;
    static Vector2d accelUp = new Vector2d();
    static final Vector2d globalUp = new Vector2d(0.0F, 0.1F);
    static Vector2d bankUp = new Vector2d();
    static Vector2d newAccel = new Vector2d();
    static float accelDamping = 0.7F;


	
	
}
