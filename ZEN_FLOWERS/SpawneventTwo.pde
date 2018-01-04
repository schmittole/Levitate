class SpawneventTwo {
	
	PVector one = new PVector();

	PVector two = new PVector();

	Flower f;

	Timer myTimer = new Timer(3);

	boolean dead = false;

	boolean spawned = false;

	ArrayList<PVector> list = new ArrayList<PVector>();

	public SpawneventTwo(Flower mf) {
		f = mf;

		list.add(randomPoint());

		//ckeck if add some more vectors to spanw from (up to 3)
		int r = int(random(5));
		if(r > 1) {
			list.add(randomPoint());
		}
		if(r > 3) {
			list.add(randomPoint());
		}
		myTimer = f.soundTimer;
	}

	PVector randomPoint() {
      return new PVector(int(random(width)), int(random(height)), int(random(lowerZ, upperZ)));
    }

	void run() {
		//check if spawnEvent is dead (= new levs have been spawned)
		if(dead) {
			addMeToToDeleteTwo(this);
		}
		drawLines();
	}

	void drawLines() {
		  float scalar = myTimer.progress();
		  for(PVector p : list) {
			  stroke(f.curColor, f.faderOpacity+0.2);
		      float cX = p.x;
		      float cY = p.y;
		      float cZ = p.z;
		      cX = scalar * (p.x - f.pos.x) + f.pos.x;
		      cY = scalar * (p.y - f.pos.y) + f.pos.y;
		      cZ = scalar * (p.z - f.pos.z) + f.pos.z;
		      line(cX, cY, cZ, f.pos.x, f.pos.y, f.pos.z);
	  	  }
	  	  if(scalar >= 1 && !spawned) {
	  	  	spawnlevs();
	  	  	spawned = true;
	  	  }
	}

	void spawnlevs() {
		//add a lev for each vector in list
		for(int i = 0; i < list.size(); i++) {
			playRandomPlant();
			//add a random lev
			int r = int(random(3));
			switch (r) {
				case 0: 
				levs.addSingleFlower(new FlowerA(list.get(i), soundStringsA[int(random(soundStringsA.length-1))], main));
				break;

				case 1: 
				levs.addSingleFlower(new FlowerB(list.get(i), soundStringsB[int(random(soundStringsB.length-1))], main));
				break;

				case 2: 
				levs.addSingleFlower(new FlowerC(list.get(i), soundStringsA[int(random(soundStringsA.length-1))], main));
				break;
				
				default:
				levs.addSingleFlower(new FlowerC(list.get(i), soundStringsA[int(random(soundStringsA.length-1))], main));
			}
		}
		dead = true;
	}
}