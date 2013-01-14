package belette;

import java.awt.Color;
import java.awt.Graphics;

import utils.Vector2d;
import applets.BattleField;
import bots.IBot;

public class Balle implements IBelette {

	private	IBot firedby;
	private Vector2d velocity;
	private float power = 51F;
	private Vector2d coords;
	private float radius = 5F ;
	private Vector2d origcoords;
	private float portee;
	boolean active = true;
	public Balle(IBot fb,float p){
		firedby=fb;
		coords=new Vector2d(fb.getCoord().x,fb.getCoord().y);
		origcoords=new Vector2d(coords.x,coords.y);
		velocity=new Vector2d (fb.getAim().x,fb.getAim().y);
		velocity.setNormalize();
		velocity = velocity.scale(10F);
		active=true;
		portee=p;
		BattleField.bullets.add(this);
	}
	
	@Override
	public IBot firedBy() {
		return firedby;
	}

	@Override
	public Vector2d getVelocity() {
		return velocity;
	}

	@Override
	public float getPower() {
		return power;
	}

	@Override
	public Vector2d getCoords() {
		return coords;
	}

	@Override
	public float getRadius() {
		return radius;
	}

	@Override
	public void hitBot(IBot bot) {
		active=false;
		// TODO Auto-generated method stub

	}

	@Override
	public void hitWall(Vector2d impactCoords) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics g) {
		if(!active) return;
		Color cl;
		switch(firedby.getColor()){
		case BattleField.BLUE:
			cl=Color.BLUE;
			break;
		case BattleField.RED:
			cl=Color.RED;
			break;
		default: cl= Color.ORANGE;
		}
		
		g.setColor(cl);
		g.fillOval((int)(coords.x-radius),(int)( coords.y-radius), (int)(radius), (int)(radius));
		
	}

	@Override
	public void computeNextFrame() {
		if(coords.distance(origcoords)>portee) active=false;
		if(active) 
			coords=coords.add(velocity);

	}
	
	public boolean isActive(){
		return active;
	}

}
