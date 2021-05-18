package camera.Haar;

public class MakeFeatures {

    int roundx;
    int roundy;

    public MakeFeatures(int roundx, int roundy) {

        this.roundx = roundx;
        this.roundy = roundy;
    }

    /**
     *
     */
    public Object[] createHaarPixel(int roundx, int roundy){
        //TODO - Complete the creation of haar features where you resize each one of the images

        //System.out.println("New set of features of sizes: "+ round+ "x" + (2*round)+ " and " + round+"x"+(3*round) + " and " + (2*round) + "x" + (2*round));

        // Type 1, black and white, 2 pixel. Which has 4 sub types: two horizontals and two verticals
        int[][] type1 = new int[1*roundx][2*roundy];
        for(int i = 0; i<type1.length; i++){
            for(int j = 0; j<type1[0].length/2; j++){
                type1[i][j] = 1;
            }
        }
        //System.out.println("Type 1: " + Arrays.deepToString(type1));

        /* Noticed that this one is not needed, simply a reverse
        // Type 2 - black, white, black pixel. Which has 2 sub types, vertical and horizontal
        int[][] type2 = new int[1*round][3*round];
        for(int i = 0; i<type2.length; i++){
            for(int j = type2[0].length/3; j<(2*type2[0].length/3); j++){
                type2[i][j] = 1;
            }
        }
        System.out.println("Type 2: " + Arrays.deepToString(type2));

         */


        // Type 3 - White, black, white. Which has 2 sub types, vertical and horizontal
        int[][] type3 = new int[1*roundx][3*roundy];
        for(int i = 0; i<type3.length; i++){
            for(int j = 0; j<type3[0].length/3; j++){
                type3[i][j] = 1;
            }
            for(int j = 2*(type3[0].length/3); j<type3[0].length; j++){
                type3[i][j] = 1;
            }
        }
        //System.out.println("Type 3: " + Arrays.deepToString(type3));



        // Type 4 - White/Black diagonal. Which has 2 sub types. white diagonal left and right
        int[][] type4 = new int[2*roundx][2*roundy];
        for(int i = 0; i<type4.length/2; i++){
            for(int j = 0; j<type4[0].length/2; j++){
                type4[i][j] = 1;
            }
        }
        for(int i = type4.length/2; i<type4.length; i++){
            for(int j = type4[0].length/2; j<type4[0].length; j++){
                type4[i][j] = 1;
            }
        }
        //System.out.println("Type 4: " + Arrays.deepToString(type4) + "\n");


        return new Object[]{type1, type3, type4};
    }
}
