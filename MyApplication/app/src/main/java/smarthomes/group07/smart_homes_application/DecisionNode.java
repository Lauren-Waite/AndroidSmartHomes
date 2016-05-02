package smarthomes.group07.smart_homes_application;

/**
 * Created by laurenwaite on 5/1/16.
 */
public class DecisionNode extends DecisionTreeNode {
    private String feature;
    private float threshold;
    private String comparator;

    public DecisionNode(String f, float t, String c) {
        feature = f;
        threshold = t;
        comparator = c;
    }

    public String getNodeName() {
        return feature;
    }
    public float getThreshold() {
        return threshold;
    }

    public DecisionTreeNode analyzeNode(float v) {
        if(comparator.equals("<=")) {
            if(v <= threshold) {
                return left;
            } else {
                return right;
            }
        }
        if(comparator.equals("<")) {
            if(v < threshold) {
                return left;
            } else {
                return right;
            }
        }
        if(comparator.equals(">")) {
            if(v > threshold) {
                return left;
            } else {
                return right;
            }
        }
        if(comparator.equals(">=")) {
            if(v >= threshold) {
                return left;
            } else {
                return right;
            }
        }
        return null;
    }

}