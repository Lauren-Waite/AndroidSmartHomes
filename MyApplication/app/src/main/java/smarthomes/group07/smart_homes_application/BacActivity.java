package smarthomes.group07.smart_homes_application;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class BacActivity extends Activity implements SensorEventListener {
    private static String[] mFeatureRef = {"mean_x", "mean_y", "mean_z", "std_x", "std_y", "std_z", "med_x", "med_y", "med_z", "rms_x", "rms_y", "rms_z"};
    private TextView mShotTextView;
    private TextView mBacTextView;
    SharedPreferences preferences;
    DecisionTreeNode mRoot;

    int myWeight;
    double genderConstant;

    private SensorManager mSensorManager;
    private Sensor mAccSensor;
    ArrayList<Float> mxValues = new ArrayList<Float>();
    ArrayList<Float> myValues = new ArrayList<Float>();
    ArrayList<Float> mzValues = new ArrayList<Float>();
    ArrayList<Long> mtimeValues = new ArrayList<Long>();

    boolean isFirst = true;
    long mEndTime;
    long mStartTime;
    DecimalFormat mDf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_bac);

        preferences = this.getSharedPreferences("PROFILE_PREF", Context.MODE_PRIVATE);

        myWeight = preferences.getInt("weight", 0);
        genderConstant = 0.0;
        if (preferences.getInt("gender", 0) == 1) {
            // Female
            genderConstant = 0.55;
        } else if (preferences.getInt("gender", 0) == 2) {
            // Male
            genderConstant = 0.68;
        }

        mDf = new DecimalFormat("0.000");
        buildTree();
    }


    protected void onStart() {
        super.onStart();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void returnToHome(View view) {
        finish();
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Blank interface method
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        long timestamp_seconds = event.timestamp / 1000000000;

        if(isFirst) {
            isFirst = false;
            mEndTime = timestamp_seconds + 3;
            mStartTime = timestamp_seconds;
        }
        mxValues.add(event.values[0]);
        myValues.add(event.values[1]);
        mzValues.add(event.values[2]);
        mtimeValues.add(timestamp_seconds);

        if(timestamp_seconds == mEndTime) {
            extractAndClassify();
            mxValues.clear();
            myValues.clear();
            mzValues.clear();
            mtimeValues.clear();
            mEndTime = mEndTime + 3;
        }
    }

    private void extractAndClassify() {
        ArrayList<Float> features = new ArrayList<Float>();
        features.add(calcMean(mxValues));
        features.add(calcMean(myValues));
        features.add(calcMean(mzValues));
        features.add(calcStd(mxValues, features.get(0)));
        features.add(calcStd(myValues, features.get(1)));
        features.add(calcStd(mzValues, features.get(2)));
        features.add(calcMed(mxValues));
        features.add(calcMed(myValues));
        features.add(calcMed(mzValues));
        features.add(calcRms(mxValues));
        features.add(calcRms(myValues));
        features.add(calcRms(mzValues));

        mShotTextView = (TextView) findViewById(R.id.shot_count);
        int currcount = Integer.parseInt(mShotTextView.getText().toString());
        mBacTextView = (TextView) findViewById(R.id.bac_value);
        if(classify(features, mRoot)) {
            currcount++;
            mShotTextView.setText(Integer.toString(currcount));
        }

        float bac = calcBAC(currcount);
        if(bac > 0) {
            mBacTextView.setText(mDf.format(bac));
        }
    }


    private boolean classify(ArrayList<Float> features, DecisionTreeNode node) {
        if(node instanceof ClassificationNode) {
            if(node.getNodeName().equals("taking_shot")) {
                return true;
            }
            return false;

        }
        String feature = node.getNodeName();
        int i = 0;
        while(i < mFeatureRef.length && !feature.equals(mFeatureRef[i])) {
            i++;
        }
        float value = features.get(i);
        DecisionTreeNode nextNode = node.analyzeNode(value);
        return classify(features, nextNode);
    }



    private void buildTree() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("DecisionTree.txt")));

            String line = reader.readLine().trim();
            String[] lineArray = line.split("\\s+");
            String featureName = lineArray[0];
            String comparator = lineArray[1];
            String threshString = lineArray[2];
            if(threshString.contains(":")) {
                threshString = threshString.substring(0, threshString.length()-1);
            }
            float threshold = Float.parseFloat(threshString);

            mRoot = new DecisionNode(featureName, threshold, comparator);

            if(3 < lineArray.length) {
                String classification = lineArray[3];
                mRoot.setLeft(new ClassificationNode(classification));
            }

            recBuildTree(reader, 0, mRoot);

        } catch (IOException e) {
            System.out.println("FAIL");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

    }

    // RECURSIVE TREE BUILDING FUNCTION.
    public static void recBuildTree(BufferedReader reader, int level, DecisionTreeNode curNode) throws IOException {
        if((curNode instanceof ClassificationNode) || (curNode.hasTwoChildren())) {
            return;
        }

        String line = reader.readLine();
        if(line == null) {
            return;
        } else {
            line = line.trim();
        }
        String[] lineArray = line.split("\\s+");

        int i = 0;
        while(lineArray[i].equals("|")) {
            i++;
        }
        String featureName = lineArray[i];

        if(curNode.getNodeName().equals(featureName) && level == i) {
            if(i+3 < lineArray.length) {
                String classification = lineArray[i+3];
                ClassificationNode newClass = new ClassificationNode(classification);

                if(!curNode.hasLeft()) {
                    curNode.setLeft(newClass);
                } else if (!curNode.hasRight()) {
                    curNode.setRight(newClass);
                }
                return;
            }

            line = reader.readLine().trim();
            lineArray = line.split("\\s+");

            i = 0;
            while(lineArray[i].equals("|")) {
                i++;
            }
            featureName = lineArray[i];
        }

        String comparator = lineArray[i+1];
        String threshString = lineArray[i+2];
        if(threshString.contains(":")) {
            threshString = threshString.substring(0, threshString.length()-1);
        }
        float threshold = Float.parseFloat(threshString);
        DecisionNode newNode = new DecisionNode(featureName, threshold, comparator);

        if(!curNode.hasLeft()) {
            curNode.setLeft(newNode);
        } else if(!curNode.hasRight()) {
            curNode.setRight(newNode);
        }

        // IF IT HAS a classification
        if(i+3 < lineArray.length) {
            String classification = lineArray[i+3];
            ClassificationNode newClass = new ClassificationNode(classification);

            if(!newNode.hasLeft()) {
                newNode.setLeft(newClass);
            } else if (!newNode.hasRight()) {
                newNode.setRight(newClass);
            }
        }

        if(curNode.hasLeft() && curNode.getLeft() instanceof DecisionNode) {
            recBuildTree(reader, i, newNode);
            recBuildTree(reader, level, curNode);
        } else if(curNode.hasRight() && curNode.getRight() instanceof DecisionNode) {
            recBuildTree(reader, i, newNode);
        }
    }

    // Calculate the mean of an ArrayList of floats
    public static float calcMean(ArrayList<Float> values) {
        float sum = 0;
        for(int i = 0; i < values.size(); i++) {
            sum += values.get(i);
        }
        return sum / values.size();
    }
    // Calculate the std of an ArrayList of floats given the mean
    public static float calcStd(ArrayList<Float> values, float mean) {
        float running_sum = 0;
        for(int i = 0; i < values.size(); i++) {
            float current_val = (float)Math.pow(values.get(i) - mean, 2);
            running_sum += current_val;
        }
        float std_avg = running_sum / values.size();
        return (float)Math.sqrt(std_avg);
    }
    // Calculate the median of an ArrayList of floats
    public static float calcMed(ArrayList<Float> values) {
        ArrayList<Float> sortedVals = new ArrayList<Float>();
        sortedVals.addAll(values);
        Collections.sort(sortedVals);

        int getIndex = sortedVals.size() / 2;

        if(sortedVals.size() % 2 == 1) {
            return sortedVals.get(getIndex);
        }

        float val1 = sortedVals.get(getIndex);
        float val2 = sortedVals.get(getIndex - 1);
        return (float)((val1 + val2) / 2.0);
    }
    // Calculate the RMS of an ArrayList of floats
    public static float calcRms(ArrayList<Float> values) {
        float running_sum = 0;
        for(int i = 0; i < values.size(); i++) {
            float current_val = (float)Math.pow(values.get(i), 2);
            running_sum += current_val;
        }
        float rms = (float)Math.sqrt(running_sum / values.size());
        return rms;
    }

    public  float calcBAC(int shotCount) {
        float weightGram = (float)myWeight * (float)453.592;
        float bac = 100 * ((shotCount*14)/(weightGram * (float)genderConstant));
        long timeElapsed =  mEndTime/3600 - mStartTime/3600;

        bac = bac - (float)(timeElapsed*.015);
        return bac;
    }
}
