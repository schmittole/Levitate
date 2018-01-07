public class LevList {

  ArrayList<Plane> planes;

  PApplet myMain;

  Lev firstAdded;

  PVector myCenter;

  int activePlanes = 0;

  PVector conCol = new PVector(0, 0, 100);

  int totalSize;

  public LevList(PApplet main) {
    myCenter = new PVector();
    planes = new ArrayList<Plane>();
    for(int i = 0; i < 6; i++) {
      planes.add(new Plane());
    }
    myMain = main;
  }

  boolean totalSizeBiggerOne() {
    int size = 0;
    for(Plane p : planes) {
      for(Lev f : p.getlevs()) {
        size++;
        if(size > 1) {
          return true;
        }
      }
    }
    return false;
  }

  int getTotalSize() {
    int size = 0;
    for(Plane p : planes) {
      for(Lev f : p.getlevs()) {
        size++;
      }
    }
    return size;
  }

  public ArrayList<Plane> getPlanes() {
    return planes;
  }

  public void set(int i, Plane p) {
    planes.set(i, p);
  }

  void addSingleLev(Lev f) {
    //LevA
    if(f instanceof LevA) {
      planes.get(0).addLev(f);
      for(Lev cur : planes.get(0).getlevs()) {
        cur.setMyPlane(planes.get(0));
      }
    }
    //LevB
    if(f instanceof LevB) {
      planes.get(1).addLev(f);
      for(Lev cur : planes.get(1).getlevs()) {
        cur.setMyPlane(planes.get(1));
      }
    }
    //LevC
    if(f instanceof LevC) {
      planes.get(2).addLev(f);
      for(Lev cur : planes.get(2).getlevs()) {
        cur.setMyPlane(planes.get(2));
      }
    }

    //LevD
    if(f instanceof LevD) {
      planes.get(3).addLev(f);
      for(Lev cur : planes.get(3).getlevs()) {
        cur.setMyPlane(planes.get(3));
      }
    }

    //LevE
    if(f instanceof LevE) {
      planes.get(4).addLev(f);
      for(Lev cur : planes.get(4).getlevs()) {
        cur.setMyPlane(planes.get(4));
      }
    }

    //LevF
    if(f instanceof LevF) {
      planes.get(5).addLev(f);
      for(Lev cur : planes.get(5).getlevs()) {
        cur.setMyPlane(planes.get(5));
      }
    }
  }

  void addPlane(Plane p) {
    planes.add(p);
  }

  void setTotalSize() {
    totalSize = 0;
    for(int i = 0; i < 3; i++) {
      Plane p = planes.get(i);
      for(Lev f : p.getlevs()) {
        totalSize++;
      }
    }
  }

  void run() {
    setTotalSize();
    myCenter = new PVector();

  for(Plane p : planes) {
    p.removeToErase();
  }

  activePlanes = 0;
  for(Plane p : planes) {
    p.render();
      if(p.isAlive()) {
      myCenter.x += p.myCenter.x;
      myCenter.y += p.myCenter.y;
      myCenter.z += p.myCenter.z;
      activePlanes++;
      }
    }
  }

  //draws a line from center to center
  void drawFromListCenterToEachPlane() {

    myCenter.x = myCenter.x / activePlanes;
    myCenter.y = myCenter.y / activePlanes;
    myCenter.z = myCenter.z / activePlanes;

    for(Plane f : planes) {
        stroke(conCol.x, conCol.y, conCol.z, 90);
        line(f.myCenter.x, f.myCenter.y, f.myCenter.z, myCenter.x, myCenter.y, myCenter.z);
    }
  }

  void resetAllHomeComingVectors() {
    for(Plane plane : planes) {
      for(Lev f : plane.getlevs()) {
        f.resetHomeComingVector();
      }
    }
  }

  //checks if all levs are home
  boolean alllevsHome() {
    for(Plane plane : planes) {
      for(Lev f : plane.getlevs()) {
        if(!f.getHome()) {
          return false;
        }
      }
    }
      return true;
  }
}
