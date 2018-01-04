class Spawnevent {

	ArrayList<Flower> list;

	Timer myTimer = new Timer(3);

	PVector spawnPoint = new PVector(int(random(maxX)), int(random(maxY)), int(random(lowerZ, upperZ)));

	PApplet main;

	boolean dead = false;

	boolean spawned = false;

	public Spawnevent(Flower a, Flower b, PApplet m) {
		main = m;
		list = new ArrayList<Flower>();
		list.add(a);
		list.add(b);
		myTimer.start();
	}

	public Spawnevent(Flower a, Flower b, PApplet m, PVector pos) {
		main = m;
		list = new ArrayList<Flower>();
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
		  for(Flower f : list) {
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
	  	  	spawnFlower();
	  	  	spawned = true;
	  	  }
	  	}
	}

	void spawnFlower() {
		//what kind of big lev should be spawned?
		int r = int(random(3));
		switch (r) {
			case 0: 
			levs.addSingleFlower(new FlowerD(spawnPoint, soundStringsEnlight[int(random(soundStringsEnlight.length-1))], main));
			break;

			case 1: 
			levs.addSingleFlower(new FlowerE(spawnPoint, soundStringsEnlight[int(random(soundStringsEnlight.length-1))], main));
			break;

			case 2: 
			levs.addSingleFlower(new FlowerF(spawnPoint, soundStringsEnlight[int(random(soundStringsEnlight.length-1))], main));
			break;
			
			default:
			levs.addSingleFlower(new FlowerF(spawnPoint, soundStringsEnlight[int(random(soundStringsEnlight.length-1))], main));
		}

		dead = true;
	}

}