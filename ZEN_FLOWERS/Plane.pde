class Plane {
	
	ArrayList<Flower> mylevs;

  Flower toErase;

  PVector myCenter;

  PVector conCol = new PVector(0, 0, 100);

  boolean oneIsTagged = false;

	public Plane() {
		mylevs = new ArrayList<Flower>();
    myCenter = new PVector();
	}

  void setToErase(Flower f) {
    toErase = f;
  }

	void addFlower(Flower f) {
		mylevs.add(f);
	}	

	public ArrayList<Flower> getlevs() {
		return mylevs;
	}

  void add(Flower f) {
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
      for(Flower f : mylevs) {
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

  //draws line from center of plane to each flower
  void drawFromPlaneCenterToEachFlower() {
    myCenter.x = myCenter.x / mylevs.size();
    myCenter.y = myCenter.y / mylevs.size();
    myCenter.z = myCenter.z / mylevs.size();

    for(Flower f : mylevs) {
      stroke(conCol.x, conCol.y, conCol.z, 50);
      line(f.getPos().x, f.getPos().y, f.getPos().z, myCenter.x, myCenter.y, myCenter.z);
    }
  }
}