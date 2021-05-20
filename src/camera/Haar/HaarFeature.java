package camera.Haar;

public class HaarFeature {
    public int[][] matrix;
    public int x_scalar;
    public int y_scalar;
    public int feature_type; // 1,2,3,4,5
    public int[] coords;

    public int x;
    public int y;

    public HaarFeature(int[][] matrix, int feature_type, int[] coords, int x_scalar, int y_scalar){
        this.matrix = matrix;
        this.feature_type = feature_type;
        this.coords = coords;
        this.x_scalar = x_scalar;
        this.y_scalar = y_scalar;

        this.x = coords[0];
        this.y = coords[1];
    }

    public HaarFeature(int feature_type, int x_scalar, int y_scalar, int x_pos, int y_pos){
        this.feature_type = feature_type;
        this.x_scalar = x_scalar;
        this.y_scalar = y_scalar;
        this.x = x_pos;
        this.y = y_pos;
        this.matrix = createMatrix(feature_type, x_scalar, y_scalar);
    }

    public int[][] createMatrix(int type, int x_s, int y_s){
        switch(type){
            case 1:
                int[][] type1 = new int[x_s][2*y_s];
                for(int i = 0; i<type1.length; i++){
                    for(int j = 0; j<type1[0].length/2; j++){
                        type1[i][j] = 1;
                    }
                }
                return type1;
            case 2:
                int[][] type2 = new int[x_s][2*y_s];
                for(int i = 0; i<type2.length; i++){
                    for(int j = 0; j<type2[0].length/2; j++){
                        type2[i][j] = 1;
                    }
                }
                type2 = rotateFeature(type2, 1);
                return type2;
            case 3:
                int[][] type3 = new int[x_s][3*y_s];
                for(int i = 0; i<type3.length; i++){
                    for(int j = 0; j<type3[0].length/3; j++){
                        type3[i][j] = 1;
                    }
                    for(int j = 2*(type3[0].length/3); j<type3[0].length; j++){
                        type3[i][j] = 1;
                    }
                }
                return type3;
            case 4:
                int[][] type4 = new int[x_s][3*y_s];
                for(int i = 0; i<type4.length; i++){
                    for(int j = 0; j<type4[0].length/3; j++){
                        type4[i][j] = 1;
                    }
                    for(int j = 2*(type4[0].length/3); j<type4[0].length; j++){
                        type4[i][j] = 1;
                    }
                }
                type4 = rotateFeature(type4, 1);
                return type4;
            case 5:
                int[][] type5 = new int[2*x_s][2*y_s];
                for(int i = 0; i<type5.length/2; i++){
                    for(int j = 0; j<type5[0].length/2; j++){
                        type5[i][j] = 1;
                    }
                }
                for(int i = type5.length/2; i<type5.length; i++){
                    for(int j = type5[0].length/2; j<type5[0].length; j++){
                        type5[i][j] = 1;
                    }
                }
                return type5;
        }
        return null;
    }

    /**
     * Serves to rotate the feature a certain amount of times
     * @param rotateIt
     * @param angle
     * @return
     */
    public int[][] rotateFeature(int[][] rotateIt, int angle){

        int[][] rotatedOne = null;

        for(int k=0; k<angle; k++){

            int height = rotateIt.length;
            int width = rotateIt[0].length;
            rotatedOne = new int[width][height];

            for(int i = 0 ; i < width; i++){
                for(int j = 0 ; j < height; j++){
                    //rotatedOne[i][j] = rotateIt[height-j-1][i];
                    rotatedOne[i][j] = rotateIt[j][i];

                }
            }
            rotateIt = rotatedOne;
        }

        return rotatedOne;
    }
}
