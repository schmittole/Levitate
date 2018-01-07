class Plane {
	
	ArrayList<Lev> mylevs;

  Lev toErase;

  PVector myCenter;

  PVector conCol = new PVector(0, 0, 100);

  boolean oneIsTagged = false;

	public Plane() {
		mylevs = new ArrayList<Lev>();
    myCenter = new PVector();
	}

  void setToErase(Lev f) {
    toErase = f;
  }

	void addLev(Lev f) {
		mylevs.add(f);
	}	

	public ArrayList<Lev> getlevs() {
		return mylevs;
	}

  void add(Lev f) {
    mylevs.add(f);
  }

  boolean isAlive() {
    return mylevs.size() > 0;
  }

  void removeToErase() {
    if(toErase != null) {
      mylevs.remove(toErase);
      toErase = null;
    }
  }

	public void render() {
    myCenter = new PVector();

    if(mylevs.size() < 2) {
      oneIsTagged = false;
    }
      //camera(width/2.0, height/2.0, (height/2.0) / tan(PI*30.0 / 180.0)*2, width/2.0, height/2.0, 0, 0, 1, 0);    
      for(Lev f : mylevs) {
        switch (state) {
          //levitating in order (starting positions)
          case (IN_ORDER) :
          if(f.homeComingVector != null) {
            f.resetHomeComingVector();
          }
            f.detect(curser.pos);
            f.render();
            break;

          //levitating freely. wandering.  
          case (WANDERING) :
          if(f.homeComingVector != null) {
            f.resetHomeComingVector();
          }
            f.move();
            f.render();
            boolean curCouldTag = f.detect(curser.pos);
            if(curCouldTag) {
              couldTag = true;
            }

            break;

          //lev back to starting position
          case (GO_HOME) :
            f.goHome();
            f.render();
            if(lastState != state) {
              playSwoosh();
            }
          default :
        }

        myCenter.x += f.getPos().x;
        myCenter.y += f.getPos().y;
        myCenter.z += f.getPos().z;

      }
	}
}