class Spawnevent {

	ArrayList<Lev> list;

	Timer myTimer = new Timer(3);

	PVector spawnPoint = new PVector(int(random(maxX)), int(random(maxY)), int(random(lowerZ, upperZ)));

	PApplet main;

	boolean dead = false;

	boolean spawned = false;

	public Spawnevent(Lev a, Lev b, PApplet m) {
		main = m;
		list = new ArrayList<Lev>();
		list.add(a);
		list.add(b);
		myTimer.start();
	}

	public Spawnevent(Lev a, Lev b, PApplet m, PVector pos) {
		main = m;
		list = new ArrayList<Lev>();
		list.add(a);
		list.add(b);
		myTimer.start();
		spawnPoint.set(pos);
	}

	void run() {
		//check if spawnEvent is dead (=big lev has been spawned)
		if(dead) {
			addMeToToDelete(this);
		}
		drawLines();
	}

	void drawLines() {
		  float scalar = myTimer.progress();
		  if(list != null && list.size() == 2) {
		  for(Lev f : list) {
			  stroke(f.curColor, f.faderOpacity+0.2);
		      float cX = spawnPoint.x;
		      float cY = spawnPoint.y;
		      float cZ = spawnPoint.z;
		      cX = scalar * (spawnPoint.x - f.pos.x) + f.pos.x;
		      cY = scalar * (spawnPoint.y - f.pos.y) + f.pos.y;
		      cZ = scalar * (spawnPoint.z - f.pos.z) + f.pos.z;
		      line(cX, cY, cZ, f.pos.x, f.pos.y, f.pos.z);
	  	  }
	  	  //check if big lev should be spawned
	  	  if(scalar >= 1 && !spawned) {
	  	  	spawnLev();
	  	  	spawned = true;
	  	  }
	  	}
	}

	void spawnLev() {
		//what kind of big lev should be spawned?
		int r = int(random(3));
		switch (r) {
			case 0: 
			levs.addSingleLev(new LevD(spawnPoint, soundStringsEnlight[int(random(soundStringsEnlight.length-1))], main));
			break;

			case 1: 
			levs.addSingleLev(new LevE(spawnPoint, soundStringsEnlight[int(random(soundStringsEnlight.length-1))], main));
			break;

			case 2: 
			levs.addSingleLev(new LevF(spawnPoint, soundStringsEnlight[int(random(soundStringsEnlight.length-1))], main));
			break;
			
			default:
			levs.addSingleLev(new LevF(spawnPoint, soundStringsEnlight[int(random(soundStringsEnlight.length-1))], main));
		}

		dead = true;
	}

}