class MouseController extends InteractionController {

    /*---------------------------------------------------------------
    Constructor
    ----------------------------------------------------------------*/

    MouseController(PApplet pa) {
        super();
    }


    /*---------------------------------------------------------------
    Functions
    ----------------------------------------------------------------*/

    void draw() {



            rightHandIndex.x = mouseX;
            rightHandIndex.y = mouseY;

            disableTagging();

            if (keyPressed) {

                if (key == 'e'||key == 'E') {
                    rightHandIndex.z -= 10;
                    if(rightHandIndex.z < lowerZ) { rightHandIndex.z = lowerZ; }
                }

                if (key == 'd'||key == 'D') {
                    rightHandIndex.z += 10;
                    if (rightHandIndex.z > upperZ) { rightHandIndex.z = upperZ; }
                }

                if (key == 't'||key == 'T') {
                    enableTagging();
                }

                if (key > '0' && key < '4') {
                    setState(key-48);
                }

                if (key == 'c' || key == 'C') {
                    drawC = true;
                }

                if (key == 'x' || key == 'x') {
                    drawC = false;
                }
            }

        super.draw();

    }

}
