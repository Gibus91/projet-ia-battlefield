package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import belette.Balle;
import belette.IBelette;


import applets.BattleField;

import surface.Pathway;
import surface.PolylineObject;
import surface.PolylinePathway;
import surface.Surface;
import utils.AStar;
import utils.Noeud;
import utils.Vector2d;

public class Soldier extends IBot {

	private float epsilon;
	float radius;
	private int color;
	private static Surface surface ;

	ArrayList<IBot> seenEnemies;

	//variables pour le pathfinding
	Vector2d destination;
	Vector2d closestDestination;
	Vector2d acceleration;
	Noeud firstNode;
	Noeud currentNode;
	Noeud lastNode;
	HashMap<Noeud, Noeud> fathers;
	boolean lastNodeReach;
	private static boolean alreadyInited;
	private static ArrayList<Noeud> waypoints;
	private static HashMap<Noeud, Integer> waypointsToIndex;


	//variables utiles à la gestion des etats
	int currentState;
	IBot enemyEngaging;
	public static final int LookingForEnemies = 1;
	public static final int EngagingEnemy = 2;
	public static final int Firing = 3;
	public static final int Fleeing = 4;
	public static final float portee = 75F;
	private float life;
	private Vector2d drawaim;
	private Vector2d aim;

	//variables utilies au steering behavior
	 public boolean IsSteeringForSeparation = true; // True if the vehicle also try to avoid vehicles
	    
    public float predict;
    
    float separationStrength;
    float separationDistance;
    
    public Pathway path; // Path to follow
    
    Vector2d onPath = new Vector2d(); // Projection of futur position on path
    Vector2d target = new Vector2d(); // Position on path we should be in future
    Vector2d offset = new Vector2d();
    Vector2d tangent = new Vector2d();
    Vector2d overlap = new Vector2d();
    Vector2d steering = new Vector2d();
    Vector2d futurePosition = new Vector2d();
    Vector2d pathFollowSteer = new Vector2d();
    Vector2d separationSteer = new Vector2d();
    Vector2d arrivalSteer = new Vector2d();
    boolean withinPath;
    final Color vehLineColor = new Color(0.0F, 0.3F, 0.0F);
    final Color vehFillColor = new Color(0.5F, 1.0F, 0.5F);
    public boolean annotation;
    static float slowingDistance = 30F;
    boolean touch;



	public Soldier(Vector2d co, float rad, Surface s, int color){
		
		
		
	 	predict = 5F;
        separationStrength = 0.3F;
        separationDistance = 1.0F;
        annotation = true;
	    maxSpeed = 3F;
	    maxForce = 10F;
	       
		
		
		this.color = color;
		radius=rad;
		epsilon = 2 * rad;
		destination = null;
		closestDestination = null;
		maxSpeed=5F;
		surface=s;
		firstNode = null;
		currentNode = null;
		lastNode = null;
		fathers = null;
		lastNodeReach = false;
		currentState = LookingForEnemies;
		seenEnemies=new ArrayList<IBot>();
		life=100;

		aim=new Vector2d(1,0);
		if(!alreadyInited){
			createWaypoints();
			alreadyInited = true;
		}
	}

	@Override
	public Vector2d getCoord() {

		return  position;
	}
	
	
	
	

	@Override
	public float botRadius() {

		return radius;
	}

	@Override
	public void AI() {
		currentState = resolveState(currentState);
		switch(currentState){
		case LookingForEnemies :
			if(destination == null){
				choosePatrolDestination();
			}
			goTo();
			break;
		case Firing:
			//System.out.println("Piou piou");
			destination=enemyEngaging.getCoord();
			path = null;
			currentNode = null;
			lastNode = null;
			fathers = null;
			
			velocity=Vector2d.zero;
			aim=enemyEngaging.getCoord().subtract(getCoord());
			aim.setNormalize();
			if(new Random().nextFloat()<0.1f) new Balle(this,portee);


//			destination = getCoord().add(new Vector2d(aim.y,-aim.x).scale(10));

			goTo();
			break;
		case EngagingEnemy:
			if(enemyEngaging==null) break;
			//System.out.println("Engage enemy");
			destination=enemyEngaging.getCoord();
			goTo();
			break;
		}


		drawaim=getCoord().add(aim.scale(radius));


	}




	@Override
	public int getColor() {
	
		return this.color;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updatePosition() {
		update();
		
	}

	@Override
	public float getLife() {
	
		return life;
	}

	@Override
	public boolean hit(float power) {
		life-=power;
		if(life<0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isAlive() {
		return life>0;
	}

	public void draw(Graphics g, float scale)
	{
	    drawAnnotation(g, scale);
	    drawOutlinedCircle(getCoord(), botRadius(), vehLineColor, vehFillColor, g, scale);
	    steering.setApproximateTruncate(maxForce);
	    drawVector(steering, 20F, Color.blue, g, scale);
	    pathFollowSteer.setApproximateTruncate(maxForce);
	    drawVector(pathFollowSteer,5F, Color.green, g, scale);
	    if (IsSteeringForSeparation) {
	       separationSteer.setApproximateTruncate(maxForce);
	       drawVector(separationSteer,5F, Color.red, g, scale);
	    }        
	           
	    drawVector(velocity, 5F, Color.magenta, g, scale);
	    g.drawOval((int) (getCoord().x-radius - portee), (int)(getCoord().y-radius - portee), (int)(radius+portee)*2, (int)(radius+portee)*2);
		if(waypoints!=null)
			for(Noeud n : waypoints){
				g.fillOval((int)n.getPosition().x-3,(int) n.getPosition().y-3, 3*2, 3*2);
			}
	
		for(Noeud n : waypoints){
			for(Noeud neighbour : n.getMyNeighbours()){
				g.drawLine((int)n.getPosition().x, (int)n.getPosition().y, (int)neighbour.getPosition().x,(int)neighbour.getPosition().y);
			}
	
		}
		if(destination != null){
			g.setColor(Color.BLUE);
			g.fillOval((int) destination.x, (int) destination.y, 6, 6);
		}
	    
	}
	
	public void drawa(Graphics g, float scale) {
		if(destination != null){
			g.setColor(Color.GREEN);
			g.fillOval((int)destination.x - 3, (int)destination.y - 3, 6, 6);
		}

		switch (color){
		case BattleField.RED :
			g.setColor(Color.RED);
			break;
		case BattleField.BLUE :
			g.setColor(Color.BLUE);
			break;
		}
		g.fillOval((int) (getCoord().x-radius), (int)(getCoord().y-radius), (int)radius*2, (int)radius*2);
		g.drawOval((int) (getCoord().x-radius - portee), (int)(getCoord().y-radius - portee), (int)(radius+portee)*2, (int)(radius+portee)*2);


		if(waypoints!=null)
			for(Noeud n : waypoints){
				g.fillOval((int)n.getPosition().x-3,(int) n.getPosition().y-3, 3*2, 3*2);
			}

		for(Noeud n : waypoints){
			for(Noeud neighbour : n.getMyNeighbours()){
				g.drawLine((int)n.getPosition().x, (int)n.getPosition().y, (int)neighbour.getPosition().x,(int)neighbour.getPosition().y);
			}

		}
		g.setColor(Color.CYAN);
		g.drawLine((int)(getCoord().x),(int)( getCoord().y), (int)(drawaim.x),(int)( drawaim.y));
	}

	@Override
	public Vector2d getAim() {
	
		return aim;
	}

	public void update()
	{
		Vector2d tmp = new Vector2d(0.0f , 0.0f);
		if(path != null && destination != null){
			if(destination.distance(getCoord()) > slowingDistance){
				steeringForPathFollowing(pathFollowSteer);
				pathFollowSteer.setApproximateNormalize();
				tmp = pathFollowSteer;
			}
			else{
				path = null;
	    		steeringForArrival(arrivalSteer);
	            touch = touch | ((double)destination.approximateDistance(getCoord()) < 5.0D);
	            tmp = arrivalSteer;
			}
		}else if(destination != null){
			steeringForArrival(arrivalSteer);
	        touch = touch | ((double)destination.approximateDistance(getCoord()) < 0.1D);
	        tmp = arrivalSteer;
		}
	    if (IsSteeringForSeparation && false) {
	    	steeringForSeparation(separationSteer);
	    	separationSteer.setApproximateNormalize();
	    	separationSteer.setScale(separationStrength, separationSteer);
	    	steering.setSum(pathFollowSteer, separationSteer);
	    }
	    else
	    	steering = tmp;
	    if(steering.equalsZero())
	        steering.set(forward);
	    applyGlobalForce(steering); // Apply the steering force to the vehicle
	    super.update(); // Updates its position
	    enforceNonPenetrationConstraint(); // Ensures no overlap of vehicles
	    if(touch){
	    	path = null;
	    	fathers = null;
	    	destination = null;
	    	touch = false;
	    	lastNode = null;
	    	currentNode = null;
	    }
	}

	public void applyGlobalForce(Vector2d force)
	{
	    allForces.setSum(allForces, force);
	}

	public void draw(Graphics g) {
		if(destination != null){
			g.setColor(Color.GREEN);
			g.fillOval((int)destination.x - 3, (int)destination.y - 3, 6, 6);
		}
		switch (color){
		case BattleField.RED :
			g.setColor(Color.RED);
			break;
		case BattleField.BLUE :
			g.setColor(Color.BLUE);
			break;
		}
		g.fillOval((int) (getCoord().x-radius), (int)(getCoord().y-radius), (int)radius*2, (int)radius*2);
		g.drawOval((int) (getCoord().x-radius - portee), (int)(getCoord().y-radius - portee), (int)(radius+portee)*2, (int)(radius+portee)*2);
		if(waypoints!=null)
			for(Noeud n : waypoints){
				g.fillOval((int)n.getPosition().x-3,(int) n.getPosition().y-3, 3*2, 3*2);
			}
	
		for(Noeud n : waypoints){
			for(Noeud neighbour : n.getMyNeighbours()){
				g.drawLine((int)n.getPosition().x, (int)n.getPosition().y, (int)neighbour.getPosition().x,(int)neighbour.getPosition().y);
			}
	
		}
	}

	private void updateBulletColision() {
		for(IBelette b : BattleField.bullets){
			if(b.isActive() && !b.firedBy().equals(this) && b.getCoords().distance(getCoord())<b.getRadius()+radius){


				life-=b.getPower();
				b.hitBot(this);
				System.out.println("HIT");
			}
		}

	}

	private void choosePatrolDestination() {
		Random r = new Random();
		Vector2d nextPosition = new Vector2d(r.nextInt(surface.wxsize) , r.nextInt(surface.wysize));
		while(surface.isInside(new Vector2d(-1, -1), nextPosition)){
			nextPosition.x = r.nextInt(surface.wxsize);
			nextPosition.y = r.nextInt(surface.wysize);
		}
		destination = nextPosition;
	}



	public IBot chooseTarget(ArrayList<IBot> enemies){
		double distanceMin = Double.MAX_VALUE;
		IBot target = null;
		for(IBot e : enemies){
			double dist = getCoord().distance(e.getCoord());
			if( dist < distanceMin){
				target = e;
				distanceMin = dist;
			}
		}
		return target;
	}

	public ArrayList<IBot> sawEnemy(){
		return seenEnemies;
	}


	public boolean saw(IBot bot){
		return surface.cansee(getCoord(), bot.getCoord());
	}

	
	
	public void updateCloseEnemies(){
		if(seenEnemies!=null) seenEnemies.clear();
		if (getColor()==BattleField.BLUE){
			for(IBot i : BattleField.redTeam){
				if (saw(i)){
					seenEnemies.add(i);
					//System.out.println("Red Enemy seen");
				}
			}
		} else {
			if (getColor()==BattleField.RED)
			for(IBot i : BattleField.blueTeam){
				if (saw(i)){
						seenEnemies.add(i);
						//System.out.println("Blue Enemy seen");
					}
				}
		}
	}

	
	
	public int resolveState(int state){
		ArrayList<IBot> enemies;
		updateCloseEnemies();
		switch (state){
		case LookingForEnemies :
			enemies = sawEnemy();
			if(enemies != null){
				IBot enemy = chooseTarget(enemies);
				enemyEngaging = enemy; 
				if(enemyEngaging !=null &&  enemyEngaging.getCoord().distance(getCoord()) <= portee){
					return Firing;
				}
				else{
					return EngagingEnemy;
				}
			}
			return LookingForEnemies;
		case Firing :
			if(enemyEngaging == null){//si l'enemie que l'on suivait est mort on cherche de nouveaux enemies
				enemies = sawEnemy();
				if(enemies != null){
					IBot enemy = chooseTarget(enemies);
					enemyEngaging = enemy;
					if(enemy.getCoord().distance(getCoord()) <= portee){
						return Firing;
					}
					else{
						return EngagingEnemy;
					}
				}
			}
			else if(enemyEngaging.getCoord().distance(getCoord()) > portee){
				enemies = sawEnemy();
				if(enemies != null){
					IBot enemy = chooseTarget(enemies);
					if(enemy !=null && enemy.getCoord().distance(getCoord()) <= portee){
						enemyEngaging = enemy;
						return Firing;
					}
				}
				return EngagingEnemy;
			}
			return Firing;

		case EngagingEnemy :
			if(enemyEngaging==null) return LookingForEnemies;
			if(!saw(enemyEngaging)){
				enemies = sawEnemy();
				if(enemies != null){
					IBot enemy = chooseTarget(enemies);
					if(enemy.getCoord().distance(getCoord()) <= portee){
						enemyEngaging = enemy;
						return Firing;
					}
				}
			}
			if(enemyEngaging.getCoord().distance(getCoord()) <= portee+10) return Firing;
			return EngagingEnemy;

		case Fleeing :
			return Fleeing;

		default :
			return LookingForEnemies;
		}
	}



	public void goTo(){

		if(destination != null && path == null){
			if(currentNode == null){
				currentNode = getClosestWaypoint(getCoord());
			}
			if(lastNode == null){
				lastNode = getClosestWaypoint(destination);
			}
			
			SoldierParamPathFinding param = new SoldierParamPathFinding(lastNode);
			AStar astar = new AStar();
			fathers = astar.performe(currentNode, param);
			
			Noeud nextNode = lastNode;
			ArrayList<Noeud> nodePath = new ArrayList<Noeud>();
			while(currentNode != nextNode){
				nodePath.add(nextNode);
				nextNode = fathers.get(nextNode);
			}
			nodePath.add(currentNode);
			Vector2d[] points = new Vector2d[nodePath.size() + 1];
			for(int i = nodePath.size() - 1 ; i >=0 ; i--){
				points[nodePath.size() - 1 - i] = nodePath.get(i).getPosition();
			}
			points[points.length - 1] = destination;
			path = new PolylinePathway(points, radius);
		}
	}


    public void steeringForPathFollowing(Vector2d v)
    {
        futurePosition.setScale(predict,  velocity);
        float lead = futurePosition.approximateLength();
        futurePosition.setSum(futurePosition, getCoord());
        Vector2d intersect = surface.getClosestIntersectWithObjects(getCoord(), futurePosition);
        Vector2d avoidanceForce = new Vector2d(0.0f , 0.0f);
        if(intersect != null){
        	avoidanceForce.setDiff(getCoord(), intersect);
        }
        withinPath = path.mapPointToPath(futurePosition, onPath, tangent);
        float pathDistance = path.mapPointToPathDistance(getCoord());
        path.mapPathDistanceToPoint(pathDistance + lead, target);
        if(withinPath &&  forward.dot(tangent) > 0.0F)
        {
            v.setZero();
            return;
        } else
        {
            steeringForSeek(target, v);
            avoidanceForce.setScale(2 ,avoidanceForce);
            v.setSum(v, avoidanceForce);
            v.setApproximateTruncate( maxSpeed);
            return;
        }
    }

    public void steeringForSeparation(Vector2d accumulator)
    {
        accumulator.setZero();
        for(int i = 0 ; i < BattleField.nbTeam ; i++){
	        for(IBot other : BattleField.teams[i])
	            if(other != this)
	            {
	                offset.setDiff(getCoord(), other.getCoord());
	                float distance = offset.approximateLength();
	                if(distance < separationDistance)
	                {
	                    if(distance > 0.0F)
	                    {
	                        offset.setScale(1.0F / (distance * distance), offset);
	                        accumulator.setSum(accumulator, offset);
	                    }
	                }
	            }
        }

    }
    
    public void steeringForArrival(Vector2d v)
    {
        v.setDiff(destination, getCoord());
        float distance = v.approximateLength();
        float rampedSpeed = maxSpeed * (distance / slowingDistance);
        float clippedSpeed = Math.min(rampedSpeed, maxSpeed);
        v.setScale(clippedSpeed / distance, v);
        v.setDiff(v, velocity);
    }
    
    public void steeringForSeek(Vector2d target, Vector2d v)
    {
        v.setDiff(target, getCoord());
        v.setApproximateTruncate( maxSpeed);
        v.setDiff(v, velocity);
    }

    public void enforceNonPenetrationConstraint()
    {
    	for(int i = 0 ; i < BattleField.nbTeam ; i++){
	        for(IBot other : BattleField.teams[i])
	            if(other != this)
	            {
	                offset.setDiff(getCoord(), other.getCoord());
	                float distance = offset.approximateLength();
	                float sumOfRadii = botRadius() + other.botRadius();
	                if(distance < sumOfRadii)
	                {
	                    overlap.setDiff(getCoord(), other.getCoord());
	                    float s = (sumOfRadii - distance) / distance;
	                    overlap.setScale(s, overlap);
	                    getCoord().setSum(getCoord(), overlap);
	                }
	            }
    	}
    	for(PolylineObject p : surface.getObjects()){
    		if(surface.circleIntersectRectangle(getCoord(), botRadius(), p)){
    			System.out.println("error penetration");
    		}
    	}
    }
    
    
    

    public void drawAnnotation(Graphics g, float scale)
    {
        if(annotation)
        {
            if(!withinPath)
                drawLine(onPath, futurePosition, Color.red, g, scale);
            drawCircle(onPath, 0.35F, false, Color.red, g, scale);
            drawCircle(futurePosition, 0.25F, true, Color.red, g, scale);
            drawCircle(target, 0.45F, false, Color.white, g, scale);
            
            
            if(path != null){
            	for(Vector2d v : ((PolylinePathway)path).points){
            		
            		drawCircle(v, 8, true, Color.CYAN,g, scale);
            	}
            }
        }
    }

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/*	
	
	
	public Pathway path; // Path to follow
	Vector2d onPath; // Projection of futur position on path
	Vector2d target; // Position on path we should be in future
	Vector2d offset;
    Vector2d tangent;
    Vector2d overlap;
    Vector2d steering;
    Vector2d futurePosition;
    Vector2d pathFollowSteer;
    Vector2d separationSteer;
    float predict;
    boolean withinPath;
	float separationDistance;
	Vector2d allForces;
	Vector2d newAccel;
	boolean IsSteeringForSeparation;
	static float accelDamping = 0.7F;
	Vector2d accelUp;
	Vector2d bankUp;
	Vector2d up;
	Vector2d forward;
	Vector2d 
	
	
	public void steeringForPathFollowing(Vector2d v)
    {
        futurePosition.setScale(predict, vitesse);
        float lead = futurePosition.approximateLength();
        futurePosition.setSum(futurePosition, coords);
        withinPath = path.mapPointToPath(futurePosition, onPath, tangent);
        float pathDistance = path.mapPointToPathDistance(coords);
        path.mapPathDistanceToPoint(pathDistance + lead, target);
        if(withinPath)// && super.forward.dot(tangent) > 0.0F)
        {
            v.setZero();
            return;
        } else
        {
            steeringForSeek(target, v);
            return;
        }
    }

		public void steeringForSeparation(Vector2d accumulator)
		{
		    accumulator.setZero();
		    for(int i = 0 ; i < BattleField.nbTeam ; i++){
			    for(IBot other : BattleField.teams[i])
			        if(other != this)
			        {
			            offset.setDiff(coords, other.getCoord());
			            float distance = offset.approximateLength();
			            if(distance < separationDistance)
			            {
			                if(distance > 0.0F)
			                {
			                    offset.setScale(1.0F / (distance * distance), offset);
			                    accumulator.setSum(accumulator, offset);
			                }
			            }
			        }
		    }
		
		}

		
		public void steeringForSeek(Vector2d target, Vector2d v)
		{
		    v.setDiff(target, coords);
		    v.setApproximateTruncate(maxSpeed);
		    v.setDiff(v, vitesse);
		}
		
		


	    public void enforceNonPenetrationConstraint()
	    {
	    	for(int i = 0 ; i < BattleField.nbTeam ; i++){
		        for(IBot other : BattleField.teams[i])
		            if(other != this)
		            {
		                offset.setDiff(coords, other.getCoord());
		                float distance = offset.approximateLength();
		                float sumOfRadii = botRadius() + other.botRadius();
		                if(distance < sumOfRadii)
		                {
		                    overlap.setDiff(coords, other.getCoord());
		                    float s = (sumOfRadii - distance) / distance;
		                    overlap.setScale(s, overlap);
		                    coords.setSum(coords, overlap);
		                }
		            }
	    	}

	    }

	
*/

	//	private void goToPoint(Vector2d dest, boolean stopAfter){
	//		
	//		if(dest.distance(coords)<50){
	//			directGoToPoint(dest, stopAfter);
	//			return;
	//		}
	//		Vector2d futureCoords = new Vector2d();
	//		futureCoords.setScale(4F, vitesse);
	//		if(!surface.cansee(coords, coords.add(vitesse))){
	//			vitesse = Vector2d.zero;
	//			return;
	//		}
	//		
	//		if(distanceToStop > dest.distance(coords)){
	//			
	////			vitesse.setDiff(dest, coords);
	//			float distance = dest.distance(coords);
	//			float rampedSpeed = maxvelocity * (distance / 200);
	//			float clippedSpeed = Math.min(rampedSpeed, maxvelocity);
	//			acceleration.setScale(clippedSpeed / distance, acceleration);
	//			vitesse.setDiff(acceleration, vitesse);
	//		}
	//		else {
	//			acceleration.setDiff(dest, coords);
	//			acceleration.setNormalize();
	//			acceleration.setScale(maxAcceleration, acceleration);
	//			vitesse.setSum(vitesse, acceleration);
	//			
	//			if (vitesse.magnitude() > maxvelocity) {
	//				vitesse.setNormalize();
	//				vitesse.setScale(maxvelocity, vitesse);
	//			}
	//		}
	//		
	//		
	//		
	//	}




	private void createWaypoints() {
		System.out.println("creating waypoints");
		waypoints = new ArrayList<Noeud>();
		waypointsToIndex = new HashMap<Noeud, Integer>();

		int pas = 30;
		//on quadrille de waypoints
		for(int i=0;i<surface.wxsize;i+=pas){
			for(int j=0;j<surface.wysize;j+=pas){
				Vector2d a = new Vector2d();
				a.set(-5f,-5f);

				Vector2d b = new Vector2d(i+0.01f, j+0.01f);
				if(!surface.isInside(a,b)){
					Noeud toAdd = new  Noeud(b);
					waypoints.add(toAdd);
				}
			}
		}

		//on encadre les polygones de waypoints
		for(PolylineObject o : surface.getObjects()){
			Vector<Vector2d> homotethie = new Vector<Vector2d>();
			Vector2d A;
			Vector2d B;
			Vector2d C;

			//ajout des waypoints représentant les sommets de la figure agrandie.
			for(int i = 0; i<o.nbPoints ; i++){
				if(i == 0 ){
					A = o.globalCoordPoints.get(o.nbPoints-1);
					B = o.globalCoordPoints.get(i);
					C = o.globalCoordPoints.get(i+1);
				}
				else if( i == o.nbPoints - 1){
					A = o.globalCoordPoints.get(i-1);
					B = o.globalCoordPoints.get(i);
					C = o.globalCoordPoints.get(0);
				}
				else{
					A = o.globalCoordPoints.get(i-1);
					B = o.globalCoordPoints.get(i);
					C = o.globalCoordPoints.get(i+1);
				}
				Vector2d comp1 = new Vector2d(B.x - A.x , B.y - A.y);
				comp1.setNormalize();
				Vector2d comp2 =  new Vector2d(B.x - C.x , B.y - C.y);
				comp2.setNormalize();

				Vector2d bisectrice = new Vector2d(comp1.x + comp2.x , comp1.y + comp2.y);
				bisectrice.setNormalize();
				bisectrice.setScale((float) epsilon, bisectrice);
				Vector2d result = new Vector2d(B.x+bisectrice.x, B.y+bisectrice.y);
				homotethie.add(i , result);
				Noeud toAdd = new  Noeud(result);
				waypoints.add(toAdd);
			}
			//ajout de waypoints le long des arrètes 
			for(int i = 0 ; i < homotethie.size() ; i++){
				A = homotethie.get(i);
				if( i != homotethie.size() - 1 ){
					B = homotethie.get(i + 1);
				}
				else{
					B = homotethie.get(0);
				}
				Vector2d vectToAdd = new Vector2d(B.x - A.x, B.y - A.y);
				vectToAdd.setNormalize();
				float distance = (float) Math.sqrt( Math.pow((B.x - A.x), 2) + Math.pow((B.y - A.y), 2));
				int k = 1;
				double ecartement = distance / (double) ((int) (distance/pas));
				vectToAdd.setScale((float)ecartement, vectToAdd);
				while(distance >= (k) * ecartement){
					Vector2d result = new Vector2d(A.x+ k * vectToAdd.x, A.y + k * vectToAdd.y);
					Noeud toAdd = new  Noeud(result);
					waypoints.add(toAdd);
					k++;
				}
			}
		}

		//on supprime les waypoints qui se retrouve dans un polygone
		Vector2d out1 = new Vector2d(-10, -10);
		Vector2d out2 = new Vector2d(-10, -3);
		for(int i = 0 ; i < waypoints.size() ; i++){
			Noeud n =  waypoints.get(i);
			if(surface.isInside(out1, n.getPosition()) || surface.isInside(out2, n.getPosition())){
				waypoints.remove(i);
			}
		}
		//on ajoute les liaisons entre waypoints et on remplie la hashmapWaypointsToIndex
		int indexwti = 0;
		for(Noeud n1  : waypoints){
			waypointsToIndex.put(n1, indexwti);
			indexwti++;
			for(Noeud n2  : waypoints){
				if(n1.getDistance(n2)<pas*1.5){
					if(surface.cansee(n1.getPosition() ,n2.getPosition() ))
						n1.getMyNeighbours().add(n2);
				}
			}
		}



		//		//on applique dijkstra pour remplir le tableau des
		//		int nbWaypoints = waypoints.size();
		//		waypointsToWaypoints = new Integer[nbWaypoints][nbWaypoints];
		//		waypointsToWaypointsDistance = 	new Double[nbWaypoints][nbWaypoints];	
		//		
		//		for(int i = 0 ; i < nbWaypoints ; i++ ){
		//			Double[] distanceIToJ = new Double[nbWaypoints];
		//			Integer[] tabPere = new Integer[nbWaypoints];
		//			//indexToVisit.get(1)
		//			HashSet<Integer> indexToVisit = new HashSet<Integer>();
		//			for(int j = 0 ; j < nbWaypoints ; j++){
		//				distanceIToJ[j] = -1.0;
		//				tabPere[j] = i;
		//			}
		//			distanceIToJ[i] = 0.0;
		//			tabPere[i] = -1;
		//			indexToVisit.add(i);
		//			int toVisit = i;
		//			while(!indexToVisit.isEmpty()){
		//				indexToVisit.remove(toVisit);
		//				Noeud visiting = waypoints.get(toVisit);
		//				int currentIndex;
		//				double currentDistance;
		//				for(Noeud n : visiting.getMyNeighbours() ){
		//					currentIndex = waypointsToIndex.get(n);
		//					currentDistance = visiting.getDistance(n);
		//					if(distanceIToJ[currentIndex] == - 1 || 
		//							distanceIToJ[currentIndex] > currentDistance + distanceIToJ[toVisit]){
		//						
		//						distanceIToJ[currentIndex] = currentDistance + distanceIToJ[toVisit];
		//						tabPere[currentIndex] = toVisit;
		//						indexToVisit.add(currentIndex);
		//					}
		//				}
		//				double min = -1;
		//				for(Integer index : indexToVisit){
		//					if(distanceIToJ[index] < min || min == -1){
		//						min =distanceIToJ[index];
		//						toVisit = index;
		//					}
		//				}
		//			}
		//			waypointsToWaypoints[i] = tabPere;
		//			waypointsToWaypointsDistance[i] = distanceIToJ;
		//		}
		//		double timeE = System.nanoTime();
		//		System.out.println("waypoints Created in "+(timeE-timeB) / Math.pow(10.0, 9.0) + " secondes");
	}

	

/*

	@Override
	public void updatePosition() {
		steeringForPathFollowing(pathFollowSteer);
        pathFollowSteer.setApproximateNormalize();
        if (IsSteeringForSeparation) {
        	steeringForSeparation(separationSteer);
        	separationSteer.setApproximateNormalize();
        	separationSteer.setScale(separationStrength, separationSteer);
        	steering.setSum(pathFollowSteer, separationSteer);
        }
        else
        	steering = pathFollowSteer;
        if(steering.equalsZero())
            steering.setZero();
        applyGlobalForce(steering); // Apply the steering force to the vehicle
       
        
        

        allForces.setApproximateTruncate(maxForce);
        // Here we should pay attention of the kind of ground (road, sand, ...)
       // And add a deceleration force if needed.
       if(mass == 1.0F)
           newAccel.set(allForces);
       else
           newAccel.setScale(1.0F / mass, allForces);
       allForces.setZero();
       acceleration.setInterp(accelDamping, newAccel, acceleration);
       vitesse.setSum(vitesse, acceleration);
       vitesse.setApproximateTruncate(maxSpeed);
       coords.setSum(coords, vitesse);
       accelUp.setScale(0.5F, acceleration);
       bankUp.setSum(up, accelUp);
       bankUp.setSum(bankUp, up);
       bankUp.setNormalize();
       float speed = vitesse.magnitude();
       if(speed > 0.0F)
       {
           super.forward.setScale(1.0F / speed, vitesse);
           super.side.setCross(super.forward, bankUp);
           super.up.setCross(super.side, super.forward);
       }
        
        
        
        
        
        
        enforceNonPenetrationConstraint(); // Ensures no overlap of vehicles

	}
*/

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Vector2d destination) {
		this.destination = destination;
	}

	public Noeud getClosestWaypoint(Vector2d position){
		double min = -1;
		Noeud result = null;
		for(Noeud n : waypoints){
			double dist = n.getPosition().distance(position);
			if(min == -1 || dist < min){
				result = n;
				min = dist;
			}
		}
		return result;
	}



}
