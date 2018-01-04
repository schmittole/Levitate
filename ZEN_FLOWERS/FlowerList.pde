public class FlowerList {

  ArrayList<Plane> planes;

  PApplet myMain;

  Flower firstAdded;

  PVector myCenter;

  int activePlanes = 0;

  PVector conCol = new PVector(0, 0, 100);

  int totalSize;

  public FlowerList(PApplet main) {
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
      for(Flower f : p.getlevs()) {
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
      for(Flower f : p.getlevs()) {
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

  void addSingleFlower(Flower f) {
    //FlowerA
    if(f instanceof FlowerA) {
      planes.get(0).addFlower(f);
      for(Flower cur : planes.get(0).getlevs()) {
        cur.setMyPlane(planes.get(0));
      }
    }
    //FlowerB
    if(f instanceof FlowerB) {
      planes.get(1).addFlower(f);
      for(Flower cur : planes.get(1).getlevs()) {
        cur.setMyPlane(planes.get(1));
      }
    }
    //FlowerC
    if(f instanceof FlowerC) {
      planes.get(2).addFlower(f);
      for(Flower cur : planes.get(2).getlevs()) {
        cur.setMyPlane(planes.get(2));
      }
    }

    //FlowerD
    if(f instanceof FlowerD) {
      planes.get(3).addFlower(f);
      for(Flower cur : planes.get(3).getlevs()) {
        cur.setMyPlane(planes.get(3));
      }
    }

    //FlowerE
    if(f instanceof FlowerE) {
      planes.get(4).addFlower(f);
      for(Flower cur : planes.get(4).getlevs()) {
        cur.setMyPlane(planes.get(4));
      }
    }

    //FlowerF
    if(f instanceof FlowerF) {
      planes.get(5).addFlower(f);
      for(Flower cur : planes.get(5).getlevs()) {
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
      for(Flower f : p.getlevs()) {
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
      for(Flower f : plane.getlevs()) {
        f.resetHomeComingVector();
      }
    }
  }

  //checks if all levs are home
  boolean alllevsHome() {
    for(Plane plane : planes) {
      for(Flower f : plane.getlevs()) {
        if(!f.getHome()) {
          return false;
        }
      }
    }
      return true;
  }
}
