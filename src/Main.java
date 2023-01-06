import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

class Main {
    static float[] x = new float[22];
    static float[] z = new float[11];
    static float[] y = new float[3];

    static float[] t = new float[3];

    static float[] ZNI = new float[11];
    static float[] YNI = new float[3];

    static float[] Sk = new float[3];
    static float[] Sj = new float[11];

    static float[] D = new float[11];


    static float[][] v = new float[22][11];
    static float[][] w = new float[11][3];

    static float[][] oldv = new float[22][11];
    static float[][] oldw = new float[11][3];

    static float[][] changeV = new float[22][11];
    static float[][] changeW = new float[11][3];


    static float a = (float) 1;

    static void setChangeW() {
        for (int i = 0; i < 11; i++)
            for (int j = 0; j < 3; j++)
                changeW[i][j] = a * Sk[j] * z[i];

    }

    static void setChangeV() {
        for (int i = 0; i < 22; i++)
            for (int j = 1; j < 11; j++)
                changeV[i][j] = a * Sj[j] * x[i];
    }

    static int updateW() {

        int count = 0;
        float wOld = 0;
        float wNew = 0;

        for (int i = 0; i < 11; i++)
            for (int j = 0; j < 3; j++) {
                wOld = w[i][j];
                wNew = wOld + changeW[i][j];
                w[i][j] = wNew;

                if (wNew == wOld)
                    count++;
            }
        return count;

    }

    static int updateV() {
        int count = 0;

        float vOld = 0;
        float vNew = 0;

        for (int i = 0; i < 22; i++)
            for (int j = 1; j < 11; j++) {
                vOld = v[i][j];
                vNew = vOld + changeV[i][j];
                v[i][j] = vNew;

                if (vOld == vNew)
                    count++;
            }
        return count;
    }

    static float DF(float num) {

        return F(num) * (1 - F(num));
    }

    static void setD() {
        for (int j = 0; j < 11; j++)
            for (int k = 0; k < 3; k++)
                D[j] = Sk[k] * w[j][k];
    }

    static int validationCheck = 0;
    static int n = 0;
    static boolean change = true;

    static void validation() {
        validationCheck = 0;

        try {
            String st;
            BufferedReader br = new BufferedReader(new FileReader("validation.txt"));

            while ((st = br.readLine()) != null) {

                StringTokenizer tokenizer = new StringTokenizer(st, " ");

                for (int i = 1; i < 22; i++)
                    x[i] = Float.parseFloat(tokenizer.nextToken());

                float target = Float.parseFloat(tokenizer.nextToken());

                for (int i = 1; i < 11; i++)
                    for (int j = 0; j < 22; j++)
                        ZNI[i] = x[j] * v[j][i];

                for (int i = 1; i < 11; i++)
                    z[i] = F(ZNI[i]);

                for (int i = 0; i < 11; i++)
                    for (int j = 0; j < 3; j++)
                        YNI[j] = z[i] * w[i][j];

                for (int i = 0; i < 3; i++)
                    y[i] = F(YNI[i]);


                if (target == 1 && y[0] == 1 && y[1] == 0 && y[2] == 0) {
                    validationCheck++;

                } else if (target == 2 && y[0] == 0 && y[1] == 1 && y[2] == 0) {
                    validationCheck++;

                } else if (target == 3 && y[0] == 0 && y[1] == 0 && y[2] == 1) {
                    validationCheck++;

                } else {
                    validationCheck = 0;

                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void setYNI() {

        for (int k = 0; k < 3; k++)
            for (int j = 0; j < 11; j++)
                YNI[k] += z[j] * w[j][k];
    }

    static int nnnnn = 0;

    static void MLP() {
        String st;
        try {
            BufferedReader br = new BufferedReader(new FileReader("train.txt"));

            float target;

            while ((st = br.readLine()) != null) {

                // step 4
                StringTokenizer tk = new StringTokenizer(st, " ");
                for (int i = 1; i < 22; i++)
                    x[i] = Float.parseFloat(tk.nextToken());

                target = Float.parseFloat(tk.nextToken());
                setTarget(target);


                // step 5
                setZNI();
                setZ();


                // step 6
                setYNI();
                setY();


                // step 7
                setSk();
                setChangeW();

                // step 8
                setD();
                setSj();
                setChangeV();

                // step 9

                int upW = updateW();
                int upV = updateV();
                if (upW == 33 && upV == 220)
                    change = false;


                nnnnn++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void setSj() {
        for (int i = 1; i < 11; i++)
            Sj[i] = D[i] * DF(ZNI[i]);
    }

    private static void setSk() {
        for (int i = 0; i < 3; i++)
            Sk[i] = (t[i] - y[i]) * DF(YNI[i]);
    }

    private static void setY() {
        for (int j = 0; j < 3; j++)
            y[j] = F(YNI[j]);
    }


    static void Epochs() {

        while (validationCheck < 6) {
            MLP();

            for (int i = 0; i < 11; i++)
                System.out.println(Arrays.toString(w[i]));

            for (int i = 0; i < 22; i++)
                System.out.println(Arrays.toString(v[i]));


            validation();
        }
    }

    static float test() {

        int number = 0;
        int correct = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader("test.txt"));


            float target = 0;
            String st;
            while ((st = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(st, " ");

                for (int i = 1; i < 22; i++)
                    x[i] = Float.parseFloat(tokenizer.nextToken());

                target = Float.parseFloat(tokenizer.nextToken());


                for (int i = 1; i < 11; i++)
                    for (int j = 0; j < 22; j++)
                        ZNI[i] = x[j] * v[j][i];

                for (int i = 1; i < 11; i++)
                    z[i] = F(ZNI[i]);

                for (int i = 0; i < 11; i++)
                    for (int j = 0; j < 3; j++)
                        YNI[j] = z[i] * w[i][j];

                for (int i = 0; i < 3; i++)
                    y[i] = F(YNI[i]);


                if (target == 1 && y[0] == 1 && y[1] == 0 && y[2] == 0)
                    correct++;

                else if (target == 2 && y[0] == 0 && y[1] == 1 && y[2] == 0)
                    correct++;

                else if (target == 3 && y[0] == 0 && y[1] == 0 && y[2] == 1)
                    correct++;

                number++;


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (float) (correct / number);
    }


    public static void main(String[] args) {

        x[0] = 1;
        z[0] = 1;

        // step 1
        setW();
        setV();

        // step to 9
        //
        Epochs();

        System.out.println(test());

        System.out.println(nnnnn);


    }

    static void setW() {


        for (int i = 0; i < 11; i++)
            for (int j = 0; j < 3; j++)
                w[i][j] = (float) Math.random();


    }

    static void setV() {
        for (int i = 0; i < 22; i++)
            for (int j = 1; j < 11; j++)
                v[i][j] = (float) Math.random();
    }

    static void setZNI() {
        for (int j = 1; j < 11; j++) {
            for (int k = 0; k < 22; k++)
                ZNI[j] += x[k] * v[k][j];
        }
    }

    static void setTarget(float target) {

        if (target == 1) {
            t[0] = 1;
            t[1] = 0;
            t[2] = 0;
        } else if (target == 2) {
            t[0] = 0;
            t[1] = 1;
            t[2] = 0;
        } else if (target == 3) {
            t[0] = 0;
            t[1] = 0;
            t[2] = 1;
        }

    }

    private static void setZ() {
        for (int j = 1; j <= 10; j++)
            z[j] = F(ZNI[j]);
    }

    static float F(float num) {

        return (float) (1 / (1 + (Math.exp(-1 * num))));
    }
}